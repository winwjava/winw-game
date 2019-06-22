package winw.game.stock.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import winw.game.stock.Portfolio;
import winw.game.stock.StockList;
import winw.game.stock.StockQuote;
import winw.game.stock.StockQuoteService;
import winw.game.stock.TencentStockQuoteService;

public class StrategybacktestingTest {

	// TrendFollowingStrategy GoldenCrossStrategy
	private StrategyBacktesting strategy = new TrendFollowingStrategy();

	// YahooStockQuoteService TencentStockQuoteService
	private StockQuoteService service = new TencentStockQuoteService();

	private final double init = 1000000;

	private final NumberFormat percentFormat = new DecimalFormat("#.##%");

	@Before
	public void before() throws Exception {
		strategy.setStockQuoteService(service);
	}

	@Test
	public void testOne() throws Exception {
		// '000300.SS'
		// '香港恒生'=>'0011.hk',
		// 新兴市场国家
		// '道琼指数'=>'INDU',
		// '纳斯达克'=>'^IXIC',
		// sh000300
		strategy.testing("sh000300", "2008-01-01", "2019-06-22", 12640, new Portfolio(init));
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
			Portfolio portfolio = strategy.testing(temp, "2018-01-01", "2019-06-22", 11640, new Portfolio(init));
			if (portfolio == null || portfolio.getTradeList().isEmpty()) {
				continue;
			}
			count++;
			profit += portfolio.getProfit();
			portfolios.add(portfolio);
		}

		Collections.sort(portfolios, Comparator.comparing(Portfolio::getProfit));
		Collections.reverse(portfolios);
		System.out.println("test CSI_300: ");
		for (int i = 0; i < portfolios.size(); i++) {
			Portfolio portfolio = portfolios.get(i);
			String code = portfolio.getTradeList().get(0).getCode();
			StockQuote quote = service.get(code);
			// "\t" + quote.getMarketVal()+ "/" + quote.getMarketCap() +
			System.out.println(code + "\t" + quote.getName() + "\t" + percentFormat.format(portfolio.getProfitRate()));
		}
		// System.out.println(profit);
		System.out.println(strategy.getClass().getSimpleName() + ", test: " + count + ", profit: "
				+ percentFormat.format(profit / (init * count)));
	}

}
