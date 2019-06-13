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
import winw.game.stock.StockQuoteService;
import winw.game.stock.TencentStockQuoteService;

public class StrategybacktestingTest {

	private StrategyBacktesting strategy = new TrendFollowingStrategy();
	private StockQuoteService service = new TencentStockQuoteService();

	private final double init = 1000000;

	private final NumberFormat percentFormat = new DecimalFormat("##.##%");

	@Before
	public void before() throws Exception {
		strategy.setStockQuoteService(service);
	}

	@Test
	public void testAll() throws Exception {
		int count = 0;
		double profit = 0;
		List<Portfolio> portfolios = new ArrayList<Portfolio>();
		for (String temp : StockList.CSI_300) {// "sh600436","sz000671", "sh600276",
												// "sh601877"//
			if (temp.startsWith("sz300")) {
				continue;
			}
			Portfolio portfolio = test(temp, "2017-10-15", "2019-01-01", 640);
			if (portfolio == null || portfolio.getTradeList().isEmpty()) {
				continue;
			}
			count++;
			profit += portfolio.getProfit();
			portfolios.add(portfolio);
		}
		System.out.println(profit);
		System.out.println(strategy.getClass().getSimpleName() + ", test: " + count + ", profit: "
				+ percentFormat.format(profit / (init * count)));

		Collections.sort(portfolios, Comparator.comparing(Portfolio::getProfit));
		Collections.reverse(portfolios);
		System.out.println("detail: ");
		for (int i = 0; i < portfolios.size(); i++) {
			Portfolio portfolio = portfolios.get(i);
			String code = portfolio.getTradeList().get(0).getCode();
			System.out.println(
					code + "\t" + service.get(code).getName() + "\t" + percentFormat.format(portfolio.getProfitRate()));
		}

	}

	@Test
	public void testOne() throws Exception {
		// '上证指数'=>'000001.SS',
		// '深圳成指'=>'399001.sz',
		// '香港恒生'=>'0011.hk',
		// '英国FTSE'=>'^FTSE',
		// '法国CAC'=>'^FCHI',
		// '德国DAX'=>'^GDAXI',
		// '道琼指数'=>'INDU',

		// '纳斯达克'=>'^IXIC',
		// strategy.testing("sh000300", "2017-10-15", "2019-06-09", 1116400);

		Portfolio portfolio = test("sh000300", "2017-10-15", "2019-06-01", 640);
		System.out.println("return: " + percentFormat.format(portfolio.getProfitRate()));
	}

	public Portfolio test(String code, String from, String to, int days) throws Exception {
		System.out.println("Backtesting: " + code + "\t" + from + " ~ " + to);

		Portfolio portfolio = new Portfolio(init);// 初始金额
		strategy.portfolio = portfolio;
		strategy.testing(code, from, to, days);
		return portfolio;
	}

}
