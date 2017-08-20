package winw.game.stock.strategy;

import java.text.DecimalFormat;
import java.util.List;

import org.junit.Test;

import winw.game.stock.QuoteType;
import winw.game.stock.StockQuote;
import winw.game.stock.StockQuoteService;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.analysis.Indicator;

public class TrendTradingStrategyTest {

	private StockQuoteService service = new TencentStockQuoteService();

	private TrendTradingStrategy strategy = new TrendTradingStrategy();

	@Test
	public void testAll() throws Exception {
		for (int i = 1; i < 700; i++) {
			try {
				test("sh" + (600000 + i));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		test("sz002714");
		test("sz002352");
	}

	@Test
	public void testOne() throws Exception {
		Portfolio portfolio = test("sh600516");

		// print trade Log
		for (Trade trade : portfolio.getTradeLog()) {
			System.out.println(trade.toString());
		}
	}

	DecimalFormat decimalFormat = new DecimalFormat("##0.00");

	public Portfolio test(String code) throws Exception {
		double init = 15000;

		StockQuote stockQuote = service.get(code);
		String name = stockQuote.getName();
		if (name.startsWith("S") || name.startsWith("ST") || name.startsWith("*ST") || name.startsWith("S*ST")
				|| name.startsWith("SST") || name.startsWith("退市")) {
			return null;
		}

		List<StockQuote> quoteList = service.get(stockQuote.getCode(), QuoteType.DAILY_QUOTE);

		Portfolio portfolio = new Portfolio(init);// 初始金额
		double profit = strategy.test(portfolio, Indicator.compute(quoteList));

		// print profit
		System.out.println(stockQuote.getName() + "\t" + decimalFormat.format(profit - init));
		return portfolio;
	}

}
