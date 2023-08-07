package winw.game.quant.strategy;

import winw.game.quant.Portfolio;
import winw.game.quant.QuantTradingStrategy;

/**
 * 日内倒卖策略，属于日内回转交易。
 * 
 * <Point>
 * 考虑配对交易。Engle-Granger分析，满足协整的标准。
 * 
 * <Point>
 * A想100元买入，B想105卖出。
 * 
 * <Point>
 * 下面是日内均值回归策略。 在当日低价是买入，高价时卖出。收盘时保持半仓。
 * <Point>
 * 每日低买高卖策略，以当天接近低价买入，在第二天有非常大的概率可以高于买入价卖出，即便是在下跌趋势中。
 * <Point>
 * 适合趋势向下（或趋势震荡）时操作。趋势向上是跟踪趋势即可。
 * <Point>
 * 如何接近最低价买入：
 * <ul>
 * <li>盘中下跌1%，则买入10%仓位，总仓位是10%，比较接近最低价；
 * <li>盘中下跌2%，则买入20%仓位，总仓位是30%，是最低价的；
 * <li>盘中下跌4%，则买入60%仓位，总仓位是90%；
 * <li>如果尾盘是最低价，并且下跌超过1%，则满仓；
 * </ul>
 * <Point>
 * 如果以相对高价卖出，首先尽量高于成本价卖出：
 * <ul>
 * <li>开盘，委托利润超过1%清仓；
 * <li>每个整点，如果不亏钱则清仓；
 * <li>尾盘如果还没卖掉，则再等1天，第二天尾盘还没卖掉，则清仓止损；
 * </ul>
 * 
 * <Point>
 * 扣除手续费成本0.6%。理论上当日最低价与次日平均价之间的价差符合正态分布，期望年化收益率超过50%。
 * 
 * <Point>
 * 利润超过1%清仓，还是0.5%，取决于回测，以及最近价格的波动率。
 * 
 * @author winw
 *
 */
public class IntradayScalpStrategy extends QuantTradingStrategy {

	/**
	 * 需要开盘时间、每个整点，收盘前5分钟看盘交易。
	 */
	@Override
	public void trading(Portfolio portfolio) {

	}

}
