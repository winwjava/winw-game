package winw.game.stock.strategy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Advise.Trading;
import winw.game.stock.analysis.Indicator;
import winw.game.stock.analysis.Signal;

/**
 * 趋势跟踪策略。
 * 
 * <p>
 * A trend can only be determined in hindsight, since at any time prices in the
 * future are not known.
 * 
 * <h2>MACD趋势分析</h2>
 * 
 * <p>
 * Since the MACD is based on moving averages, it is inherently a lagging
 * indicator. As a metric of price trends, the MACD is less useful for stocks
 * that are not trending (trading in a range) or are trading with erratic price
 * action.
 * <p>
 * 价格以趋势方式演变。
 * 
 * 信号线交叉。
 * <p>
 * 当MACD和平均线交叉时，发生“信号线交叉” 也就是说，当分歧（条形图）改变符号时。 如果MACD线穿过平均线（“看涨”交叉），
 * 或者如果它穿过平均线（“看跌”交叉点）下跌，则出售此类事件的标准解释。 这些事件被认为是股票趋势即将在交叉的方向加速的迹象。
 * 
 * <p>
 * 零交叉
 * <p>
 * 当MACD系列改变符号时，即发生“零交叉”事件，即MACD线穿过水平零轴。当价格系列的快速和慢速EMA之间没有差异时，就会发生这种情况。
 * 从正面到负面MACD的变化被解释为“看跌”，从负向正向为“看涨”。零交叉提供了趋势方向发生变化的证据，但是比信号线交叉更少地确认其动量。
 * 
 * <p>
 * ４、用50日均线的走向确定大势，以便确定自己的操作原则和所用指标及参数。
 * 当50日均线向上或基本走平时，市场强势或至少横盘整理，可以参与，当50日均线向下走时，市场处于弱势，不宜参与；
 * 用长期MACD指标来确认短期MACD指标的信号意义，当短周期MACD买入信号得到长周期MACD确认时，则买入信号更加可信（50日均线大势认可法则）。
 * 
 * <p>
 * 参考：https://www.douban.com/note/327926327/
 * 
 * @author winw
 *
 */
public class TrendFollowingStrategy implements Strategy {// TODO 成交量指标

	/**
	 * MACD信号线交叉（金叉、死叉）确定买入卖出，
	 */
	@Override
	public Advise analysis(List<Indicator> list) {
		Indicator today = list.get(list.size() - 1);
		if (today.getSignalList().contains(Signal.ZERO_CROSSOVER) || today.getSignalList().contains(Signal.GOLDEN_CROSSOVER)) {
			return new Advise(Trading.BUY_SIGNAL);
		}

		if (today.getSignalList().contains(Signal.DEATH_CROSSOVER)) {
			return new Advise(Trading.SELL_SIGNAL);
		}

		// 曲线拟合
		// GaussianCurveFitter
		// HarmonicCurveFitter
		// PolynomialCurveFitter
//		WeightedObservedPoints points = new WeightedObservedPoints();
//		for (int i = list.size() - 3; i < list.size(); i++) {
//			points.add(new Integer(i).doubleValue(), list.get(i).getClose());
//		}
//		PolynomialCurveFitter curveFitter = PolynomialCurveFitter.create(2);
//		System.out.println("PolynomialCurveFitter: " + curveFitter.fit(points.toList()));

		int index = list.size() - 3;
		// 当天发生金叉，今天计算斜率
		if (!today.getSignalList().contains(Signal.GOLDEN_CROSSOVER)) {// && !list.get(index + 1).getSignalList().contains(Signal.ZERO_CROSSOVER)
			return new Advise();
		}
		// 线性回归
		SimpleRegression regression = new SimpleRegression();
		double f = list.get(index).getClose();
		// System.out.println(today.getDate());
		for (int i = index; i < list.size(); i++) {
			regression.addData(f ++ , list.get(i).getClose());
			// System.out.println(list.get(i).getDate() +": "+f+", "+ list.get(i).getClose());
		}
		if (regression.getSlope() > 0.5) {
			// displays slope of regression line
			System.out.println(today.getCode() +"\t"+ list.get(index).getDate() + "~" + today.getDate() +", Slope: " + regression.getSlope());
			// return new Advise(Trading.BUY_SIGNAL);
		}

		// displays intercept of regression line
//		System.out.println("Intercept: " + regression.getIntercept());
		// displays slope standard error
//		System.out.println("SlopeStdErr: " + regression.getSlopeStdErr());
//		System.out.println("predict: " + regression.predict(1.5d));
		// displays predicted y value for x = 1.5

		// 成交量萎缩
		// if (today.getSignalList().contains(Signal.VOLUME_SHRINK)) {
		// return new Advise(Trading.SELL_SIGNAL);
		// }

		// if (!today.getSignalList().contains(Signal.VOLUME_ENLARGE)) {
		// return new Advise();
		// }

		// 金叉之后，成交量明显放大
		// 查看最近10天有无金叉，如果有金叉，并且成交量明显放大，则买入
		// ArrayList<Signal> signalList = new ArrayList<Signal>();
		// for (int i = 0; i <= 10; i++) {
		// signalList.addAll(list.get(list.size() - 1 - i).getSignalList());
		// }
		// if (signalList.contains(Signal.GOLDEN_CROSSOVER) &&
		// !signalList.contains(Signal.DEATH_CROSSOVER)) {
		// System.out.println(today.getCode() + "\t" + today.getDate() +
		// "\t金叉之后，成交量明显放大");
		// return new Advise(Trading.BUY_SIGNAL);
		// }

		return new Advise();
	}

	@Deprecated
	protected boolean isReliable(List<Indicator> list, Indicator today) {

		// TODO 在0轴以下反复交叉，不参与，只有零交叉时参与。

		List<Double> deaList = new ArrayList<Double>();

		for (int i = 50; i < list.size(); i++) {// 从第50开始，各种指标的误差可以忽略
			Indicator current = list.get(i - 1);
			Indicator yestday = list.get(i - 2);
			if (current.getMacd() > 0 && yestday.getMacd() < 0) {
				deaList.add(current.getDea());
			}
		}

		// for (int i = deaList.size(); i < deaList.size() - 3; i--) {
		// if (deaList.get(i) > 0 ) {
		// return true;
		// }
		// }
		// System.out.println(today.getDate() + " 在0轴以下反复交叉，不参与，只有零交叉时参与。");
		return false;
	}

	private boolean useReliable = false;

	@Deprecated
	protected boolean isReliable(double diff) {
		// BUY_DIFF BETWEEN -0.51 AND 0.23 OR BUY_DIFF BETWEEN 0.29 AND 0.84
		if (!useReliable) {
			return true;
		} else if (diff >= -0.51 && diff < 0.23) {
			return true;
		} else if (diff >= 0.29 && diff < 0.84) {
			return true;
		} else {
			return false;
		}
	}

}
