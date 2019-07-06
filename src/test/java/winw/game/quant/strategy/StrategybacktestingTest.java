package winw.game.quant.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import winw.game.quant.Portfolio;
import winw.game.quant.StockQuote;
import winw.game.quant.StockQuoteService;
import winw.game.quant.TencentStockQuoteService;

public class StrategybacktestingTest {

	// TrendFollowingStrategy GoldenCrossStrategy
	private TrendFollowingStrategy strategy = new TrendFollowingStrategy();

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
		strategy.backtesting("2015-01-01", "2019-07-05", 12640, new Portfolio(init), "sh510300");
	}

	@Test
	public void testAll() throws Exception {
		String[] wight = new String[] { "sh601318", "sh600519", "sh600036", "sz000651", "sz000333", };

		// "sh601166",
		// "sz000858",
		// "sh600887",
		// "sh600276",
		// "sh601328"

		int count = 0;
		double profit = 0;
		List<Portfolio> portfolios = new ArrayList<Portfolio>();
		for (String temp : wight) {
			if (temp.startsWith("sz300")) {
				continue;
			}
			Portfolio portfolio = strategy.backtesting("2018-01-01", "2019-06-22", 11640, new Portfolio(init), temp);
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
