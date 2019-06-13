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
public class TrendFollowingStrategy extends StoplossStrategy {

	@Override
	public void trading(List<Indicator> indicators) {// TODO 成交量
		Indicator current = indicators.get(indicators.size() - 1);
		if (current.getSlope60() > 0.05 && current.getSlope5() > 0.05
				&& portfolio.getPosition(current.getCode()) == 0) {
			System.out.println("Slope60: " + current.getSlope60() + ", Slope5: " + current.getSlope5());
			Trade order = portfolio.order(current, 1);
			String subject = current.getDate() + "[B]" + order.getCode();
			System.out.println(subject + ", " + order);
			mailService.send(subject, order);
		}

		if (current.getSlope60() < 0.04 && portfolio.getPosition(current.getCode()) > 0) {
			Trade order = portfolio.order(current, 0);
			String subject = current.getDate() + "[S]" + order.getCode();
			System.out.println(subject + ", " + order);
			mailService.send(subject, order);
		}
		super.trading(indicators);
	}

}
