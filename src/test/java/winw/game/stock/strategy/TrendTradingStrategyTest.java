package winw.game.stock.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.junit.Test;

import winw.game.stock.QuoteType;
import winw.game.stock.StockList;
import winw.game.stock.StockQuote;
import winw.game.stock.StockQuoteService;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.analysis.Indicator;

public class TrendTradingStrategyTest {

	private StockQuoteService service = new TencentStockQuoteService();

	private TrendTradingStrategy strategy = new TrendTradingStrategy();

	@Test
	public void testAll() throws Exception {
		for (String temp : StockList.CSI_300) {
			test(temp);
		}
	}

	@Test
	public void testOne() throws Exception {
		Portfolio portfolio = test("sh600233");

		// print trade Log
		for (Trade trade : portfolio.getTradeLog()) {
			System.out.println(trade.toString());
		}
	}

	DecimalFormat decimalFormat = new DecimalFormat("##0.00");
	NumberFormat percentFormat = NumberFormat.getPercentInstance();

	public Portfolio test(String code) throws Exception {
		double init = 100000;

		StockQuote stockQuote = service.get(code);
		String name = stockQuote.getName();
		if (name.startsWith("S") || name.startsWith("ST") || name.startsWith("*ST") || name.startsWith("S*ST")
				|| name.startsWith("SST") || name.startsWith("退市")) {
			return null;
		}

		List<StockQuote> quoteList = service.get(stockQuote.getCode(), QuoteType.DAILY_QUOTE);

		Portfolio portfolio = new Portfolio(init);// 初始金额
		double assets = strategy.test(portfolio, Indicator.compute(quoteList));

		double profit = assets - init;
		percentFormat.setMinimumFractionDigits(2);

		System.out.println(stockQuote.getName() + "\t" + decimalFormat.format(profit) + "\t"
				+ percentFormat.format(profit / init));
		return portfolio;
	}

}
