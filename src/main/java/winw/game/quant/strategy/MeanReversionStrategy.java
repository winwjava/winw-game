package winw.game.quant.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;

import winw.game.quant.Portfolio;
import winw.game.quant.QuantQuote;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.QuoteChart;
import winw.game.quant.QuoteService;

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

	private List<QuantQuote> buyOrders = new ArrayList<QuantQuote>();
	private List<QuantQuote> sellOrders = new ArrayList<QuantQuote>();

	/**
	 * 用 Z-Score 实现
	 */
	@Override
	public void trading(Portfolio portfolio) {
		buyOrders.clear();
		sellOrders.clear();
		for (String code : samples()) {
			List<QuantQuote> quantQuotes = getHistoryQuote(code);
			if (quantQuotes == null || quantQuotes.isEmpty()) {
				return;
			}
			QuantQuote current = quantQuotes.get(quantQuotes.size() - 1);
			QuantQuote yestday = quantQuotes.get(quantQuotes.size() - 2);

			if (yestday.getZscore() <= -2 && !portfolio.hasPosition(code)) {
				buyOrders.add(current);
			}

			if (yestday.getZscore() >= 1 && portfolio.hasPosition(code)) {
				sellOrders.add(current);
			}
		}
		for (QuantQuote temp : sellOrders) {
			portfolio.order(temp, -1, String.format("z-score: %.2f", temp.getZscore()));
		}

		for (QuantQuote temp : buyOrders) {
			portfolio.order(temp, 1, String.format("z-score: %.2f", temp.getZscore()));
		}
	}

	// 当天z-score回去了也不能买。
	// TODO 用Slope趋势卖出。或者回撤卖出。或者向上趋势形成后按趋势卖出。

	public static void main(String[] args) throws Exception {
		String today = DateFormatUtils.format(new Date(), QuoteService.DATE_PATTERN);
		Portfolio portfolio = new Portfolio(1000000, 1, 0.15, 0.15);
		MeanReversionStrategy strategy = new MeanReversionStrategy();
		strategy.backTesting(portfolio, "2019-04-26", today);

		// TODO 幅图显示z-score和MACD
		QuoteChart.show(portfolio, "2019-03-01", today);
	}
}
