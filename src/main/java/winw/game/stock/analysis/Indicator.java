package winw.game.stock.analysis;

import java.util.ArrayList;
import java.util.List;

import winw.game.stock.Quote;

/**
 * 指标。
 * 
 * @author winw
 *
 */
public class Indicator extends Quote {

	private double ma5;// 5日均价
	private double ma10;// 10日均价
	private double ma20;// 20日均价

	private double volumeMa5;// 5日均量
	private double volumeMa10;// 10日均量
	// private double volumeMa20;// 20日均量

	// MACD 指标的三个属性
	private double diff;
	private double dea;
	private double macd;

	// KDJ 指标的三个属性
	private double k;
	private double d;
	private double j;

	// RSI 指标的三个属性
	private double rsi1;
	private double rsi2;
	private double rsi3;

	// BOLL 指标的三个属性
	private double up; // 上轨线
	private double mb; // 中轨线
	private double dn; // 下轨线

	public Indicator(Quote o) {
		super();
		this.date = o.getDate();
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
	public static List<Indicator> compute(List<? extends Quote> quoteList) {
		List<Indicator> list = new ArrayList<Indicator>();
		for (Quote quote : quoteList) {
			list.add(new Indicator(quote));
		}
		computeMA(list);
		computeMACD(list);
		computeBOLL(list);
		computeRSI(list);
		computeKDJ(list);
		return list;
	}

	/**
	 * 计算 MA
	 */
	private static void computeMA(List<Indicator> list) {
		double ma5 = 0;
		double ma10 = 0;
		double ma20 = 0;
		double volumeMa5 = 0;
		double volumeMa10 = 0;

		for (int i = 0; i < list.size(); i++) {
			Indicator indicator = list.get(i);

			ma5 += indicator.getClose();
			ma10 += indicator.getClose();
			ma20 += indicator.getClose();

			volumeMa5 += indicator.getVolume();
			volumeMa10 += indicator.getVolume();

			if (i >= 5) {
				ma5 -= list.get(i - 5).getClose();
				indicator.setMa5(ma5 / 5f);

				volumeMa5 -= list.get(i - 5).getVolume();
				indicator.setVolumeMa5(volumeMa5 / 5f);
			} else {
				indicator.setMa5(ma5 / (i + 1f));

				indicator.setVolumeMa5(volumeMa5 / (i + 1f));
			}

			if (i >= 10) {
				ma10 -= list.get(i - 10).getClose();
				indicator.setMa10(ma10 / 10f);

				volumeMa10 -= list.get(i - 10).getVolume();
				indicator.setVolumeMa10(volumeMa10 / 5f);
			} else {
				indicator.setMa10(ma10 / (i + 1f));

				indicator.setVolumeMa10(volumeMa10 / (i + 1f));
			}

			if (i >= 20) {
				ma20 -= list.get(i - 20).getClose();
				indicator.setMa20(ma20 / 20f);
			} else {
				indicator.setMa20(ma20 / (i + 1f));
			}
		}
	}

	/**
	 * 计算 MACD
	 */
	private static void computeMACD(List<Indicator> list) {// moving average convergence/divergence
		// 指数平均数指标 （Exponential Moving Average）

		// EMAtoday=α * Price today + ( 1 - α ) * EMAyesterday;

		/*
		 * The most commonly used values are 12, 26, and 9 days, that is, MACD(12,26,9).
		 */
		/*
		 * 
		 * As true with most of the technical indicators, MACD also finds its period
		 * settings from the old days when technical analysis used to be mainly based on
		 * the daily charts. The reason was the lack of the modern trading platforms
		 * which show the changing prices every moment.
		 */
		/*
		 * As the working week used to be 6-days, the period settings of (12, 26, 9)
		 * represent 2 weeks, 1 month and one and a half week. Now when the trading
		 * weeks have only 5 days, possibilities of changing the period settings cannot
		 * be overruled. However, it is always better to stick to the period settings
		 * which are used by the majority of traders as the buying and selling decisions
		 * based on the standard settings further push the prices in that direction.
		 */
		double ema12 = 0;
		double ema26 = 0;
		double diff = 0;
		double dea = 0;
		double macd = 0;

		for (int i = 0; i < list.size(); i++) {
			Indicator indicator = list.get(i);

			if (i == 0) {
				ema12 = indicator.getClose();
				ema26 = indicator.getClose();
			} else {
				// EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
				ema12 = ema12 * 11f / 13f + indicator.getClose() * 2f / 13f;// 快速移动平均线
				// EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
				ema26 = ema26 * 25f / 27f + indicator.getClose() * 2f / 27f;// 慢速移动平均线
			}

			// DIF = EMA（12） - EMA（26） 。
			diff = ema12 - ema26;// 离差值（快速EMA 与慢速EMA的差值）
			// 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
			dea = dea * 8f / 10f + diff * 2f / 10f;// 根据离差值计算其9日的EMA，即离差平均值，是所求的MACD值。为了不与指标原名相混淆，此值又名DEA或DEM。
			// 用（DIF-DEA）*2 即为 MACD 柱状图。
			macd = (diff - dea) * 2f;

			indicator.setDiff(diff);
			indicator.setDea(dea);
			indicator.setMacd(macd);
		}
	}

	/**
	 * 计算 BOLL 需要在计算 MA 之后进行
	 */
	private static void computeBOLL(List<Indicator> list) {
		for (int i = 0; i < list.size(); i++) {
			Indicator indicator = list.get(i);

			if (i == 0) {
				indicator.setMb(indicator.getClose());
				indicator.setUp(Double.NaN);
				indicator.setDn(Double.NaN);
			} else {
				int n = 20;
				if (i < 20) {
					n = i + 1;
				}

				double md = 0;
				for (int j = i - n + 1; j <= i; j++) {
					double c = list.get(j).getClose();
					double m = indicator.getMa20();
					double value = c - m;
					md += value * value;
				}

				md = md / (n - 1);
				md = (double) Math.sqrt(md);

				indicator.setMb(indicator.getMa20());
				indicator.setUp(indicator.getMb() + 2f * md);
				indicator.setDn(indicator.getMb() - 2f * md);
			}
		}
	}

	/**
	 * 计算 RSI
	 */
	private static void computeRSI(List<Indicator> list) {
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
			Indicator indicator = list.get(i);

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
				double Rmax = Math.max(0, indicator.getClose() - list.get(i - 1).getClose());
				double RAbs = Math.abs(indicator.getClose() - list.get(i - 1).getClose());

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

			indicator.setRsi1(rsi1);
			indicator.setRsi2(rsi2);
			indicator.setRsi3(rsi3);
		}
	}

	/**
	 * 计算 KDJ
	 */
	private static void computeKDJ(List<Indicator> list) {
		double k = 0;
		double d = 0;

		for (int i = 0; i < list.size(); i++) {
			Indicator indicator = list.get(i);

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

			double rsv = 100f * (indicator.getClose() - min9) / (max9 - min9);
			if (i == 0) {
				k = rsv;
				d = rsv;
			} else {
				k = (rsv + 2f * k) / 3f;
				d = (k + 2f * d) / 3f;
			}

			indicator.setK(k);
			indicator.setD(d);
			indicator.setJ(3f * k - 2 * d);
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Indicator [date=");
		builder.append(date);
		builder.append(", close=");
		builder.append(close);
		builder.append(", diff=");
		builder.append(diff);
		builder.append(", dea=");
		builder.append(dea);
		builder.append(", macd=");
		builder.append(macd);
		builder.append(", k=");
		builder.append(k);
		builder.append(", d=");
		builder.append(d);
		builder.append(", j=");
		builder.append(j);
		builder.append(", rsi1=");
		builder.append(rsi1);
		builder.append(", rsi2=");
		builder.append(rsi2);
		builder.append(", rsi3=");
		builder.append(rsi3);
		builder.append(", up=");
		builder.append(up);
		builder.append(", mb=");
		builder.append(mb);
		builder.append(", dn=");
		builder.append(dn);
		builder.append("]");
		return builder.toString();
	}

}
