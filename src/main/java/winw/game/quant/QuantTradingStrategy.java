package winw.game.quant;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 量化交易策略。
 * <p>
 * 支持模拟交易和回溯测试。
 * <p>
 * 包含止盈止损规则。
 * 
 * @author winw
 *
 */
public abstract class QuantTradingStrategy extends QuantTradingBase {
	private Logger logger = LoggerFactory.getLogger(QuantTradingStrategy.class);

	// private String name;
	protected List<String> samples = new ArrayList<String>(Arrays.asList(CSI_300));

	protected int observation = -120;

	public QuantTradingStrategy() {
		super();
	}

	public QuantTradingStrategy(String[] samples) {
		super();
		this.samples.addAll(Arrays.asList(samples));
	}

	/**
	 * 子类实现该方法用于选样。
	 * 
	 * @return 样本代码。
	 */
	public List<String> samples() {
		return samples;
	}

	/**
	 * 添加样本代码。
	 * 
	 * @param samples
	 */
	public void addSamples(String... samples) {
		this.samples.addAll(Arrays.asList(samples));
	}

	/**
	 * 添加样本代码。
	 * 
	 * @param samples
	 */
	public void addSamples(Collection<String> samples) {
		this.samples.addAll(samples);
	}

	/**
	 * 实现具体的交易。
	 * <p>
	 * getHistoryQuote方法可以获取到每个code的历史数据。
	 * 
	 * @param codes
	 */
	public abstract void trading(Portfolio portfolio);

	protected double getMarketValue(Collection<Position> positions, String to) throws Exception {
		double marketValue = 0;
		String from1 = Quote.addDays(to, -15);// 从to的15天前开始取数据，以获取to当天的收盘价
		for (Position position : positions) {
			List<Quote> temp = quoteService.get(Quote.class, position.getCode(), from1, to);
			marketValue += position.getSize() * temp.get(temp.size() - 1).getClose();
		}
		return marketValue;
	}

	private void trading0(Portfolio portfolio) {
		// 调整持仓可卖。
		for (Position position : portfolio.getPositions().values()) {
			position.setSellable(position.getSize());
		}
		// TODO 复权处理。更新持仓数量和持仓价。记录复权信息。

		// 撤销前一日的订单。
		portfolio.cancelBatch();
		// 模拟交易
		trading(portfolio);
		// 其他资产买入时，国债可以随时卖出。
	}

	public String mockTrading(Portfolio portfolio) throws Exception {
		String today = Quote.today();
		String from0 = Quote.offset(today, observation);
		for (String temp : samples()) {
			historyQuote.put(temp, queryHistoryQuote(from0, today, temp));
		}

		trading0(portfolio);// 量化交易

		portfolio.setMarketValue(getMarketValue(portfolio.getPositions().values(), today));

		StringBuilder html = new StringBuilder();
		for (Order order : portfolio.getOrderList()) {
			logger.info(order.toString());
			html.append(order.toString()).append("<br>");
		}
		html.append("<hr/>");
		for (Position position : portfolio.getPositions().values()) {
			position.update(getQuoteIndex(position.getCode()));
			logger.info(position.toString());
			html.append(position.toString()).append("<br>");
		}
		String subject = portfolio.toString();
		logger.info(subject);
		return html.append("<hr/><b>").append(subject).append("</b>").toString();
	}

	public void backTesting(Portfolio portfolio, String from, String to) throws Exception {
		backTesting(portfolio, from, to, true);
	}

	public void backTesting(Portfolio portfolio, String from, String to, boolean log) throws Exception {
		String from0 = Quote.offset(from, observation);
		for (String temp : samples()) {
			quoteCache.put(temp, queryHistoryQuote(from0, to, temp));
		}
		// 以csi300的每日交易日期为基准。
		for (QuoteIndex temp : quoteCache.get(CSI_300)) {
			currentDate = temp.getDate();
			if (currentDate.compareTo(from) <= 0) {
				continue;
			}
			// 更新当前的历史交易数据。
			updateHistoryQuote();
			// 量化交易
			trading0(portfolio);
		}
		portfolio.setMarketValue(getMarketValue(portfolio.getPositions().values(), to));

		if (!log) {
			return;
		}
		for (Order order : portfolio.getOrderList()) {
			logger.info("{}, {}", order.getDate(), order.toString());
		}
		logger.info("{}, Backtesting from {} to {}, profit: {}, samples: {}", this.getClass().getSimpleName(), from, to,
				portfolio.getProfit(), samples());
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
	protected void stoploss(Portfolio portfolio) {// 涨跌停的买卖操作。
		for (Position position : portfolio.getPositions().values()) {
			position.addHoldingDays(1);
			String code = position.getCode();
			QuoteIndex current = getQuoteIndex(code);
			double profit = position.getReturnRate(current.getClose());
			// n天内涨幅小于m%离场。
			// if(positionDays.get(code) >= 5 && increase <= 0.01)
			// slowness
			// 控制亏损，亏损2%离场。
			if (profit < -portfolio.getStoplossLimit()) {
				portfolio.addBatch(current, 0, "stoploss: " + percentFormat(profit));
				continue;
			}
			// 控制回撤，回撤5%离场。
			double drawdown = position.getDrawdown(current.getClose());
			if (drawdown > portfolio.getDrawdownLimit()) {
				portfolio.addBatch(current, 0, "drawdown: " + percentFormat(drawdown));
			}
		}
	}

	protected static final NumberFormat percentFormat = new DecimalFormat("#.##%");

	public static String percentFormat(Object obj) {
		return percentFormat.format(obj);
	}

}
