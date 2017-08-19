package winw.game.stock.strategy;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;

import winw.game.stock.QuoteType;
import winw.game.stock.StockQuote;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.analysis.Indicator;

public class TrendTradingStrategyTest {

	private TencentStockQuoteService service = new TencentStockQuoteService();

	private TrendTradingStrategy strategy = new TrendTradingStrategy();

	@Test
	public void test() throws IOException, ParseException {

		testProfit("sz002714");
		testProfit("sz002352");

		for (int i = 1; i < 100; i++) {
			try {
				testProfit("sh" + (600000 + i));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	DecimalFormat decimalFormat = new DecimalFormat("##0.00");

	public void testProfit(String code) throws IOException, ParseException {
		StockQuote stockQuote = service.get(code);
		List<StockQuote> quoteList = service.get(stockQuote.getCode(), QuoteType.DAILY_QUOTE);

		List<Trade> tradeLog = strategy.test(Indicator.compute(quoteList));

		double close = quoteList.get(quoteList.size() - 1).getClose();
		// print profit
		System.out.println(
				stockQuote.getName() + "\t" + decimalFormat.format(Trade.profit(tradeLog, close)) + "\t" + close * 500);

		// print trade Log
		// for(Trade trade: tradeLog) {
		// System.out.println(trade.toString());
		// }
	}

}
