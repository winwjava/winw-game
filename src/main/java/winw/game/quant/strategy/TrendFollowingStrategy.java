package winw.game.quant.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

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

	@Override
	public List<QuantQuote> compute(List<QuantQuote> list) {
		return computeSlope(super.compute(list));
	}

	/**
	 * 计算 slope
	 */
	protected static List<QuantQuote> computeSlope(List<QuantQuote> list) {
		// 线性回归，计算斜率
		SimpleRegression regression = new SimpleRegression();
		for (int i = 1; i < list.size(); i++) {
			QuantQuote quantQuote = list.get(i);
			if (i < 5) {
				continue;
			}

			regression.clear();

			// 算法一：用最近60天的最高价和最低价作为高，60天标准高宽比例是300：1500
			// Y = 300 * (EMA60 - MIN(CLOSE, 60) ) * (MAX(CLOSE, 60) - MIN(CLOSE, 60) )
			// X = (1500 / 60) * N
			double hight = list.get(i).getEma60max() - list.get(i).getEma60min();
			double bottom = list.get(i).getEma60min();

			for (int j = 1; j <= 3; j++) {
				QuantQuote temp = list.get(i - 3 + j);
				regression.addData((1500 / 60) * j, 300d * (temp.getEma5() - bottom) / hight);
			}
			quantQuote.setSlopeS(regression.getSlope());
			regression.clear();
			for (int j = 1; j <= 5; j++) {
				QuantQuote temp = list.get(i - 5 + j);
				regression.addData((1500d / 60) * j, 300d * (temp.getEma60() - bottom) / hight);
			}
			quantQuote.setSlopeL(regression.getSlope());
			// 算法二：模拟X轴，X点的间隔用Y点的0.005倍（y * 30% / 60）
			// 算法三：每年250个交易日年化4%收益，斜率应该是0.04，则Y的间隔应该是X的0.104倍（y(1 + 0.04) / (250x) = 0.04）
			// double xInterval5 = indicator.getEma5() * 0.005;
			// for (int j = 0; j < 3; j++) {
			// regression.addData(j * xInterval5, list.get(i - 3 + j).getEma5());
			// }
			// indicator.setSlope5(regression.getSlope());
			//
			// regression.clear();
			// double xInterval60 = indicator.getEma60() * 0.005;
			// for (int j = 0; j < 6; j++) {
			// if (indicator.getDate().equals("2018-01-15")) {
			// System.out.println("====" + list.get(i - 5 + j).getEma60());
			// }
			// regression.addData(j * xInterval60, list.get(i - 5 + j).getEma60());
			// }
			// indicator.setSlope60(regression.getSlope());
			// if (indicator.getDate().equals("2018-01-15")) {
			// NumberFormat percentFormat = new DecimalFormat("##.00%");
			// System.out.println("====" + percentFormat.format(regression.getSlope()));
			// }
		}
		return list;
	}

	public static void main0(String[] args) {
		double[] points60 = new double[] { 16.2441, 16.2296, 16.2048, 16.2129, 16.2746, 16.3092 };

		SimpleRegression regression = new SimpleRegression();
		double xInterval5 = points60[5] * 0.15;
		for (int j = 0; j <= 5; j++) {
			regression.addData(j * xInterval5, points60[j]);
		}
	}

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
		double bondYield = shbond.getSlopeL();// 国债收益率
		for (String code : samples()) {
			if (SH_BOND.equals(code)) {
				continue;
			}
			// TODO 从转折点买入，避免从顶部买入。
			QuantQuote current = getCurrentQuote(code);
			// TODO 5/10/20均线全部需要向上。
			if (current.getSlopeL() > bondYield && current.getSlopeS() > bondYield
					&& portfolio.getEmptyPositionDays(current.getCode(), 100) > 2 // 卖出后保持空仓天数
					&& !portfolio.hasPosition(current.getCode())) {
				buyOrders.add(current);
			}

			// 考虑用20日线。卖出更可靠。
			if (current.getSlopeL() < bondYield && portfolio.hasPosition(current.getCode())) {
				sellOrders.add(current);
			}
		}
		for (QuantQuote temp : sellOrders) {
			portfolio.order(temp, -1, String.format("Slope60: %.2f", temp.getSlopeL()));
		}
		stoploss(portfolio);
		// 如果只持有国债，并且需要建仓时，先卖出国债
		Map<String, Position> positions = portfolio.getPositions();
		if (holdBonds && positions.size() == 1 && positions.containsKey(SH_BOND) && buyOrders.size() > 0) {
			portfolio.order(shbond, -1, String.format("Slope60: %.2f", shbond.getSlopeL()));
		}

		for (QuantQuote temp : buyOrders) {
			portfolio.order(temp, 1, String.format("Slope60: %.2f", temp.getSlopeL()));
		}
		// 如果空仓，则买入国债。
		if (holdBonds && positions.size() == 0) {
			portfolio.order(shbond, 1, String.format("Slope60: %.2f", shbond.getSlopeL()));
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

		QuoteChart.show(portfolio, strategy, "2019-01-01", today);
	}

	// TODO 如果当前持仓收益较低，则应及时调仓，应对持有相比更靠近底部，斜率更高。
	// 因为该策略如果有其他

}
