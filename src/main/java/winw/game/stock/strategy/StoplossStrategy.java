package winw.game.stock.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import winw.game.stock.Trade;
import winw.game.stock.analysis.Indicator;

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
public abstract class StoplossStrategy extends StrategyBacktesting {// 最大回撤

	/**
	 * 最大回撤
	 */
	protected double maxDrawdown = 0.05;

	// 最高市值
	protected Map<String, Double> largestValues = new HashMap<String, Double>();
	// 持仓天数
	protected Map<String, Integer> positionDays = new HashMap<String, Integer>();
	// 空仓天数
	protected Map<String, Integer> emptyPositionDays = new HashMap<String, Integer>();

	public void trading(List<Indicator> indicators) {
		// 只保留 Positions中有的记录
		Map<String, Integer> positions = portfolio.getPositions();
		positionDays.keySet().retainAll(positions.keySet());
		largestValues.keySet().retainAll(positions.keySet());
		for (String code : positions.keySet()) {
			Indicator current = indicators.get(indicators.size() - 1);
			double positionCost = portfolio.getPositionCost(code);
			// 横盘平仓，n天内涨幅小于m%
			positionDays.put(code, positionDays.getOrDefault(code, -1) + 1);
			double increase = (current.getClose() - positionCost) / positionCost;
			if (increase < -0.01 || (positionDays.get(code) >= 3 && increase <= 0.01)) {
				Trade order = portfolio.order(current, 0);
				notify(order, ", SlowRise: " + percentFormat.format(increase) + ", Profit: "
						+ percentFormat.format(increase));
				positionDays.remove(code);
				largestValues.remove(code);
				emptyPositionDays.put(code, 0);
				continue;
			}

			double marketValue = positions.get(code) * current.getClose();
			double largestValue = Math.max(marketValue, largestValues.getOrDefault(code, new Double(0)));
			largestValues.put(code, largestValue);
			double drawdown = (1 - marketValue / largestValue);
			if (drawdown > maxDrawdown) {
				Trade order = portfolio.order(current, 0);
				notify(order, ", Drawdown: " + percentFormat.format(drawdown) + ", Profit: "
						+ percentFormat.format(increase));
				positionDays.remove(code);
				largestValues.remove(code);
				emptyPositionDays.put(code, 0);
			}
		}

		for (String code : emptyPositionDays.keySet()) {
			emptyPositionDays.put(code, emptyPositionDays.get(code) + 1);
		}
	}

	public double getMaxDrawdown() {
		return maxDrawdown;
	}

	public void setMaxDrawdown(double maxDrawdown) {
		this.maxDrawdown = maxDrawdown;
	}

}
