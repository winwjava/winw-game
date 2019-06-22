package winw.game.stock.strategy;

import java.util.List;

import winw.game.stock.Trade;
import winw.game.stock.analysis.Indicator;

/**
 * 趋势跟踪策略。
 * 
 * @author winw
 *
 */
public class TrendFollowingStrategy extends StoplossStrategy {// TODO 成交量、流动性、市盈率

	@Override
	public void trading(List<Indicator> indicators) {
		Indicator current = indicators.get(indicators.size() - 1);

		if (current.getSlope60() > 0.04 && current.getSlope5() > 0.1 && portfolio.getPosition(current.getCode()) == 0
				&& emptyPositionDays.getOrDefault(current.getCode(), 100) > 2) {
			Trade order = portfolio.order(current, 1);
			emptyPositionDays.remove(current.getCode());
			notify(order, ", Slope60: " + floatFormat.format(current.getSlope60()) + ", Slope5: "
					+ floatFormat.format(current.getSlope5()));
		}

		if ((current.getSlope60() < 0.02) && portfolio.getPosition(current.getCode()) > 0) {
			Trade order = portfolio.order(current, 0);
			emptyPositionDays.put(current.getCode(), 0);
			notify(order, ", Slope60: " + floatFormat.format(current.getSlope60()) + ", Slope5: "
					+ floatFormat.format(current.getSlope5()));
		}
		super.trading(indicators);
	}

}
