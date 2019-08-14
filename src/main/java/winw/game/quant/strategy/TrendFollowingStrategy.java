package winw.game.quant.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import winw.game.quant.Portfolio;
import winw.game.quant.Position;
import winw.game.quant.QuantQuote;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.Quote;
import winw.game.quant.QuoteChart;

/**
 * 趋势跟踪策略。
 * 
 * @author winw
 *
 */
public class TrendFollowingStrategy extends QuantTradingStrategy {

	// 默认用国债和300二八轮换
	private String[] samples = { CSI_300, SH_BOND };

	private boolean holdBonds = false;// 空仓时持有国债。

	public TrendFollowingStrategy() {
	}

	public TrendFollowingStrategy(String... samples) {
		this.samples = ArrayUtils.addAll(samples, SH_BOND);
	}

	@Override
	public String[] samples() {
		return samples;
	}

	private List<QuantQuote> buyOrders = new ArrayList<QuantQuote>();
	private List<QuantQuote> sellOrders = new ArrayList<QuantQuote>();

	@Override
	public void trading(Portfolio portfolio) {
		buyOrders.clear();
		sellOrders.clear();
		QuantQuote shbond = getCurrentQuote(SH_BOND);
		double bondYield = shbond.getSlope60();// 国债收益率
		for (String code : samples()) {
			if (SH_BOND.equals(code)) {
				continue;
			}
			// TODO 从转折点买入，避免从顶部买入。
			QuantQuote current = getCurrentQuote(code);
			// TODO 5/10/20均线全部需要向上。
			if (current.getSlope60() > bondYield && current.getSlope5() > bondYield
					&& portfolio.getEmptyPositionDays(current.getCode(), 100) > 2 // 卖出后保持空仓天数
					&& !portfolio.hasPosition(current.getCode())) {
				buyOrders.add(current);
			}

			// 考虑用20日线。卖出更可靠。
			if (current.getSlope60() < bondYield && portfolio.hasPosition(current.getCode())) {
				sellOrders.add(current);
			}
		}
		for (QuantQuote temp : sellOrders) {
			portfolio.order(temp, -1, String.format("Slope60: %.2f", temp.getSlope60()));
		}
		stoploss(portfolio);
		// 如果只持有国债，并且需要建仓时，先卖出国债
		Map<String, Position> positions = portfolio.getPositions();
		if (holdBonds && positions.size() == 1 && positions.containsKey(SH_BOND) && buyOrders.size() > 0) {
			portfolio.order(shbond, -1, String.format("Slope60: %.2f", shbond.getSlope60()));
		}

		for (QuantQuote temp : buyOrders) {
			portfolio.order(temp, 1, String.format("Slope60: %.2f", temp.getSlope60()));
		}
		// 如果空仓，则买入国债。
		if (holdBonds && positions.size() == 0) {
			portfolio.order(shbond, 1, String.format("Slope60: %.2f", shbond.getSlope60()));
		}
	}

	public boolean isHoldBonds() {
		return holdBonds;
	}

	public void setHoldBonds(boolean holdBonds) {
		this.holdBonds = holdBonds;
	}

	public static void main(String[] args) throws Exception {
		String today = DateFormatUtils.format(new Date(), Quote.DATE_PATTERN);
		Portfolio portfolio = new Portfolio(1000000, 1, 0.05, 0.05);
		TrendFollowingStrategy strategy = new TrendFollowingStrategy(CSI_300_TOP);
		// strategy.setHoldBonds(true);
		strategy.backTesting(portfolio, "2019-01-01", today);
		QuoteChart.show(portfolio, "2019-01-01", today);
	}
	
	// TODO 如果当前持仓收益较低，则应及时调仓，应对持有相比更靠近底部，斜率更高。
	// 因为该策略如果有其他

}
