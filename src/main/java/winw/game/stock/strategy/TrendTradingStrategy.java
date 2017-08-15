package winw.game.stock.strategy;

import java.util.ArrayList;
import java.util.List;

import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Advise.Signal;
import winw.game.stock.analysis.Indicators;
import winw.game.stock.analysis.TechnicalAnalysis;

/**
 * 趋势交易策略。
 * 
 * @author winw
 *
 */
public class TrendTradingStrategy implements TradingStrategy{

	public List<Trade> test(List<Indicators> indicators) {
		boolean isHold = false; // 是否持仓
		List<Trade> tradeLog = new ArrayList<Trade>();
		for (int i = 50; i < indicators.size(); i++) {
			Indicators current = indicators.get(i);
			Advise advise = TechnicalAnalysis.macdAnalysis(indicators.subList(0, i));
			if (advise.getSignal() == Signal.BUY_SIGNAL && !isHold) {
				isHold = true;
				tradeLog.add(new Trade(current.getDate(), current.getClose(), 500));
			}

			if (advise.getSignal() == Signal.SELL_SIGNAL && isHold) {
				isHold = false;
				tradeLog.add(new Trade(current.getDate(), current.getClose(), -500));
			}
		}
		return tradeLog;
	}

}
