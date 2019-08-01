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
 * 趋势跟踪策略。
 * 
 * @author winw
 *
 */
public class TrendFollowingStrategy extends QuantTradingStrategy {

	private String[] samples = CSI_300_TOP;

	public TrendFollowingStrategy() {
	}

	public TrendFollowingStrategy(String... samples) {
		this.samples = samples;
	}

	@Override
	public String[] samples() {
		return samples;
	}

	// TODO 收益率与国债ETF收益率比较（斜率）

	private List<QuantQuote> buyOrders = new ArrayList<QuantQuote>();
	private List<QuantQuote> sellOrders = new ArrayList<QuantQuote>();

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

			if (current.getSlope60() > 0.04 && current.getSlope5() > 0.1
					&& portfolio.getEmptyPositionDays(current.getCode(), 100) > 2 // 卖出后保持空仓天数
					&& !portfolio.hasPosition(current.getCode())) {
				buyOrders.add(current);
			}

			if (current.getSlope60() < 0.03 && portfolio.hasPosition(current.getCode())) {
				sellOrders.add(current);
			}
		}
		for (QuantQuote temp : sellOrders) {
			portfolio.order(temp, -1, String.format("Slope60: %.2f", temp.getSlope60()));
		}

		for (QuantQuote temp : buyOrders) {
			portfolio.order(temp, 1, String.format("Slope60: %.2f", temp.getSlope60()));
		}
	}

	public static void main(String[] args) throws Exception {
		String today = DateFormatUtils.format(new Date(), QuoteService.DATE_PATTERN);
		Portfolio portfolio = new Portfolio(1000000, 1, 0.1, 0.1);
//		Portfolio portfolio = new Portfolio(1000000, 1, 0.07, 0.05);
		TrendFollowingStrategy strategy = new TrendFollowingStrategy();
		strategy.backTesting(portfolio, "2018-01-01", today);

		QuoteChart.show(portfolio, "2018-01-01", today);
	}

}
