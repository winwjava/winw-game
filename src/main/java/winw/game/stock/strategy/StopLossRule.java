package winw.game.stock.strategy;

/**
 * 止盈止损规则。
 * 
 * <p>
 * 一笔成功的交易是：无论获利与否，都要适时的离场。
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
