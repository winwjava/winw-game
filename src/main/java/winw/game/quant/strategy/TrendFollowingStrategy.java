package winw.game.quant.strategy;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import winw.game.quant.Portfolio;
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

	// private boolean holdBonds = false;// 空仓时持有国债。

	public TrendFollowingStrategy() {
	}

	public TrendFollowingStrategy(String... samples) {
		this.samples = ArrayUtils.addAll(samples, SH_BOND);
	}

	@Override
	public String[] samples() {
		return samples;
	}

	private SimpleRegression regression = new SimpleRegression();

	/**
	 * 
	 * 计算EMA曲线的导数。由于价格与时间没有直接的比例关系，所以共有三种假定关系的实现。
	 * 
	 * <p>
	 * 算法一：用最近60天的最高价和最低价作为高，60天标准高宽比例是300：1500 <br>
	 * Y = 300 * (EMA60 - MIN(CLOSE, 60) ) * (MAX(CLOSE, 60) - MIN(CLOSE, 60) ) <br>
	 * X = (1500 / 60) * N
	 * <p>
	 * 算法二：模拟X轴，X点的间隔用Y点的0.005倍（y * 30% / 60）
	 * <p>
	 * 算法三：每年250个交易日年化4%收益，斜率应该是0.04，则Y的间隔应该是X的0.104倍（y(1 + 0.04) / (250x) = 0.04）
	 * 
	 * @param list
	 * @return
	 */
	@Override
	public List<QuantQuote> compute(List<QuantQuote> list) {
		super.compute(list);
		// 线性回归，计算斜率
		for (int i = 1; i < list.size(); i++) {
			QuantQuote quantQuote = list.get(i);
			if (i < 5) {
				continue;
			}

			regression.clear();

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

	@Override
	public void trading(Portfolio portfolio) {
		QuantQuote shbond = getCurrentQuote(SH_BOND);
		double bondYield = shbond.getSlopeL();// 国债收益率
		for (String code : samples()) {
			if (SH_BOND.equals(code)) {
				continue;
			}
			List<QuantQuote> quantQuotes = getHistoryQuote(code);
			QuantQuote current = quantQuotes.get(quantQuotes.size() - 1);
			QuantQuote yestday = quantQuotes.get(quantQuotes.size() - 2);
			// 5/10/20均线是否全部需要向上？
			// 从转折点买入，避免从顶部买入。
			if (yestday.getSlopeL() < bondYield && current.getSlopeL() > bondYield && current.getSlopeS() > bondYield
					&& portfolio.getEmptyPositionDays(current.getCode(), 100) > 2 // 卖出后保持空仓天数
					&& !portfolio.hasPosition(current.getCode())) {
				portfolio.addBatch(current, 1, String.format("SlopeL: %.2f", current.getSlopeL()));
			}

			// 考虑用20日线。卖出更可靠。
			if (current.getSlopeL() < bondYield) {
				portfolio.addBatch(current, -1, String.format("SlopeL: %.2f", current.getSlopeL()));
			}
		}
		stoploss(portfolio);
		portfolio.commitBatch();
	}

	public static void main(String[] args) throws Exception {
		Portfolio portfolio = new Portfolio(1000000, 1, 0.05, 0.05);
		TrendFollowingStrategy strategy = new TrendFollowingStrategy(CSI_300_TOP);
		strategy.backTesting(portfolio, "2019-01-01", Quote.today());
		QuoteChart.show(portfolio, strategy, "2019-01-01", Quote.today());
	}

	public static void main0(String[] args) {
		SimpleRegression regression = new SimpleRegression();
		double[] points60 = new double[] { 16.2441, 16.2296, 16.2048, 16.2129, 16.2746, 16.3092 };
		double xInterval5 = points60[5] * 0.15;
		for (int j = 0; j <= 5; j++) {
			regression.addData(j * xInterval5, points60[j]);
		}
	}

	// TODO 如果当前持仓收益较低，则应及时调仓，应当持有相比更靠近底部的标的，可以用斜率做比较。

}
