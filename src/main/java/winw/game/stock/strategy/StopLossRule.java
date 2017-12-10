package winw.game.stock.strategy;

/**
 * 止盈止损规则。
 * 
 * <p>
 * 一笔成功的交易是：无论获利与否，都要适时的离场。
 * 
 * <p>
 * 满足以下条件之一时，以收盘价卖出平仓
 * <ol>
 * <li>最大损失止损m%
 * <li>最大利润止盈m%
 * <li>回落平仓，n天内股价回落m%
 * <li>横盘平仓，n天内涨幅小于m%
 * </ol>
 * 
 * @author winw
 *
 */
public class StopLossRule {// TODO 止盈止损规则

	private double stopLoss = 0.05;// 止损

	public double getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}

}
