package winw.game.quant.strategy;

import java.util.List;

import winw.game.quant.Order;
import winw.game.quant.Portfolio;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.Quote;

/**
 * 测试结果表明，均值回归的收益远大于趋势跟踪。
 * <Point>
 * 所以应对以均值回归为主，在均值回归的过程中如果趋势向上，则可以改变策略继续跟踪。
 * 
 * @author winw
 *
 */
public class QuantTradingStrategyTest {
	private static double init = 1000000;
	// private String today = DateFormatUtils.format(new Date(),
	// QuoteService.DATE_PATTERN);

	public static List<Order> marketOrders(String from, String to) throws Exception {
		// 先用trendFollowing回测CSI_300，根据买入时段，调换策略。
		Portfolio portfolio = new Portfolio(init, 1, 0.05, 0.05);
		TrendFollowingStrategy strategy = new TrendFollowingStrategy(QuantTradingStrategy.CSI_300);
		strategy.backTesting(portfolio, from, to, false);
		return portfolio.getOrderList();
	}

	public static void main(String[] args) throws Exception {
		org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webDaemon", "-webPort", "8080").start();
		Thread.sleep(10000000);

		test("2017-01-01", "2018-01-01");
		test("2018-01-01", "2019-01-01");
//		test("2019-01-01", Quote.today());
		Portfolio portfolio = new Portfolio(init, 1, 0.1, 0.1);
		TrendFollowingStrategy trendFollowing = new TrendFollowingStrategy(QuantTradingStrategy.CSI_300_TOP);
		portfolio.setStoplossLimit(0.05);
		portfolio.setDrawdownLimit(0.05);
		trendFollowing.backTesting(portfolio, "2019-01-01", Quote.today());
	}

	public static void test(String from, String to) throws Exception {
		// 市场处于向上趋势，调整为趋势跟踪策略
		// 市场处于向下或震荡趋势，调整为均值回归策略
		Portfolio portfolio = new Portfolio(init, 1, 0.1, 0.1);
		MeanReversionStrategy meanReversion = new MeanReversionStrategy();
		TrendFollowingStrategy trendFollowing = new TrendFollowingStrategy(QuantTradingStrategy.CSI_300_TOP);
		int lastSize = 0;
		String lastDate = from;
		for (Order order : marketOrders(from, to)) {
			lastSize = order.getSize();
			if (lastSize < 0) {// 前面一段时间的趋势是向上的。
				portfolio.setStoplossLimit(0.05);
				portfolio.setDrawdownLimit(0.05);
				trendFollowing.backTesting(portfolio, lastDate, order.getDate(), false);
			} else {
				portfolio.setStoplossLimit(0.1);
				portfolio.setDrawdownLimit(0.1);
				meanReversion.backTesting(portfolio, lastDate, order.getDate(), false);
			}
			lastDate = order.getDate();
		}
		if (lastSize < 0) {//
			portfolio.setStoplossLimit(0.1);
			portfolio.setDrawdownLimit(0.1);
			meanReversion.backTesting(portfolio, lastDate, to);
		} else {
			portfolio.setStoplossLimit(0.05);
			portfolio.setDrawdownLimit(0.05);
			trendFollowing.backTesting(portfolio, lastDate, to);
		}
		// QuoteChart.show(portfolio, from, to);
	}
	
	// TODO 根据历史回测，确定用均值回归还是趋势跟踪。
}
