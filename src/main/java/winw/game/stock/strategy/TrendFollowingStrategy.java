package winw.game.stock.strategy;

import java.util.List;

import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Advise.Signal;
import winw.game.stock.analysis.Indicator;

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
		Indicator yesterday = list.get(list.size() - 2);

		// 当DIF和DEA处于0轴以上时，属于多头市场。
		// 当DIF和DEA处于0轴以下时，属于空头市场。
		// boolean bullMarket = today.getDiff() > 0 && today.getDea() > 0;

		// if (cross && today.getDiff() > today.getDea()) {
		// return new Advise(Signal.BUY_SIGNAL);
		// }

		// 零交叉
		// if (today.getDiff() > 0 && yesterday.getDiff() < 0 && today.getMacd() > 0) {
		// return new Advise(Signal.BUY_SIGNAL);
		// }
		// if (today.getDiff() < 0 && yesterday.getDiff() > 0) {
		// return new Advise(Signal.SELL_SIGNAL);
		// }

		// 熊市
		// boolean bullMarket = false;
		// for (int i = list.size() - 12; i < list.size() - 1; i++) {
		// if (list.get(i).getDiff() > 0) {
		// bullMarket = true;
		// break;
		// }
		// }

		// 信号线交叉
		// MACD金叉：DIFF 由下向上突破 DEA，为买入信号。
		// MACD死叉：DIFF 由上向下突破 DEA，为卖出信号。
		if (today.getMacd() > 0 && yesterday.getMacd() < 0) {
			return new Advise(Signal.BUY_SIGNAL);
		}
		if (today.getMacd() < 0 && yesterday.getMacd() > 0) {
			return new Advise(Signal.SELL_SIGNAL);
		}

		// MACD信号线交叉分析、零交叉分析。
		// 由于MACD是基于移动平均线，因此本质上是滞后指标。
		// FIXME 作为价格趋势的指标，MACD对于没有趋势（在一定范围内交易）或正在以不稳定的价格行动进行交易的股票不太有用。
		return new Advise();
	}

}
