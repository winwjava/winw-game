package winw.game.stock.trading;

import java.util.List;

import winw.game.stock.analysis.Indicators;

/**
 * 交易策略。
 * 
 * @author winw
 *
 */
public interface TradingStrategy {

	List<Trade> test(List<Indicators> indicators);
}
