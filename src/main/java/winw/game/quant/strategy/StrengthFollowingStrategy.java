package winw.game.quant.strategy;

import winw.game.quant.Portfolio;

/**
 * 替代趋势跟踪策略。使用物理的力量与加速度作为趋势的判断依据。
 * 
 * <p>
 * 向上或向下力量强弱，在横向时间轴上会形成加速度。一般上涨和下跌都有一个买卖双方统一认识（价值发现）的过程。 <br>
 * 所以，大部分涨跌是符合抛物线形态的。
 * 
 * <p>
 * 周K线的趋势可以代表中长期趋势（日K线的趋势并不可靠，比如下跌趋势中的小反弹）。
 * 
 * @author winw
 *
 */
public abstract class StrengthFollowingStrategy extends StatsAarbitrageStrategy {

	@Override
	public void trading(Portfolio portfolio) {
		// 周K线向上，可以

	}

}
