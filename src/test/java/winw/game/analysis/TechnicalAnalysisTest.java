package winw.game.analysis;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import winw.game.stock.Stock;
import winw.game.stock.StockQuote;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Advise.Signal;
import winw.game.stock.analysis.Indicators;
import winw.game.stock.analysis.TechnicalAnalysis;
import winw.game.stock.quant.trader.Trade;

public class TechnicalAnalysisTest {

	TechnicalAnalysis analysis = new TechnicalAnalysis();
	TencentStockQuoteService service = new TencentStockQuoteService();

	@Test
	public void test() throws IOException, ParseException {
		for (int i = 1; i < 500; i++) {
			try {
				analysis("sh" + (600000 + i));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		analysis("sz002714");
		analysis("sh600233");
		analysis("sz002120");
		analysis("sz002352");
	}

	private void analysis(String code) throws IOException, ParseException {
		Stock stock = service.getStock(code);
		List<StockQuote> quoteList = service.getHistoricalQuote(stock.getCode());
		List<Indicators> indicators = Indicators.compute(quoteList);

		boolean isHold = false; // 是否持仓
		List<Trade> tradeLog = new ArrayList<Trade>();
		for (int i = 30; i < indicators.size(); i++) {
			Indicators current = indicators.get(i);
			Advise advise = analysis.trend(stock, indicators.subList(0, i));
			if (advise.getSignal() == Signal.BUY_SIGNAL && !isHold) {
				isHold = true;
				tradeLog.add(new Trade(current.getDate(), current.getClose(), 100));
			}

			if (advise.getSignal() == Signal.SELL_SIGNAL && isHold) {
				isHold = false;
				tradeLog.add(new Trade(current.getDate(), current.getClose(), -100));
			}
		}

		// profit
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		System.out.println(stock.getName() + "\t"
				+ decimalFormat.format(Trade.profit(tradeLog, indicators.get(indicators.size() - 1).getClose())));
	}

}
