package winw.game.stock.strategy;

import java.util.List;

import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Indicator;
import winw.game.stock.analysis.TechnicalAnalysis;

/**
 * 趋势跟踪策略。
 * 
 * <p>
 * A trend can only be determined in hindsight, since at any time prices in the
 * future are not known.
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
	 * 50日均线确认趋势，MACD零交叉（金叉、死叉）确定买入卖出，
	 */
	@Override
	public Advise analysis(List<Indicator> indicator) {

		return TechnicalAnalysis.analysisMACD(indicator);
	}

}
