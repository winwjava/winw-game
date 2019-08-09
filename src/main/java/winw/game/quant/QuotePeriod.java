package winw.game.quant;

/**
 * 报价周期。
 * 
 * 分为实时报价和分段报价，分段报价有：每日、每周、每月。
 * 
 * @author winw
 *
 */
public enum QuotePeriod {
	/**
	 * 实时报价。
	 */
	REALTIME,
	/**
	 * 每日报价。
	 */
	DAILY,
	/**
	 * 每周报价。
	 */
	WEEKLY,
	/**
	 * 每月报价。
	 */
	MONTHLY,
}
