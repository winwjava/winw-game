package winw.game.stock.trading;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;

import winw.game.stock.Stock;
import winw.game.stock.StockQuote;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.analysis.Indicators;

public class TrendTradingStrategyTest {

	private TencentStockQuoteService service = new TencentStockQuoteService();

	private TrendTradingStrategy strategy = new TrendTradingStrategy();

	@Test
	public void test() throws IOException, ParseException {
		for (int i = 1; i < 100; i++) {
			try {
				testProfit("sh" + (600000 + i));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		testProfit("sz002714");
		testProfit("sh600233");
		testProfit("sz002120");
		testProfit("sz002352");
	}

	public void testProfit(String code) throws IOException, ParseException {
		Stock stock = service.getStock(code);
		List<StockQuote> quoteList = service.getHistoricalQuote(stock.getCode());

		List<Trade> tradeLog = strategy.test(Indicators.compute(quoteList));

		// profit
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		System.out.println(stock.getName() + "\t"
				+ decimalFormat.format(Trade.profit(tradeLog, quoteList.get(quoteList.size() - 1).getClose())));
	}

}
