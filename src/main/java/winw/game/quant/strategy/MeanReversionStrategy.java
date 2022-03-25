package winw.game.quant.strategy;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;

import winw.game.quant.Portfolio;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.Quote;
import winw.game.quant.QuoteIndex;
import winw.game.quant.QuotePanel;

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
	public MeanReversionStrategy() {
		super();
		 this.samples.add(CSI_300);
		this.samples.addAll(Arrays.asList(CSI_300_TOP));
	}

	/**
	 * 计算 Z-Score（此处计算的是收盘价与20天移动平均线的差值的Z-Score）
	 * <p>
	 * 公式：z-score = (value - mean) / standard deviation;
	 */
	@Override
	public List<QuoteIndex> compute(List<QuoteIndex> list) {
		super.compute(list);
		double[] subArray = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			QuoteIndex quoteIndex = list.get(i);
			subArray[i] = quoteIndex.getClose() - quoteIndex.getMa20();
			if (i < 62) {
				continue;
			}
			// 取59天的数据。
			double std = Math.sqrt(StatUtils.populationVariance(subArray, i - 59, 59));
			quoteIndex.setZ((subArray[i - 1] - StatUtils.mean(subArray, i - 59, 59)) / std);
		}
		return list;
	}

	/**
	 * 用 Z-Score 实现
	 */
	@Override
	public void trading(Portfolio portfolio) {
		for (String code : samples()) {
			QuoteIndex today = getQuoteIndex(code, 0);
			QuoteIndex yesterday = getQuoteIndex(code, -1);

			if (today.getZ() <= -2) {
				portfolio.addPrompt(today.getCode() + ": " + String.format("Z: %.2f", today.getZ()));
			}
			if (yesterday.getZ() <= -2 && !portfolio.hasPosition(code)) {
				portfolio.addBatch(today, Double.valueOf(1) / portfolio.getMaxPosition(),
						String.format("Z: %.2f", today.getZ()));
			}
			if (yesterday.getZ() >= 1 && portfolio.hasPosition(code)) {
				portfolio.addBatch(today, 0, String.format("Z: %.2f", today.getZ()));
			}
		}
		stoploss(portfolio);

		portfolio.commitBatch();
	}

	// 当天z-score回去了也不能买。
	// 用Slope趋势卖出。或者回撤卖出。或者向上趋势形成后按趋势卖出。

	public static void main(String[] args) throws Exception {
		Portfolio portfolio = new Portfolio(500000, 3, 0.05, 0.05);
		MeanReversionStrategy strategy = new MeanReversionStrategy();

		strategy.backTesting(portfolio, "2021-10-01", Quote.today());
		QuotePanel.show(portfolio, strategy, "2021-09-01", Quote.today());
		// TODO test current day and next day buy.
	}
}
