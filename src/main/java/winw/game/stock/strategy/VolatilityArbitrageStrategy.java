package winw.game.stock.strategy;

import java.util.List;

import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Indicator;

/**
 * 波动套利策略。
 * 
 * <p>
 * 均值回归理论
 * 
 * <p>
 * RSI是典型的指标。
 * 
 * @author winw
 *
 */
public class VolatilityArbitrageStrategy implements Strategy {

	@Override
	public Advise analysis(List<Indicator> indicator) {
		return null;
	}

}
