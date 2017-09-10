package winw.game.stock.analysis;

import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.List;

import winw.game.stock.analysis.Advise.Market;
import winw.game.stock.analysis.Advise.Signal;

/**
 * 技术分析。
 * 
 * @author winw
 *
 */
public class TechnicalAnalysis {

	/**
	 * MACD趋势分析。
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
	 * @param list
	 */
	public static Advise analysisMACD(List<Indicator> list) {// MACD信号线交叉分析、零交叉分析。
		// 由于MACD是基于移动平均线，因此本质上是滞后指标。
		// FIXME 作为价格趋势的指标，MACD对于没有趋势（在一定范围内交易）或正在以不稳定的价格行动进行交易的股票不太有用。
		Advise advise = new Advise();
		Indicator today = list.get(list.size() - 1);
		Indicator yesterday = list.get(list.size() - 2);
		StringBuilder result = new StringBuilder();

		// DIFF和DEA的两条线交叉
		boolean intersect = Line2D.linesIntersect(0, yesterday.getDiff(), 1, today.getDiff(), 0, yesterday.getDea(), 1,
				today.getDea());

		if (intersect && today.getDiff() > today.getDea()) {// && today.getDiff() > 0 && today.getDea() > 0
			advise.setSignal(Signal.BUY_SIGNAL);
			result.append("1. MACD金叉：DIFF 由下向上突破 DEA，为买入信号。");
		}
		if (intersect && today.getDiff() < today.getDea()) {
			advise.setSignal(Signal.SELL_SIGNAL);
			result.append("2. MACD死叉：DIFF 由上向下突破 DEA，为卖出信号。");
		}
		// if (today.getMacd() > 0 && yesterday.getMacd() < 0) {
		// advise.setMarket(Market.MARKET_BOTTOM);
		// result.append("3. MACD 绿转红：MACD 值由负变正，市场由空头转为多头。");
		// }
		// if (today.getMacd() < 0 && yesterday.getMacd() > 0) {
		// result.append("4. MACD 红转绿：MACD 值由正变负，市场由多头转为空头。");
		// advise.setMarket(Market.MARKET_TOP);
		// }

		// ⒈当DIF和DEA处于0轴以上时，属于多头市场。
		// ⒉当DIF和DEA处于0轴以下时，属于空头市场。

		// MACD值在变小，认为是熊市，否则认为是牛市
		advise.setMarket(today.getMacd() >= yesterday.getMacd() ? Market.BULL_MARKET : Market.BEAR_MARKET);
		// advise.setMarket(today.getDiff() < yesterday.getDiff() ? Market.BULL_MARKET :
		// Market.BEAR_MARKET);

		if (result.length() > 0) {
			advise.setAdvise(result.toString());
		}
		return advise;
	}

	public static Advise analysisKDJ(List<Indicator> list) {
		// 使用要领
		// 1.KDJ指标的区间主要分为3个小部分，即20以下、20—80之间和80以上。其中20以下的区间为超卖区；80以上的区域为超买区；20—80之间的区域为买卖平衡区。
		// 2.如果K、D、J值都大于50时，为多头市场，后市看涨；如果K、D、J值都小于50时，为空头市场，后市看空。
		// 3.KDJ指标图形中，D曲线运行速度最慢，敏感度最低；其次是K曲线，J曲线敏感度最强。
		// 4.当J大于K、K大于D时，即3条指标曲线呈多头排列，显示当前为多头市场；当3条指标出现黄金交叉时，指标发出买入信号。
		// 5.当3条指标曲线呈空头排列时，表示短期是下跌趋势；3条曲线出现死亡交叉时，指标发出卖出信号。
		// 6.如果KD线交叉突破反复在50左右震荡，说明行情正在整理，此时要结合J值，观察KD偏离的动态，再决定投资行动。

		Advise advise = new Advise();
		Indicator today = list.get(list.size() - 1);
		Indicator yesterday = list.get(list.size() - 2);
		StringBuilder result = new StringBuilder();

		// K线和D线的两条线交叉
		boolean intersect = Line2D.linesIntersect(0, yesterday.getK(), 1, today.getK(), 0, yesterday.getD(), 1,
				today.getD());

		if (intersect && today.getK() > today.getD()) {
			advise.setSignal(Signal.BUY_SIGNAL);
			result.append("1. K值大于D值，K线向上突破D线时，为买进信号。");
		}
		if (intersect && today.getK() < today.getD()) {
			advise.setSignal(Signal.SELL_SIGNAL);
			result.append("2. K值小于D值，K线向下跌破D线时，为卖出信号。");
		}
		// FIXME 经常被主力操纵
		// 5.KDJ指标比RSI准确率高，且有明确的买、卖点出现，但K、D线交叉时须注意“骗线”出现，主要因为KDJ指标过于敏感且此指标群众基础较好，所以经常被主力操纵。
		// 6.K线与D线的交叉突破在80以上或20以下时较为准确。当这种交叉突破在50左右发生时，表明市场走势陷入盘局，正在寻找突破方向。此时，K线与D线的交叉突破所提供的买卖信号无效。

		// MACD值在变小，认为是熊市，否则认为是牛市
		// advise.setMarket(today.getMacd() >= yesterday.getMacd() ? Market.BULL_MARKET
		// : Market.BEAR_MARKET);
		// advise.setMarket(today.getDiff() < yesterday.getDiff() ? Market.BULL_MARKET :
		// Market.BEAR_MARKET);

		if (result.length() > 0) {
			advise.setAdvise(result.toString());
		}
		return advise;
	}

	// protected static final double XXX = Math.sin(Math.PI * 2 / 180);

	/**
	 * FIXME 计算OBV和VPT的需要以上市第一天作为基期，才可保证数据准确，暂时放弃。
	 */
	@Deprecated
	public static Advise analysisOBV(List<Indicator> list) {
		Advise advise = new Advise();
		Indicator today = list.get(list.size() - 1);
		Indicator yesterday = list.get(list.size() - 2);
		// today.getObv() > 0 &&
		// today.getObvma() >= yesterday.getObvma()

		double vate = (today.getObvma() - yesterday.getObvma()) / yesterday.getObvma();

		if (today.getObvma() <= 0) {
			advise.setMarket(Market.BEAR_MARKET);
		}
		// else if (vate < 0) {
		// advise.setMarket(Market.BEAR_MARKET);
		// }
		// if (Math.abs(vate) < XXX) {
		// advise.setMarket(null);
		// }
		DecimalFormat decimalFormat = new DecimalFormat("##0.000");
		advise.setAdvise("" + decimalFormat.format(vate));
		return advise;
	}

	/**
	 * FIXME 计算OBV和VPT的需要以上市第一天作为基期，才可保证数据准确，暂时放弃。
	 */
	@Deprecated
	public static Advise analysisVPT(List<Indicator> list) {
		Advise advise = new Advise();
		Indicator today = list.get(list.size() - 1);
		// Indicator yesterday = list.get(list.size() - 2);
		if (today.getVptema() <= 0) {
			advise.setMarket(Market.BEAR_MARKET);
		}
		return advise;
	}
}
