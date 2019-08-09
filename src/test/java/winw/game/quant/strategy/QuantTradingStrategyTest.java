package winw.game.quant.strategy;

import java.util.List;

import winw.game.quant.Order;
import winw.game.quant.Portfolio;
import winw.game.quant.QuantTradingStrategy;

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
		test("2017-01-01","2018-01-01");
		test("2018-01-01","2019-01-01");
		Portfolio portfolio = new Portfolio(init, 1, 0.1, 0.1);
		TrendFollowingStrategy trendFollowing = new TrendFollowingStrategy(QuantTradingStrategy.CSI_300_TOP);
		portfolio.setStoplossLimit(0.05);
		portfolio.setDrawdownLimit(0.05);
		trendFollowing.backTesting(portfolio, "2019-01-01", "2019-08-05");
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
		//QuoteChart.show(portfolio, from, to);
	}
}
