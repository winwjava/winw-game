package winw.game.quant.strategy;

import winw.game.quant.Portfolio;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.QuoteIndex;

/**
 * 日内突破策略，属于日内回转交易。
 * 
 * 
 * <p>
 * 又称“RANGE BREAK”，或称打开范围突破（ORB，Opening range breakthrough）–包括利用打开时建立的方向偏差。
 * <p>
 * 向上突破时买入，向下突破时卖出。
 * 
 * <p>
 * 当发现判断错误时，应及时平仓纠正。
 * 
 * <p>
 * 尾盘平仓，只保留一半作为底仓。
 * 
 * @author winw
 *
 */
public class IntradayBreakthroughStrategy extends QuantTradingStrategy {

	@Override
	public void trading(Portfolio portfolio) {
		// 唐奇安通道，或者用BOLL线。
		// 向上突破时买入，向下突破时卖出。

		for (String code : samples()) {
			if (SH_BOND_ETF.equals(code) || CSI_300.equals(code)) {
				continue;
			}
			QuoteIndex today = getQuoteIndex(code, 0);
			QuoteIndex yesterday = getQuoteIndex(code, -1);
			if(yesterday == null) {
				continue;
			}
			if (today.getS() > 0.05
					&& portfolio.getEmptyPositionDays(today.getCode(), 100) > 2 // 卖出后保持空仓天数
					&& !portfolio.hasPosition(today.getCode())
					&& today.getEma60() > yesterday.getEma60()) {
				portfolio.addBatch(today, 1, String.format("SlopeL: %.2f", today.getL()));
			}

			// 考虑用20日线。卖出更可靠。
			if (today.getS() <= 0.02) {
				portfolio.addBatch(today, 0, String.format("SlopeL: %.2f", today.getL()));
			}
		}
		stoploss(portfolio);
		portfolio.commitBatch();
	}

}
