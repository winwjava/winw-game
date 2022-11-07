package winw.game.quant.strategy;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import ij.measure.CurveFitter;
import winw.game.quant.Portfolio;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.Quote;
import winw.game.quant.QuoteIndex;
import winw.game.quant.QuotePanel;

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

	public List<QuoteIndex> compute(List<QuoteIndex> list) {
		super.compute(list);
		// 分段拟合
		LinkedHashMap<Integer, CurveFitter> resultMap = new LinkedHashMap<Integer, CurveFitter>();

		double[] xPoints = new double[list.size()];
		double[] yPoints = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			xPoints[i] = i;
			yPoints[i] = list.get(i).getClose();
		}

		System.out.println(yPoints.length);
		// 反向开始，拿10个点开始拟合，然后拿9个拟合，如果10个点可以成功，则继续拿20个点拟合
		for (int i = yPoints.length - 30, j = yPoints.length - 1; i > 0 && j - i > 1; i++) {

			// TODO 考虑去掉方差较大的点（意外跳动），再拟合。

			double[] xTemp = Arrays.copyOfRange(xPoints, i, j);
			double[] yTemp = Arrays.copyOfRange(yPoints, i, j);
			CurveFitter straight = new CurveFitter(xTemp, yTemp);
			straight.doFit(CurveFitter.STRAIGHT_LINE);
			CurveFitter poly2 = new CurveFitter(xTemp, yTemp);
			poly2.doFit(CurveFitter.POLY2);

			CurveFitter poly3 = new CurveFitter(xTemp, yTemp);
			poly3.doFit(CurveFitter.POLY3);
			
//			System.out.println("from [" + yPoints[i] + " to " + yPoints[j] + "] FitGoodness: " + poly2.getFitGoodness()
//					+ ", R^2: " + poly2.getRSquared());

			CurveFitter fit = poly2.getRSquared() > straight.getRSquared() ? poly2 : straight;
			fit = poly3.getRSquared() > fit.getRSquared() ? poly3 : poly2;
			if (fit.getRSquared() > 0.9) {// R^2 大于0.9说明拟合优度较好
				resultMap.put(i, fit);
				// 计算实际拟合的点
				for (int m = i + 1; m <= j; m++) {
					list.get(m).setY(fit.f(m));
					System.out.println("------- " + list.get(m).getClose() + " ----- " + list.get(m).getY());
				}
				j = i;
				i = j - 30 > yPoints.length ? yPoints.length : j - 30;
			}
		}
		return list;
	}

	@Override
	public void trading(Portfolio portfolio) {
		for (String code : samples()) {
			// 先计算最近一个拐点。
			QuoteIndex today = getQuoteIndex(code, 0);

			if (portfolio.getEmptyPositionDays(today.getCode(), 100) > 2 // 卖出后保持空仓天数
					&& !portfolio.hasPosition(today.getCode())) {
				portfolio.addBatch(today, 0.5, String.format("SlopeL: %.2f", today.getL()));
			}

			// TODO 过拟合问题，如何优化？

			// TODO 用直线拟合

			// TODO 只做指定的函数类型，比如开口向上的抛物线，并且在底部，回测
			// 或者开口向下的抛物线，并且在起步阶段。

		}
		stoploss(portfolio);
		portfolio.commitBatch();
	}

	public static void main(String[] args) throws Exception {
		Portfolio portfolio = new Portfolio(1000000, 5, 0.05, 0.05);
		DynamicAnalysisStrategy strategy = new DynamicAnalysisStrategy();
		strategy.backTesting(portfolio, "2021-08-01", Quote.today());
		QuotePanel.show(portfolio, strategy, "2021-08-01", Quote.today());
	}

}
