package winw.game.quant.strategy;

import java.util.List;

import winw.game.quant.Portfolio;
import winw.game.quant.StockQuoteService;
import winw.game.quant.TencentStockQuoteService;
import winw.game.quant.Trade;
import winw.game.quant.analysis.Indicator;

/**
 * 在市场中应用这种策略的方法是寻找极端事件，然后打赌事情将恢复到接近平均水平。
 * 
 * <p>
 * Standard Deviation/RSI/Bollinger Bands
 * <p>
 * 价格距离移动平均线越远，其回归的可能性就越大。
 * 
 * @author winw
 *
 */
public class MeanReversionStrategy extends AbstractStrategy {

	/**
	 * 用 Z-Score 实现
	 */
	@Override
	public void trading(List<Indicator> indicators) {
		Indicator current = indicators.get(indicators.size() - 1);

		if (current.getZscore() < -2 && portfolio.getPosition(current.getCode()) == 0) {
			Trade order = portfolio.order(current, 1);
			notify(order, ", Z-Score: " + floatFormat.format(current.getZscore()));
		}

		if (current.getZscore() >= 1 && portfolio.getPosition(current.getCode()) > 0) {
			Trade order = portfolio.order(current, 0);
			notify(order, ", Z-Score: " + floatFormat.format(current.getZscore()));
		}
		super.stoploss(indicators);
	}

	public static void main(String[] args) throws Exception {
		StockQuoteService service = new TencentStockQuoteService();
		MeanReversionStrategy strategy = new MeanReversionStrategy();
		strategy.setStockQuoteService(service);
		strategy.backtesting("sh600519", "2015-01-01", "2019-07-05", 12640, new Portfolio(1000000));
	}

}
