package winw.game.quant.strategy;

import java.util.List;

import winw.game.quant.Trade;
import winw.game.quant.analysis.Indicator;

/**
 * 金叉死叉策略。
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
public class GoldenCrossStrategy extends AbstractStrategy {

	@Override
	public void trading(String... code) {
		List<Indicator> indicators = getHistoryQuote(code[0]);
		if (indicators == null || indicators.isEmpty()) {
			return;
		}
		Indicator today = indicators.get(indicators.size() - 1);
		Indicator yesterday = indicators.get(indicators.size() - 2);

		if (today.getDiff() > 0 && yesterday.getDiff() < 0 && today.getMacd() > 0 && today.getSlope60() > 0.02
				&& portfolio.getPosition(today.getCode()) == 0) {
			Trade order = portfolio.order(today, 1);
			notify(order, ", GOLDEN_CROSSOVER");
		}

		// if (today.getMacd() > 0 && yesterday.getMacd() < 0 &&
		// portfolio.getPosition(today.getCode()) == 0) {
		// Trade order = portfolio.order(today, 1);
		// String subject = today.getDate() + "[B]" + order.getCode() + "
		// ZERO_CROSSOVER";
		// System.out.println(subject + ", " + order);
		// mailService.send(subject, order);
		// }
		if (today.getMacd() < 0 && yesterday.getMacd() > 0 && portfolio.getPosition(today.getCode()) > 0) {
			Trade order = portfolio.order(today, 0);
			notify(order, ", DEATH_CROSSOVER");
		}

		// TODO 20日成交量
	}

}
