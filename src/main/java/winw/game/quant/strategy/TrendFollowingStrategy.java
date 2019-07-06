package winw.game.quant.strategy;

import java.util.List;

import winw.game.quant.Portfolio;
import winw.game.quant.StockQuoteService;
import winw.game.quant.TencentStockQuoteService;
import winw.game.quant.Trade;
import winw.game.quant.analysis.Indicator;

/**
 * 趋势跟踪策略。
 * 
 * @author winw
 *
 */
public class TrendFollowingStrategy extends AbstractStrategy {// TODO 成交量、流动性、市盈率（参考000300）

	// TODO 收益率与国债ETF收益率比较（斜率）

	@Override
	public void trading(String... code) {
		List<Indicator> indicators = getHistoryQuote(code[0]);
		if (indicators == null || indicators.isEmpty()) {
			return;
		}
		Indicator current = indicators.get(indicators.size() - 1);

		if (current.getSlope60() > 0.04 && current.getSlope5() > 0.1
				&& emptyPositionDays.getOrDefault(current.getCode(), 100) > 2 // 卖出后保持空仓天数
				&& portfolio.getPosition(current.getCode()) == 0) {
			Trade order = portfolio.order(current, 1);
			emptyPositionDays.remove(current.getCode());
			notify(order, ", Slope60: " + floatFormat.format(current.getSlope60()) + ", Slope5: "
					+ floatFormat.format(current.getSlope5()));
		}

		if ((current.getSlope60() < 0.03) && portfolio.getPosition(current.getCode()) > 0) {
			Trade order = portfolio.order(current, 0);
			emptyPositionDays.put(current.getCode(), 0);
			notify(order, ", Slope60: " + floatFormat.format(current.getSlope60()) + ", Slope5: "
					+ floatFormat.format(current.getSlope5()));
		}
	}

	public static void main(String[] args) throws Exception {
		StockQuoteService service = new TencentStockQuoteService();
		TrendFollowingStrategy strategy = new TrendFollowingStrategy();
		strategy.setStockQuoteService(service);
		strategy.backtesting("2015-01-01", "2019-07-05", 12640, new Portfolio(1000000), "sh600519");
	}

}
