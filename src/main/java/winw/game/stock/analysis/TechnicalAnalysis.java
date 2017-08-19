package winw.game.stock.analysis;

import java.util.List;

import winw.game.stock.analysis.Advise.Market;
import winw.game.stock.analysis.Advise.Signal;

/**
 * 趋势分析。
 * 
 * @author winw
 *
 */
public class TechnicalAnalysis {

	// 由于MACD是基于移动平均线，因此本质上是滞后指标。
	// 作为价格趋势的指标，MACD对于不趋势（在一定范围内交易）或正在以不稳定的价格行动进行交易的股票不太有用。

	// public Advise analysis(Stock stock, List<StockQuote> quoteList) {
	// addToday(stock, quoteList);
	// 计算 MA MACD BOLL RSI KDJ 指标
	// return macdAnalysis(stock, Indicators.compute(quoteList));

	// TODO KDJ
	// TODO RSI
	// }

	/**
	 * MACD趋势分析。
	 * 
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
	public static Advise macdAnalysis(List<Indicator> list) {// MACD信号线交叉分析、零交叉分析。
		Advise advise = new Advise();
		Indicator today = list.get(list.size() - 1);
		Indicator yesterday = list.get(list.size() - 2);
		StringBuilder result = new StringBuilder();

		// TODO 如何避免虚假信号

		// 1. MACD金叉：DIFF 由下向上突破 DEA，为买入信号。
		if (today.getDiff() > yesterday.getDiff() && today.getDea() < yesterday.getDea()) {

			advise.setSignal(Signal.BUY_SIGNAL);
			result.append("1. MACD金叉：DIFF 由下向上突破 DEA，为买入信号。");
		}
		// 2. MACD死叉：DIFF 由上向下突破 DEA，为卖出信号。
		if (today.getDiff() < yesterday.getDiff() && today.getDea() > yesterday.getDea()) {
			advise.setSignal(Signal.SELL_SIGNAL);
			result.append("2. MACD死叉：DIFF 由上向下突破 DEA，为卖出信号。");
		}
		// 3. MACD 绿转红：MACD 值由负变正，市场由空头转为多头。
		if (today.getMacd() > 0 && yesterday.getMacd() < 0) {
			advise.setMarket(Market.BULL_MARKET);
			result.append("3. MACD 绿转红：MACD 值由负变正，市场由空头转为多头。");
		}
		// 4. MACD 红转绿：MACD 值由正变负，市场由多头转为空头。
		if (today.getMacd() < 0 && yesterday.getMacd() > 0) {
			result.append("4. MACD 红转绿：MACD 值由正变负，市场由多头转为空头。");
			advise.setMarket(Market.BEAR_MARKET);
			advise.setSignal(Signal.SELL_SIGNAL);
		}

//		if (today.getDiff() < yesterday.getDiff()) {
//			result.append("MACD 比昨天小，卖掉");
//			advise.setSignal(Signal.SELL_SIGNAL);
//		}
		
		advise.setMarket(today.getMacd() > 0 ? Market.BULL_MARKET : Market.BEAR_MARKET);

		if (result.length() > 0) {
			advise.setAdvise(result.toString());
		}
		return advise;
	}

}
