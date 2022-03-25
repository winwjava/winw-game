package winw.game.quant.strategy;

import winw.game.quant.QuantTradingStrategy;

/**
 * 统计套利策略。
 * 
 * <p>
 * 统计套利 是有风险的套利策略，根据历史统计数据执行的低风险套利策略，风险在于历史的数据并不能表明未来，并且也无法避免黑天鹅事件。
 * 
 * <p>
 * 统计套利使用最广泛的是均值回归策略。
 * 
 * <p>
 * 配对交易。
 * 
 * @author winw
 *
 */
public abstract class StatsAarbitrageStrategy extends QuantTradingStrategy {

}
