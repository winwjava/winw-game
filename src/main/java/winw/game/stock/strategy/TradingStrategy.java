package winw.game.stock.strategy;

import java.util.List;

import winw.game.stock.analysis.Indicator;

/**
 * 交易策略。
 * 
 * @author winw
 *
 */
public interface TradingStrategy {

	/**
	 * 回测
	 * 
	 * @param portfolio
	 * @param indicator
	 */
	void test(Portfolio portfolio, List<Indicator> indicator);
}
