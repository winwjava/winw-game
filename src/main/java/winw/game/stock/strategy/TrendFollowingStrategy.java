package winw.game.stock.strategy;

import java.util.List;

import winw.game.stock.Portfolio;
import winw.game.stock.Trade;
import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Advise.Market;
import winw.game.stock.analysis.Advise.Signal;
import winw.game.stock.analysis.Indicator;
import winw.game.stock.analysis.TechnicalAnalysis;

/**
 * 趋势跟踪策略。
 * 
 * <p>
 * A trend can only be determined in hindsight, since at any time prices in the
 * future are not known.
 * 
 * <p>
 * ４、用50日均线的走向确定大势，以便确定自己的操作原则和所用指标及参数。
 * 当50日均线向上或基本走平时，市场强势或至少横盘整理，可以参与，当50日均线向下走时，市场处于弱势，不宜参与；
 * 用长期MACD指标来确认短期MACD指标的信号意义，当短周期MACD买入信号得到长周期MACD确认时，则买入信号更加可信（50日均线大势认可法则）。
 * 
 * <p>
 * 参考：https://www.douban.com/note/327926327/
 * 
 * @author winw
 *
 */
public class TrendFollowingStrategy implements InvestmentStrategy {// TODO 成交量指标

	/**
	 * 50日均线确认趋势，MACD零交叉（金叉、死叉）确定买入卖出，
	 */
	public void test(Portfolio portfolio, List<Indicator> indicator) {
		int position = 0; // 持仓
		// double buyPrice = 0;
		for (int i = 50; i < indicator.size(); i++) {
			Indicator current = indicator.get(i - 1);
			Advise macd = TechnicalAnalysis.analysisMACD(indicator.subList(0, i));
			Market market = null;
			// market = TechnicalAnalysis.analysisOBV(indicator.subList(0, i)).getMarket();
			// market = TechnicalAnalysis.analysisVPT(indicator.subList(0, i)).getMarket();

			// 买入
			if (macd.getSignal() == Signal.BUY_SIGNAL && position == 0) {
				if (Market.BEAR_MARKET.equals(market)) {
					// System.out.println(current.getDate() + "\tBEAR_MARKET");
					continue;
				}
				// buyPrice = current.getClose();
				position = portfolio.maxBuy(current.getClose());
				portfolio.trading(new Trade(current.getDate(), current.getClose(), portfolio.maxBuy(current.getClose()),
						current.getDiff(), current.getDea()));
			}

			// 卖出
			if (position > 0 && macd.getSignal() == Signal.SELL_SIGNAL) {
				portfolio.trading(new Trade(current.getDate(), current.getClose(), -position, current.getDiff(),
						current.getDea()));
				position = 0;
			}

			// if (position > 0 && (buyPrice - current.getLow()) / buyPrice >= 0.13) {// 止损
			// // System.out.println("止损");
			// portfolio.trading(new Trade(current.getDate(), current.getClose(),
			// -position));
			// position = 0;
			// continue;
			// }
		}
		if (position > 0) {// 清算
			Indicator last = indicator.get(indicator.size() - 1);

			Trade trade = new Trade(last.getDate(), last.getClose(), -position, last.getDiff(), last.getDea());
			portfolio.trading(trade);
		}
	}

}
