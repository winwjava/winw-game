package winw.game.quant.strategy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.StatUtils;

import winw.game.quant.Portfolio;
import winw.game.quant.Position;
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
		this.samples.add(SH_BOND_ETF);
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
		boolean buy = false, sell = false;
		for (String code : samples()) {
			if (SH_BOND_ETF.equals(code)) {
				continue;
			}
			QuoteIndex today = getQuoteIndex(code, 0);
			QuoteIndex yesterday = getQuoteIndex(code, -1);

			if (yesterday.getZ() <= -2 && !portfolio.hasPosition(code)) {
				buy = true;
				portfolio.addBatch(today, 1, String.format("z-score: %.2f", today.getZ()));
			}
			if (yesterday.getZ() >= 1 && portfolio.hasPosition(code)) {
				sell = true;
				portfolio.addBatch(today, -1, String.format("z-score: %.2f", today.getZ()));
			}
		}
		stoploss(portfolio);

		QuoteIndex shbond = getQuoteIndex(SH_BOND_ETF, 0);
		// 有需要建仓时，先将国债清仓。
		Map<String, Position> positions = portfolio.getPositions();
		if (positions.containsKey(SH_BOND_ETF) && buy) {
			portfolio.addBatch(shbond, -1, "auto sell bond");
		}
		// TODO 余额自动买入国债。
		if (sell || (buy == false && portfolio.maxBuy(shbond.getClose()) >= 100)) {
			portfolio.addBatch(shbond, 1, "auto buy bond");
		}
		portfolio.commitBatch();
	}

	// 当天z-score回去了也不能买。
	// TODO 用Slope趋势卖出。或者回撤卖出。或者向上趋势形成后按趋势卖出。

	public static void main(String[] args) throws Exception {
		Portfolio portfolio = new Portfolio(1000000, 1, 0.15, 0.15);
		MeanReversionStrategy strategy = new MeanReversionStrategy();
		strategy.backTesting(portfolio, "2019-04-26", Quote.today());
		QuotePanel.show(portfolio, strategy, "2019-04-01", Quote.today());
	}
}
