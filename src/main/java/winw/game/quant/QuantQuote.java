package winw.game.quant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * 报价的量化指标。
 * 
 * @author winw
 *
 */
public class QuantQuote extends Quote {

	protected double ma5;// 5日均价
	protected double ma10;// 10日均价
	protected double ma20;// 20日均价
	protected double ma60;// 60日均价

	protected double ema5;
	protected double ema60;

	protected double volumeMa5;// 5日均量
	protected double volumeMa10;// 10日均量
	protected double volumeMa20;// 20日均量

	// MACD 指标的三个属性
	protected double diff;
	protected double dea;
	protected double macd;

	// KDJ 指标的三个属性
	protected double k;
	protected double d;
	protected double j;

	// RSI 指标的三个属性
	private double rsi1;
	private double rsi2;
	private double rsi3;

	// BOLL 指标的三个属性
	private double up; // 上轨线
	private double mb; // 中轨线
	private double dn; // 下轨线

	private double ema60max;
	private double ema60min;

	// MA60的斜率
	private double slope5;
	private double slope60;

	// standard score
	private double zscore;

	public QuantQuote() {
		super();
	}

	public QuantQuote(Quote o) {
		super();
		this.code = o.getCode();
		this.name = o.getName();
		this.date = o.getDate();
		this.open = o.getOpen();
		this.high = o.getHigh();
		this.low = o.getLow();
		this.close = o.getClose();
		this.volume = o.getVolume();
		this.amount = o.getAmount();
	}

	/**
	 * 取不超过days时间的天
	 * 
	 * @param days
	 * @param list
	 * @return
	 * @throws ParseException
	 */
	private static int formIndex(int days, List<? extends Quote> list) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, (int) (-days - (50 * 1.2)));
		Date start = calendar.getTime();
		DateFormat dateFormat = new SimpleDateFormat(Quote.DATE_PATTERN);
		int from = 0;// -1
		for (int i = 0; i < list.size(); i++) {
			if (start.before(dateFormat.parse(list.get(i).getDate()))) {
				from = i;
				break;
			}
		}
		return from;
	}

	public static List<QuantQuote> compute(List<? extends Quote> quoteList, int days) throws ParseException {
		return compute(quoteList.subList(formIndex(days, quoteList), quoteList.size()));
	}

	/**
	 * 计算 MA MACD BOLL RSI KDJ 指标
	 * 
	 * @param quoteList
	 * @return
	 */
	public static List<QuantQuote> compute(List<? extends Quote> quoteList) {
		List<QuantQuote> list = new ArrayList<QuantQuote>();
		for (Quote quote : quoteList) {
			list.add(new QuantQuote(quote));
		}
		computeMA(list);
		computeEMA(list);
		computeMACD(list);
		// computeBOLL(list);
		// computeRSI(list);
		// computeKDJ(list);
		computeSlope(list);
		computeZscore(list);
		return list;
	}

	/**
	 * 计算 MA
	 */
	protected static void computeMA(List<QuantQuote> list) {
		double ma5 = 0;
		double ma10 = 0;
		double ma20 = 0;
		double ma60 = 0;
		double volumeMa5 = 0;
		double volumeMa10 = 0;

		for (int i = 0; i < list.size(); i++) {
			QuantQuote quantQuote = list.get(i);

			ma5 += quantQuote.getClose();
			ma10 += quantQuote.getClose();
			ma20 += quantQuote.getClose();
			ma60 += quantQuote.getClose();

			volumeMa5 += quantQuote.getVolume();
			volumeMa10 += quantQuote.getVolume();

			if (i >= 5) {
				ma5 -= list.get(i - 5).getClose();
				quantQuote.setMa5(ma5 / 5f);

				volumeMa5 -= list.get(i - 5).getVolume();
				quantQuote.setVolumeMa5(volumeMa5 / 5f);
			} else {
				quantQuote.setMa5(ma5 / (i + 1f));

				quantQuote.setVolumeMa5(volumeMa5 / (i + 1f));
			}

			if (i >= 10) {
				ma10 -= list.get(i - 10).getClose();
				quantQuote.setMa10(ma10 / 10f);

				volumeMa10 -= list.get(i - 10).getVolume();
				quantQuote.setVolumeMa10(volumeMa10 / 10f);
			} else {
				quantQuote.setMa10(ma10 / (i + 1f));

				quantQuote.setVolumeMa10(volumeMa10 / (i + 1f));
			}

			if (i >= 20) {
				ma20 -= list.get(i - 20).getClose();
				quantQuote.setMa20(ma20 / 20f);
			} else {
				quantQuote.setMa20(ma20 / (i + 1f));
			}

			if (i >= 60) {
				ma60 -= list.get(i - 60).getClose();
				quantQuote.setMa60(ma60 / 60f);
			} else {
				quantQuote.setMa60(ma60 / (i + 1f));
			}
		}
	}

	/**
	 * 计算 EMA
	 */
	protected static void computeEMA(List<QuantQuote> list) {
		Double k5 = 2.0 / (5 + 1.0);
		Double k60 = 2.0 / (60 + 1.0);
		Double ema5 = list.get(0).getClose();
		Double ema60 = list.get(0).getClose();

		for (QuantQuote quote : list) {
			ema5 = quote.getClose() * k5 + ema5 * (1 - k5);
			ema60 = quote.getClose() * k60 + ema60 * (1 - k60);
			quote.setEma5(ema5);
			quote.setEma60(ema60);

		}

		for (int i = 1; i < list.size(); i++) {
			QuantQuote quantQuote = list.get(i);
			quantQuote.ema60max = Collections.max(list.subList(i < 100 ? 0 : i - 100, i), new Comparator<QuantQuote>() {

				@Override
				public int compare(QuantQuote o1, QuantQuote o2) {
					return new Double(o1.getEma60()).compareTo(o2.getEma60());
				}
			}).getEma60();
			quantQuote.ema60min = Collections.max(list.subList(i < 100 ? 0 : i - 100, i), new Comparator<QuantQuote>() {

				@Override
				public int compare(QuantQuote o1, QuantQuote o2) {
					return new Double(o2.getEma60()).compareTo(o1.getEma60());
				}
			}).getEma60();
		}
	}

	/**
	 * 计算 MACD （moving average convergence/divergence）
	 * <p>
	 * EMAtoday=α * Price today + ( 1 - α ) * EMAyesterday;
	 * <p>
	 * The most commonly used values are 12, 26, and 9 days, that is, MACD(12,26,9).
	 * <p>
	 * As true with most of the technical indicators, MACD also finds its period
	 * settings from the old days when technical analysis used to be mainly based on
	 * the daily charts. The reason was the lack of the modern trading platforms
	 * which show the changing prices every moment.
	 * <p>
	 * As the working week used to be 6-days, the period settings of (12, 26, 9)
	 * represent 2 weeks, 1 month and one and a half week. Now when the trading
	 * weeks have only 5 days, possibilities of changing the period settings cannot
	 * be overruled. However, it is always better to stick to the period settings
	 * which are used by the majority of traders as the buying and selling decisions
	 * based on the standard settings further push the prices in that direction.
	 */
	protected static void computeMACD(List<QuantQuote> list) {
		double ema12 = 0;
		double ema26 = 0;
		double diff = 0;
		double dea = 0;
		double macd = 0;

		for (int i = 0; i < list.size(); i++) {
			QuantQuote quantQuote = list.get(i);

			if (i == 0) {
				ema12 = quantQuote.getClose();
				ema26 = quantQuote.getClose();
			} else {
				// EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
				ema12 = ema12 * 11f / 13f + quantQuote.getClose() * 2f / 13f;// 快速移动平均线
				// EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
				ema26 = ema26 * 25f / 27f + quantQuote.getClose() * 2f / 27f;// 慢速移动平均线
			}

			// DIF = EMA（12） - EMA（26） 。
			diff = ema12 - ema26;// 离差值（快速EMA 与慢速EMA的差值）
			// 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
			dea = dea * 8f / 10f + diff * 2f / 10f;// 根据离差值计算其9日的EMA，即离差平均值，是所求的MACD值。为了不与指标原名相混淆，此值又名DEA或DEM。
			// 用（DIF-DEA）*2 即为 MACD 柱状图。
			macd = (diff - dea) * 2f;

			quantQuote.setDiff(diff);
			quantQuote.setDea(dea);
			quantQuote.setMacd(macd);
		}
	}

	/**
	 * 计算 BOLL 需要在计算 MA 之后进行
	 */
	protected static void computeBOLL(List<QuantQuote> list) {
		for (int i = 0; i < list.size(); i++) {
			QuantQuote quantQuote = list.get(i);

			if (i == 0) {
				quantQuote.setMb(quantQuote.getClose());
				quantQuote.setUp(Double.NaN);
				quantQuote.setDn(Double.NaN);
			} else {
				int n = 20;
				if (i < 20) {
					n = i + 1;
				}

				double md = 0;
				for (int j = i - n + 1; j <= i; j++) {
					double c = list.get(j).getClose();
					double m = quantQuote.getMa20();
					double value = c - m;
					md += value * value;
				}

				md = md / (n - 1);
				md = (double) Math.sqrt(md);

				quantQuote.setMb(quantQuote.getMa20());
				quantQuote.setUp(quantQuote.getMb() + 2f * md);
				quantQuote.setDn(quantQuote.getMb() - 2f * md);
			}
		}
	}

	/**
	 * 计算 RSI
	 */
	protected static void computeRSI(List<QuantQuote> list) {
		double rsi1 = 0;
		double rsi2 = 0;
		double rsi3 = 0;
		double rsi1ABSEma = 0;
		double rsi2ABSEma = 0;
		double rsi3ABSEma = 0;
		double rsi1MaxEma = 0;
		double rsi2MaxEma = 0;
		double rsi3MaxEma = 0;

		for (int i = 0; i < list.size(); i++) {
			QuantQuote quantQuote = list.get(i);

			if (i == 0) {
				rsi1 = 0;
				rsi2 = 0;
				rsi3 = 0;
				rsi1ABSEma = 0;
				rsi2ABSEma = 0;
				rsi3ABSEma = 0;
				rsi1MaxEma = 0;
				rsi2MaxEma = 0;
				rsi3MaxEma = 0;
			} else {
				double Rmax = Math.max(0, quantQuote.getClose() - list.get(i - 1).getClose());
				double RAbs = Math.abs(quantQuote.getClose() - list.get(i - 1).getClose());

				rsi1MaxEma = (Rmax + (6f - 1) * rsi1MaxEma) / 6f;
				rsi1ABSEma = (RAbs + (6f - 1) * rsi1ABSEma) / 6f;

				rsi2MaxEma = (Rmax + (12f - 1) * rsi2MaxEma) / 12f;
				rsi2ABSEma = (RAbs + (12f - 1) * rsi2ABSEma) / 12f;

				rsi3MaxEma = (Rmax + (24f - 1) * rsi3MaxEma) / 24f;
				rsi3ABSEma = (RAbs + (24f - 1) * rsi3ABSEma) / 24f;

				rsi1 = (rsi1MaxEma / rsi1ABSEma) * 100;
				rsi2 = (rsi2MaxEma / rsi2ABSEma) * 100;
				rsi3 = (rsi3MaxEma / rsi3ABSEma) * 100;
			}

			quantQuote.setRsi1(rsi1);
			quantQuote.setRsi2(rsi2);
			quantQuote.setRsi3(rsi3);
		}
	}

	/**
	 * 计算 KDJ
	 */
	protected static void computeKDJ(List<QuantQuote> list) {
		double k = 0;
		double d = 0;

		for (int i = 0; i < list.size(); i++) {
			QuantQuote quantQuote = list.get(i);

			int startIndex = i - 8;
			if (startIndex < 0) {
				startIndex = 0;
			}

			double max9 = Double.MIN_VALUE;
			double min9 = Double.MAX_VALUE;
			for (int index = startIndex; index <= i; index++) {
				max9 = Math.max(max9, list.get(index).getHigh());
				min9 = Math.min(min9, list.get(index).getLow());
			}

			// Raw Stochastic Value
			double rsv = 100f * (quantQuote.getClose() - min9) / (max9 - min9);

			if (max9 == min9) {
				rsv = 0;
			}

			if (i == 0) {
				k = rsv;
				d = rsv;
			} else {
				k = (rsv + 2f * k) / 3f;
				d = (k + 2f * d) / 3f;
			}

			quantQuote.setK(k);
			quantQuote.setD(d);
			quantQuote.setJ(3f * k - 2 * d);
		}
	}

	/**
	 * 计算 slope
	 */
	protected static void computeSlope(List<QuantQuote> list) {
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
			double hight = list.get(i).ema60max - list.get(i).ema60min;
			double bottom = list.get(i).ema60min;

			for (int j = 1; j <= 3; j++) {
				QuantQuote temp = list.get(i - 3 + j);
				regression.addData((1500 / 60) * j, 300d * (temp.ema5 - bottom) / hight);
			}
			quantQuote.setSlope5(regression.getSlope());
			regression.clear();
			for (int j = 1; j <= 5; j++) {
				QuantQuote temp = list.get(i - 5 + j);
				regression.addData((1500d / 60) * j, 300d * (temp.ema60 - bottom) / hight);
			}
			quantQuote.setSlope60(regression.getSlope());
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
	}

	/**
	 * 计算 Z-Score（此处计算的是收盘价与20天移动平均线的差值的Z-Score）
	 * <p>
	 * 公式：z-score = (value - mean) / standard deviation;
	 */
	protected static void computeZscore(List<QuantQuote> list) {
		double[] subArray = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			QuantQuote quantQuote = list.get(i);
			subArray[i] = quantQuote.close - quantQuote.ma20;
			if (i < 62) {
				continue;
			}
			// 取59天的数据。
			double std = Math.sqrt(StatUtils.populationVariance(subArray, i - 59, 59));
			quantQuote.zscore = (subArray[i - 1] - StatUtils.mean(subArray, i - 59, 59)) / std;
		}
	}

	public static void main(String[] args) {
		double[] points60 = new double[] { 16.2441, 16.2296, 16.2048, 16.2129, 16.2746, 16.3092 };

		SimpleRegression regression = new SimpleRegression();
		double xInterval5 = points60[5] * 0.15;
		for (int j = 0; j <= 5; j++) {
			regression.addData(j * xInterval5, points60[j]);
		}
	}

	public double getMa5() {
		return ma5;
	}

	public void setMa5(double ma5) {
		this.ma5 = ma5;
	}

	public double getMa10() {
		return ma10;
	}

	public void setMa10(double ma10) {
		this.ma10 = ma10;
	}

	public double getMa20() {
		return ma20;
	}

	public void setMa20(double ma20) {
		this.ma20 = ma20;
	}

	public double getMa60() {
		return ma60;
	}

	public void setMa60(double ma60) {
		this.ma60 = ma60;
	}

	public double getVolumeMa5() {
		return volumeMa5;
	}

	public void setVolumeMa5(double volumeMa5) {
		this.volumeMa5 = volumeMa5;
	}

	public double getVolumeMa10() {
		return volumeMa10;
	}

	public void setVolumeMa10(double volumeMa10) {
		this.volumeMa10 = volumeMa10;
	}

	public double getDea() {
		return dea;
	}

	public void setDea(double dea) {
		this.dea = dea;
	}

	public double getDiff() {
		return diff;
	}

	public void setDiff(double diff) {
		this.diff = diff;
	}

	public double getMacd() {
		return macd;
	}

	public void setMacd(double macd) {
		this.macd = macd;
	}

	public double getK() {
		return k;
	}

	public void setK(double k) {
		this.k = k;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public double getJ() {
		return j;
	}

	public void setJ(double j) {
		this.j = j;
	}

	public double getRsi1() {
		return rsi1;
	}

	public void setRsi1(double rsi1) {
		this.rsi1 = rsi1;
	}

	public double getRsi2() {
		return rsi2;
	}

	public void setRsi2(double rsi2) {
		this.rsi2 = rsi2;
	}

	public double getRsi3() {
		return rsi3;
	}

	public void setRsi3(double rsi3) {
		this.rsi3 = rsi3;
	}

	public double getUp() {
		return up;
	}

	public void setUp(double up) {
		this.up = up;
	}

	public double getMb() {
		return mb;
	}

	public void setMb(double mb) {
		this.mb = mb;
	}

	public double getDn() {
		return dn;
	}

	public void setDn(double dn) {
		this.dn = dn;
	}

	public double getEma5() {
		return ema5;
	}

	public void setEma5(double ema5) {
		this.ema5 = ema5;
	}

	public double getEma60() {
		return ema60;
	}

	public void setEma60(double ema60) {
		this.ema60 = ema60;
	}

	public double getEma60max() {
		return ema60max;
	}

	public void setEma60max(double ema60max) {
		this.ema60max = ema60max;
	}

	public double getEma60min() {
		return ema60min;
	}

	public void setEma60min(double ema60min) {
		this.ema60min = ema60min;
	}

	public double getSlope60() {
		return slope60;
	}

	public void setSlope60(double slope60) {
		this.slope60 = slope60;
	}

	public double getSlope5() {
		return slope5;
	}

	public void setSlope5(double slope5) {
		this.slope5 = slope5;
	}

	public double getZscore() {
		return zscore;
	}

	public void setZscore(double zscore) {
		this.zscore = zscore;
	}

}
