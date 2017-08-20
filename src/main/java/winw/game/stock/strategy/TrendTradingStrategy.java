package winw.game.stock.strategy;

import java.util.List;

import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Advise.Signal;
import winw.game.stock.analysis.Indicator;
import winw.game.stock.analysis.TechnicalAnalysis;

/**
 * 趋势交易策略。
 * 
 * @author winw
 *
 */
public class TrendTradingStrategy implements TradingStrategy {

	public double test(Portfolio portfolio, List<Indicator> indicator) {
		int position = 0; // 持仓
		for (int i = 50; i < indicator.size(); i++) {
			Indicator current = indicator.get(i - 1);
			Advise advise = TechnicalAnalysis.macdAnalysis(indicator.subList(0, i));
			if (advise.getSignal() == Signal.BUY_SIGNAL && position == 0) {
				position = portfolio.maxBuy(current.getClose());
				portfolio.trading(
						new Trade(current.getDate(), current.getClose(), portfolio.maxBuy(current.getClose())));
			}

			if (advise.getSignal() == Signal.SELL_SIGNAL && position > 0) {
				portfolio.trading(new Trade(current.getDate(), current.getClose(), -position));
				position = 0;
			}
		}
		
		return portfolio.getCash() + position * indicator.get(indicator.size() -1).getClose();
	}

}
