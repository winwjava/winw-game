package winw.game.stock.strategy;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;

import winw.game.stock.Stock;
import winw.game.stock.StockQuote;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.analysis.Indicators;
import winw.game.stock.strategy.Trade;
import winw.game.stock.strategy.TrendTradingStrategy;

public class TrendTradingStrategyTest {

	private TencentStockQuoteService service = new TencentStockQuoteService();

	private TrendTradingStrategy strategy = new TrendTradingStrategy();

	@Test
	public void test() throws IOException, ParseException {
		 for (int i = 1; i < 700; i++) {
		 try {
		 testProfit("sh" + (600000 + i));
		 } catch (Exception e) {
		 // e.printStackTrace();
		 }
		 }

		testProfit("sh600211");
		testProfit("sz002714");
		testProfit("sh600233");
		testProfit("sz002120");
		testProfit("sz002352");
	}

	DecimalFormat decimalFormat = new DecimalFormat("##0.00");

	public void testProfit(String code) throws IOException, ParseException {
		Stock stock = service.getStock(code);
		List<StockQuote> quoteList = service.getHistoricalQuote(stock.getCode());

		List<Trade> tradeLog = strategy.test(Indicators.compute(quoteList));

		// print tradeLog
//		System.out.println(stock.getName() + "\t" + tradeLog.toString());
		// print profit
		System.out.println(stock.getName() + "\t"
				+ decimalFormat.format(Trade.profit(tradeLog, quoteList.get(quoteList.size() - 1).getClose())));
	}

}
