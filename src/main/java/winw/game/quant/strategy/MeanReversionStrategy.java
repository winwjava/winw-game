package winw.game.quant.strategy;

import java.util.List;

import org.apache.commons.math3.stat.StatUtils;

import winw.game.quant.Portfolio;
import winw.game.quant.QuantQuote;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.Quote;
import winw.game.quant.QuoteChart;

/**
 * 均值回归策略。
 * <p>
 * 价格距离移动平均线越远，其回归的可能性就越大。
 * <p>
 * 在市场中应用这种策略的方法是寻找极端事件，然后打赌事情将恢复到接近平均水平。
 * 
 * <p>
 * Standard Deviation/RSI/Bollinger Bands
 * 
 * @author winw
 *
 */
public class MeanReversionStrategy extends QuantTradingStrategy {

	@Override
	public String[] samples() {
		return CSI_300_TOP;
	}

	/**
	 * 计算 Z-Score（此处计算的是收盘价与20天移动平均线的差值的Z-Score）
	 * <p>
	 * 公式：z-score = (value - mean) / standard deviation;
	 */
	@Override
	public List<QuantQuote> compute(List<QuantQuote> list) {
		super.compute(list);
		double[] subArray = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			QuantQuote quantQuote = list.get(i);
			subArray[i] = quantQuote.getClose() - quantQuote.getMa20();
			if (i < 62) {
				continue;
			}
			// 取59天的数据。
			double std = Math.sqrt(StatUtils.populationVariance(subArray, i - 59, 59));
			quantQuote.setZscore((subArray[i - 1] - StatUtils.mean(subArray, i - 59, 59)) / std);
		}
		return list;
	}

	/**
	 * 用 Z-Score 实现
	 */
	@Override
	public void trading(Portfolio portfolio) {
		for (String code : samples()) {
			List<QuantQuote> quantQuotes = getHistoryQuote(code);
			if (quantQuotes == null || quantQuotes.isEmpty()) {
				return;
			}
			QuantQuote current = quantQuotes.get(quantQuotes.size() - 1);
			QuantQuote yestday = quantQuotes.get(quantQuotes.size() - 2);

			if (yestday.getZscore() <= -2 && !portfolio.hasPosition(code)) {
				portfolio.addBatch(current, 1, String.format("z-score: %.2f", current.getZscore()));
			}
			if (yestday.getZscore() >= 1 && portfolio.hasPosition(code)) {
				portfolio.addBatch(current, -1, String.format("z-score: %.2f", current.getZscore()));
			}
		}

		stoploss(portfolio);
		portfolio.commitBatch();
	}

	// 当天z-score回去了也不能买。
	// TODO 用Slope趋势卖出。或者回撤卖出。或者向上趋势形成后按趋势卖出。

	public static void main(String[] args) throws Exception {
		Portfolio portfolio = new Portfolio(1000000, 1, 0.15, 0.15);
		MeanReversionStrategy strategy = new MeanReversionStrategy();
		strategy.backTesting(portfolio, "2019-04-26", Quote.today());
		QuoteChart.show(portfolio, strategy, "2019-04-01", Quote.today());
	}
}
