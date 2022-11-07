package winw.game.quant.strategy;

import winw.game.quant.Portfolio;
import winw.game.quant.QuantTradingStrategy;

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
		// 考虑用BOLL线。或者唐安奇通道

	}

}
