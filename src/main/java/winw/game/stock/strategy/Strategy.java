package winw.game.stock.strategy;

import java.util.List;

import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Indicator;

/**
 * 投资策略。
 * 
 * @author winw
 *
 */
public interface Strategy {

	/**
	 * 分析并返回 Advise。
	 * 
	 * @param indicator
	 * @return Advise
	 */
	Advise analysis(List<Indicator> indicator);

}
