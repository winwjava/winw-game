package winw.game.quant;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 报价的量化指标。
 * 
 * @author winw
 *
 */
public class QuoteIndex extends Quote {

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

	protected double ema60max;
	protected double ema60min;

	private double z;
	private double s;
	private double l;

	public QuoteIndex() {
		super();
	}

	public QuoteIndex(Quote o) {
		super();
		this.code = o.getCode();
		this.name = o.getName();

		this.date = o.getDate();
		this.time = o.getTime();

		this.open = o.getOpen();
		this.high = o.getHigh();
		this.low = o.getLow();
		this.close = o.getClose();
		this.volume = o.getVolume();
		this.amount = o.getAmount();
	}

	/**
	 * 计算 MA MACD BOLL RSI KDJ 指标
	 * 
	 * @param quoteList
	 * @return
	 */
	public static <T extends QuoteIndex> List<T> compute(List<T> list) {
		computeMA(list);
		computeEMA(list);
		computeMACD(list);
		return list;
	}

	/**
	 * 计算 MA
	 */
	protected static <T extends QuoteIndex> void computeMA(List<T> list) {
		double ma5 = 0;
		double ma10 = 0;
		double ma20 = 0;
		double ma60 = 0;
		double volumeMa5 = 0;
		double volumeMa10 = 0;

		for (int i = 0; i < list.size(); i++) {
			QuoteIndex quoteIndex = list.get(i);

			ma5 += quoteIndex.getClose();
			ma10 += quoteIndex.getClose();
			ma20 += quoteIndex.getClose();
			ma60 += quoteIndex.getClose();

			volumeMa5 += quoteIndex.getVolume();
			volumeMa10 += quoteIndex.getVolume();

			if (i >= 5) {
				ma5 -= list.get(i - 5).getClose();
				quoteIndex.setMa5(ma5 / 5f);

				volumeMa5 -= list.get(i - 5).getVolume();
				quoteIndex.setVolumeMa5(volumeMa5 / 5f);
			} else {
				quoteIndex.setMa5(ma5 / (i + 1f));

				quoteIndex.setVolumeMa5(volumeMa5 / (i + 1f));
			}

			if (i >= 10) {
				ma10 -= list.get(i - 10).getClose();
				quoteIndex.setMa10(ma10 / 10f);

				volumeMa10 -= list.get(i - 10).getVolume();
				quoteIndex.setVolumeMa10(volumeMa10 / 10f);
			} else {
				quoteIndex.setMa10(ma10 / (i + 1f));

				quoteIndex.setVolumeMa10(volumeMa10 / (i + 1f));
			}

			if (i >= 20) {
				ma20 -= list.get(i - 20).getClose();
				quoteIndex.setMa20(ma20 / 20f);
			} else {
				quoteIndex.setMa20(ma20 / (i + 1f));
			}

			if (i >= 60) {
				ma60 -= list.get(i - 60).getClose();
				quoteIndex.setMa60(ma60 / 60f);
			} else {
				quoteIndex.setMa60(ma60 / (i + 1f));
			}
		}
	}

	/**
	 * 计算 EMA
	 */
	protected static <T extends QuoteIndex> void computeEMA(List<T> list) {
		Double k5 = 2.0 / (5 + 1.0);
		Double k60 = 2.0 / (60 + 1.0);
		Double ema5 = list.get(0).getClose();
		Double ema60 = list.get(0).getClose();

		for (QuoteIndex quote : list) {
			ema5 = quote.getClose() * k5 + ema5 * (1 - k5);
			ema60 = quote.getClose() * k60 + ema60 * (1 - k60);
			quote.setEma5(ema5);
			quote.setEma60(ema60);
		}

		for (int i = 1; i < list.size(); i++) {
			QuoteIndex quoteIndex = list.get(i);
			quoteIndex.ema60max = Collections.max(list.subList(i < 100 ? 0 : i - 100, i), new Comparator<QuoteIndex>() {

				@Override
				public int compare(QuoteIndex o1, QuoteIndex o2) {
					return Double.valueOf(o1.getEma60()).compareTo(o2.getEma60());
				}
			}).getEma60();
			quoteIndex.ema60min = Collections.max(list.subList(i < 100 ? 0 : i - 100, i), new Comparator<QuoteIndex>() {

				@Override
				public int compare(QuoteIndex o1, QuoteIndex o2) {
					return Double.valueOf(o2.getEma60()).compareTo(o1.getEma60());
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
	protected static <T extends QuoteIndex> void computeMACD(List<T> list) {
		double ema12 = 0;
		double ema26 = 0;
		double diff = 0;
		double dea = 0;
		double macd = 0;

		for (int i = 0; i < list.size(); i++) {
			QuoteIndex quoteIndex = list.get(i);

			if (i == 0) {
				ema12 = quoteIndex.getClose();
				ema26 = quoteIndex.getClose();
			} else {
				// EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
				ema12 = ema12 * 11f / 13f + quoteIndex.getClose() * 2f / 13f;// 快速移动平均线
				// EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
				ema26 = ema26 * 25f / 27f + quoteIndex.getClose() * 2f / 27f;// 慢速移动平均线
			}

			// DIF = EMA（12） - EMA（26） 。
			diff = ema12 - ema26;// 离差值（快速EMA 与慢速EMA的差值）
			// 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
			dea = dea * 8f / 10f + diff * 2f / 10f;// 根据离差值计算其9日的EMA，即离差平均值，是所求的MACD值。为了不与指标原名相混淆，此值又名DEA或DEM。
			// 用（DIF-DEA）*2 即为 MACD 柱状图。
			macd = (diff - dea) * 2f;

			quoteIndex.setDiff(diff);
			quoteIndex.setDea(dea);
			quoteIndex.setMacd(macd);
		}
	}

	/**
	 * 计算 BOLL 需要在计算 MA 之后进行
	 */
	protected static <T extends QuoteIndex> void computeBOLL(List<T> list) {
		for (int i = 0; i < list.size(); i++) {
			QuoteIndex quoteIndex = list.get(i);

			if (i == 0) {
				quoteIndex.setMb(quoteIndex.getClose());
				quoteIndex.setUp(Double.NaN);
				quoteIndex.setDn(Double.NaN);
			} else {
				int n = 20;
				if (i < 20) {
					n = i + 1;
				}

				double md = 0;
				for (int j = i - n + 1; j <= i; j++) {
					double c = list.get(j).getClose();
					double m = quoteIndex.getMa20();
					double value = c - m;
					md += value * value;
				}

				md = md / (n - 1);
				md = (double) Math.sqrt(md);

				quoteIndex.setMb(quoteIndex.getMa20());
				quoteIndex.setUp(quoteIndex.getMb() + 2f * md);
				quoteIndex.setDn(quoteIndex.getMb() - 2f * md);
			}
		}
	}

	/**
	 * 计算 RSI
	 */
	protected static <T extends QuoteIndex> void computeRSI(List<T> list) {
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
			QuoteIndex quoteIndex = list.get(i);

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
				double Rmax = Math.max(0, quoteIndex.getClose() - list.get(i - 1).getClose());
				double RAbs = Math.abs(quoteIndex.getClose() - list.get(i - 1).getClose());

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

			quoteIndex.setRsi1(rsi1);
			quoteIndex.setRsi2(rsi2);
			quoteIndex.setRsi3(rsi3);
		}
	}

	/**
	 * 计算 KDJ
	 */
	protected static <T extends QuoteIndex> void computeKDJ(List<T> list) {
		double k = 0;
		double d = 0;

		for (int i = 0; i < list.size(); i++) {
			QuoteIndex quoteIndex = list.get(i);

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
			double rsv = 100f * (quoteIndex.getClose() - min9) / (max9 - min9);

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

			quoteIndex.setK(k);
			quoteIndex.setD(d);
			quoteIndex.setJ(3f * k - 2 * d);
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

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getL() {
		return l;
	}

	public void setL(double l) {
		this.l = l;
	}

	public double getS() {
		return s;
	}

	public void setS(double s) {
		this.s = s;
	}

}
