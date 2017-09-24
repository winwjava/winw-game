package winw.game.stock.strategy;

import java.util.List;

import winw.game.stock.Portfolio;
import winw.game.stock.analysis.Indicator;

/**
 * 投资策略。
 * 
 * @author winw
 *
 */
public interface InvestmentStrategy {

	/**
	 * 回测
	 * 
	 * @param portfolio
	 * @param indicator
	 */
	void test(Portfolio portfolio, List<Indicator> indicator);
}
