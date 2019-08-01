package winw.game.quant;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 所有投资策略的基类。
 * <p>
 * 支持回溯测试。
 * <p>
 * 包含止盈止损规则。
 * 
 * @author winw
 *
 */
public abstract class QuantTradingStrategy extends QuantTrading {
	private Logger logger = LoggerFactory.getLogger(QuantTradingStrategy.class);

	protected static final NumberFormat floatFormat = new DecimalFormat("#.##");
	protected static final NumberFormat percentFormat = new DecimalFormat("#.##%");

	private String[] samplesAppendShbond() {
		return ArrayUtils.addAll(samples(), SH_BOND_ETF);
	}

	private void quantTrading(Portfolio portfolio) {
		// 调整持仓可卖。
		for (Position position : portfolio.getPositions().values()) {
			position.setSellable(position.getSize());
		}

		// FIXME 复权处理。更新持仓数量和持仓价。记录复权信息。

		// 模拟交易
		trading(portfolio);
		// 交易后止损。
		stoploss(portfolio);
		// 其他资产买入时，国债可以随时卖出。
		// tradingBond(portfolio);
	}

	public String mockTrading(Portfolio portfolio) throws Exception {
		String today = DateFormatUtils.format(new Date(), datePattern);
		String from = queryFrom(today);

		for (String temp : samplesAppendShbond()) {
			historyQuote.put(temp, queryHistoryQuote(from, today, temp));
		}
		// 量化交易
		quantTrading(portfolio);

		portfolio.setMarketValue(getMarketValue(portfolio.getPositions().values(), today));
		return getTradingLog(portfolio);
	}

	public void backTesting(Portfolio portfolio, String from, String to) throws Exception {
		String from0 = queryFrom(from);
		for (String temp : samplesAppendShbond()) {
			quoteCache.put(temp, queryHistoryQuote(from0, to, temp));
		}

		// 以csi300的每日交易日期为基准。
		for (QuantQuote temp : queryHistoryQuote(from0, to, CSI_300)) {
			currentDate = temp.getDate();
			if (currentDate.compareTo(from) <= 0) {
				continue;
			}
			// 更新当前的历史交易数据。
			updateHistoryQuote();
			// 量化交易
			quantTrading(portfolio);
		}
		portfolio.setMarketValue(getMarketValue(portfolio.getPositions().values(), to));
		printTradingLog(portfolio, from, to);
	}

	private String getTradingLog(Portfolio portfolio) {
		String subject = portfolio.toString();
		StringBuilder html = new StringBuilder("<b>").append(subject).append("</b><br>");
		logger.info(subject);
		for (Order order : portfolio.getOrderList()) {
			logger.info(order.toString());
			html.append(order.toString()).append("<br>");
		}
		for (Position position : portfolio.getPositions().values()) {
			logger.info(position.toString());
			html.append(position.toString()).append("<br>");
		}
		return html.toString();
	}

	private void printTradingLog(Portfolio portfolio, String from, String to) throws Exception {
		for (Order order : portfolio.getOrderList()) {
			logger.info("{}, {}", order.getDate(), order.toString());
		}
		logger.info("{}, backtesting from {} to {}, return: {}, samples: {}", this.getClass().getSimpleName(), from, to,
				portfolio.getReturnRate(), samples());
	}

	private void updateHistoryQuote() {// 根据样本，及时同步。
		for (String code : quoteCache.keySet()) {
			List<QuantQuote> list = quoteCache.get(code);
			if (list == null) {
				continue;
			}
			for (int i = 0; i < list.size(); i++) {
				String date = list.get(i).getDate();
				if (date.compareTo(currentDate) <= 0) {
					continue;
				}
				historyQuote.put(code, list.subList(0, i));
				break;
			}
		}
	}

	/**
	 * 止盈止损。
	 * <p>
	 * 满足以下条件之一时，以收盘价卖出平仓：
	 * <ol>
	 * <li>最大损失止损m%
	 * <li>最大利润止盈m%
	 * <li>回落平仓，n天内股价回落m%
	 * <li>横盘平仓，n天内涨幅小于m%
	 * </ol>
	 * <p>
	 * 一笔成功的交易是：无论获利与否，都要适时的离场。
	 * 
	 */
	private void stoploss(Portfolio portfolio) {
		Map<QuantQuote, String> orders = new HashMap<QuantQuote, String>();
		for (Position position : portfolio.getPositions().values()) {
			position.addHoldingDays(1);
			String code = position.getCode();
			QuantQuote current = getCurrentQuote(code);
			double profit = position.getReturnRate(current.getClose());
			// n天内涨幅小于m%离场。
			// if(positionDays.get(code) >= 5 && increase <= 0.01)
			// slowness
			// 控制亏损，亏损2%离场。
			if (profit < -portfolio.getStoplossLimit()) {
				orders.put(current, "stoploss: " + percentFormat.format(profit));
				continue;
			}
			// 控制回撤，回撤5%离场。
			double drawdown = position.getDrawdown(current.getClose());
			if (drawdown > portfolio.getDrawdownLimit()) {
				orders.put(current, "drawdown: " + percentFormat.format(drawdown));
			}
		}
		for (QuantQuote temp : orders.keySet()) {
			portfolio.order(temp, -1, orders.get(temp));
		}
	}

}
