package winw.game.quant.strategy;

import java.util.List;

import winw.game.quant.Portfolio;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.QuoteIndex;

/**
 * 日内趋势策略，属于日内回转交易。资金分为两半，一半作为底仓，一半作为日内回转资金。
 * 
 * <p>
 * 日内回转交易，顾名思义就是在一天内完成“买”和“卖”两个相反方向的操作（可一次也可多次），也就是“T+0”交易。
 * 
 * 由于A股采用的是“T+1”交易制度（期货采用“T+0”），无法直接进行日内回转交易，需要先配置一定的底仓再进行回转交易。
 * 
 * <p>
 * 常见的日内回转交易策略还有：
 * <ul>
 * <li>动量策略（Momentum strategies）——寻求利用价格的快速上涨。
 * <li>突破策略（breakthrough strategies）–寻求利用支撑/阻力突破、高/低突破。
 * <li>倒卖策略（Scalping strategies）–一种专门从事快速获利的交易方式。
 * <li>打开范围突破（ORB，Opening range breakthrough）–包括利用打开时建立的方向偏差。
 * <li>高频交易–算法交易（High Frequency），包括套利或买卖买卖差价。
 * </ul>
 * 
 * <p>
 * 使用蒙特卡洛分析预测。
 * 
 * @author winw
 *
 */
public class IntradayTrendStrategy extends QuantTradingStrategy {

	public IntradayTrendStrategy() {
		super();
		this.samples.clear();
		this.samples.add("sz002475");// 立讯
	}

	@Override
	public void trading(Portfolio portfolio) {
		// 根据加权的5日平均线和分时平均线交易。
		// 只要波动率比较高，下跌中保持不亏损就可以持续盈利。

		// 每60分钟看一次盘面。

		// 资金分为两半，一半作为底仓。一半作为日内回转资金。

		// 10点钟看一次盘面。预判当天的趋势（基于统计，强化学习，历史总会不断重演）。

		// 高开高走（趋势向上）：买入，高价卖出或尾盘卖出。
		// 高开低走：早盘清仓，尾盘买入。
		// 高开，冲高回落
		// 低开高走（）：买入，高价卖出或尾盘卖出。
		// 低开低走：早盘清仓，尾盘买入。
		// 低开，探底回升
		// 全天横盘
		// 收盘价，开盘

		for (String code : samples()) {
			List<QuoteIndex> quoteIndexs = getHistoryQuote(code);
			if (quoteIndexs == null || quoteIndexs.isEmpty()) {
				continue;
			}
			QuoteIndex today = getQuoteIndex(code, 0);
			QuoteIndex yesterday = getQuoteIndex(code, -1);

			if (today.getDiff() > 0 && yesterday.getDiff() < 0 && today.getMacd() > 0) {
				portfolio.addBatch(today, 1, "GoldenCrossover");
			}
			if (today.getMacd() < 0 && yesterday.getMacd() > 0) {
				portfolio.addBatch(today, -1, "DeathCrossover");
			}
		}
		// TODO 考虑日内止损？
//		stoploss(portfolio);
		portfolio.commitBatch();
	}

}
