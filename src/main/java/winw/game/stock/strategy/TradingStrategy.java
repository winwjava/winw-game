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
	 * @param portfolio
	 * @param indicator
	 * @return
	 */
	double test(Portfolio portfolio, List<Indicator> indicator);
}
