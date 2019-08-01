package winw.game.quant.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import winw.game.quant.Portfolio;
import winw.game.quant.Quote;
import winw.game.quant.QuoteService;
import winw.game.quant.StockList;
import winw.game.quant.TencentQuoteService;

public class StrategybacktestingTest {

	private final double init = 1000000;

	private final NumberFormat percentFormat = new DecimalFormat("#.##%");

	@Test
	public void testOne() throws Exception {
		// '000300.SS'
		// '香港恒生'=>'0011.hk',
		// 新兴市场国家
		// '道琼指数'=>'INDU',
		// '纳斯达克'=>'^IXIC',
		// sh000300
		// YahooStockQuoteService TencentStockQuoteService
		TrendFollowingStrategy strategy = new TrendFollowingStrategy();
		strategy.backTesting(new Portfolio(init, 1, 0.07, 0.05), "2015-01-01", "2019-07-05");
	}

	@Test
	public void testAll() throws Exception {
		int count = 0;
		double profit = 0;
		List<Portfolio> portfolios = new ArrayList<Portfolio>();
		for (String temp : StockList.CSI_300) {
			if (temp.startsWith("sz300")) {
				continue;
			}
			Portfolio portfolio = new Portfolio(init, 1, 0.07, 0.05);
			TrendFollowingStrategy strategy = new TrendFollowingStrategy();
			strategy.backTesting(portfolio, "2018-01-01", "2019-06-22");
			if (portfolio == null || portfolio.getOrderList().isEmpty()) {
				continue;
			}
			count++;
			profit += portfolio.getReturn();
			portfolios.add(portfolio);
		}
		QuoteService quoteService = new TencentQuoteService();
		Collections.sort(portfolios, Comparator.comparing(Portfolio::getReturn));
		Collections.reverse(portfolios);
		System.out.println("test CSI_300: ");
		for (int i = 0; i < portfolios.size(); i++) {
			Portfolio portfolio = portfolios.get(i);
			String code = portfolio.getOrderList().get(0).getCode();
			Quote quote = quoteService.get(code);
			// "\t" + quote.getMarketVal()+ "/" + quote.getMarketCap() +
			System.out.println(code + "\t" + quote.getName() + "\t" + percentFormat.format(portfolio.getReturnRate()));
		}
		// System.out.println(profit);
		System.out.println(TrendFollowingStrategy.class.getSimpleName() + ", test: " + count + ", profit: "
				+ percentFormat.format(profit / (init * count)));
	}

}
