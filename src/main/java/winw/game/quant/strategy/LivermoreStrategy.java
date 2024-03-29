package winw.game.quant.strategy;

/**
 * 华尔街投机大鳄“利弗莫尔”买卖法则：买入20%仓位，下跌10%止损， 上涨10%立即再加20%
 * 
 * <ol>
 * <li>先买入20%
 * <li>假设买错了，下跌10%立即止损，损失金额为总仓位的2%。
 * <li>假设买对了，上涨10%立即加仓20%，
 * <li>再上涨10%立即再加20%......
 * <li>最后一次直接加40%。将胜利的成果扩大。
 * <li>然后只要没有跌破10%就持有，一旦跌10%立即将仓位全部卖出。
 * </ol>
 * 
 * @author winw
 *
 */
public abstract class LivermoreStrategy {// 是跟随“涨停敢死队”的简单有效的策略。

}
