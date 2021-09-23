package winw.game.quant.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import winw.game.quant.Portfolio;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.Quote;
import winw.game.quant.QuoteIndex;

/**
 * 股票价格受市场信息所影响。买卖双方会综合各种信息进行交易，最后反映到价格。
 * 
 * <p>
 * 由于信息的传播必定有一个过程。但信息的影响不是让股票立马从一个价格达到另一个价格，大部分情况会有一个过程。而这个过程一定是符合力学规律（多种向上的动力和多种向下的动力）
 * 
 * 可从最近的拐点开始拟合，做线性回归，得到线性函数，预测未来的走势和价格。
 * 
 * 
 * <p>
 * 这个策略赚取的利润来自：信息产生的原动力的从开始到动力耗尽。
 * 
 * <p>
 * 另考虑使用强化学习，总结规律，避免某些坑，提高胜率。
 * 
 * @author winw
 *
 */
public class DynamicAnalysisStrategy extends QuantTradingStrategy {

	// TODO 考虑用JavaScript 支持编写函数/公式；返回是否交易；
	
	// 默认用国债和300二八轮换
	public DynamicAnalysisStrategy() {
		this.samples.addAll(Arrays.asList(CSI_300_TOP));
	}

	@Override
	public void trading(Portfolio portfolio) {
		for (String code : samples()) {
			// 先计算最近一个拐点。
			int turningPoint = 0;
			QuoteIndex today = getQuoteIndex(code, 0);
			List<QuoteIndex> list = getHistoryQuote(code);
			List<Double> colseList = new ArrayList<>();
			for (int j = list.size() - 1; j > 0; j--) {

				// TODO 考虑用加权平均。
				double t0 = list.get(j).getMa5();
				double tn1 = list.get(j - 1).getMa5();
				double tn2 = list.get(j - 2).getMa5();

				colseList.add(t0);
				// 如果t-2 > t-1 < t0 向上拐点
				if (tn2 > tn1 && tn1 < t0) {
					turningPoint = j;
					break;
				}
				// 如果t-2 < t-1 > t0则向下拐点。
				if (tn2 < tn1 && tn1 > t0) {
					turningPoint = -j;
					break;
				}
			}
//			System.out.println(turningPoint + " "+colseList);
			// 向上情况，判断函数形态：冥函数（k*x 或 k*x*x）、指数函数（a的x次方）？

			PolynomialCurveFitter curveFitter = PolynomialCurveFitter.create(2);
			ArrayList<WeightedObservedPoint> points = new ArrayList<WeightedObservedPoint>();

			if (turningPoint < 0) {
//				System.out.println(turningPoint);
//				portfolio.addBatch(today, -1, String.format("SlopeL: %.2f", today.getL()));
				continue;
			}
			double xInterval5 = list.get(turningPoint).getMa5() * 0.15;
			for (int j = turningPoint, k = 0; j < list.size(); j++ , k++) {
				points.add(new WeightedObservedPoint(1, k * xInterval5, list.get(j).getClose()));
				System.out.println(k* xInterval5+", "+ list.get(j).getClose());
				
			}
			double[] fit = curveFitter.fit(points);
			System.out.println("fit: f(x) = "+ fit[0] + "*x*x + "+fit[1]+"*x + " + fit[2]);
			// TODO 买入日期 在拐点之前，则应该抛掉。

			// TODO 偏离趋势，向下？ 应该抛掉？

			// 5/10/20均线是否全部需要向上？
			// 从转折点买入，避免从顶部买入。
			if (portfolio.getEmptyPositionDays(today.getCode(), 100) > 2 // 卖出后保持空仓天数
					&& !portfolio.hasPosition(today.getCode())) {
				portfolio.addBatch(today, 1, String.format("SlopeL: %.2f", today.getL()));
			}

		}
		stoploss(portfolio);
		portfolio.commitBatch();
	}

	public static void main(String[] args) throws Exception {
		Portfolio portfolio = new Portfolio(1000000, 1, 0.05, 0.05);
		DynamicAnalysisStrategy strategy = new DynamicAnalysisStrategy();
		strategy.backTesting(portfolio, "2021-01-01", Quote.today());
//		strategy.backTesting(portfolio, "2020-01-01", Quote.today());
//		QuotePanel.show(portfolio, strategy, "2019-12-01", Quote.today());
	}

	public static void main0(String[] args) {
		SimpleRegression regression = new SimpleRegression();
		double[] points60 = new double[] { 16.2441, 16.2296, 16.2048, 16.2129, 16.2746, 16.3092 };
		double xInterval5 = points60[5] * 0.15;
		for (int j = 0; j <= 5; j++) {
			regression.addData(j * xInterval5, points60[j]);
		}
	}

	public static void main1(String[] args) {
		PolynomialCurveFitter curveFitter = PolynomialCurveFitter.create(2);
		double[] points60 = new double[] { 16.2441, 16.2296, 16.2048, 16.2129, 16.2746, 16.3092 };
		double xInterval5 = points60[5] * 0.15;

		ArrayList<WeightedObservedPoint> list = new ArrayList<WeightedObservedPoint>();

		for (int j = 0; j <= 5; j++) {
//			regression.addData(j * xInterval5, points60[j]);

			list.add(new WeightedObservedPoint(1, j * xInterval5, points60[j]));
		}
		double[] fit = curveFitter.fit(list);
		for (double d : fit) {
			System.out.println(d);
		}
		// y = k * x*x + x + m;
	}

}
