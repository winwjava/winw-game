package winw.game.quant;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
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
public abstract class QuantTradingStrategy {
	private Logger logger = LoggerFactory.getLogger(QuantTradingStrategy.class);

	public final static String CSI_300 = "sh000300";// 沪深三百
	public final static String SH_BOND = "sh000012";// 上证国债
	public final static String CSI_300_ETF = "sh510300";// 沪深三百ETF
	public final static String SH_BOND_ETF = "sh511010";// 上证国债ETF
	public final static String SH_GOLD_ETF = "sh518880";// 上证黄金ETF

	public final static String[] CSI_300_TOP = { // 沪深300十大权重股。市值大、流动性好。
			// 中国平安// 贵州茅台// 招商银行// 格力电器// 美的集团
			"sh601318", "sh600519", "sh600036", "sh601166", "sz000651",
			// 兴业银行// 五 粮 液// 伊利股份// 恒瑞医药// 中信证券
			"sz000333", "sz000858", "sh600276", "sh600887", "sh600030" };

	protected int observation = -120;

	protected String currentDate;

	protected String getCurrentDate() {
		return currentDate;
	}

	/**
	 * @return 样本代码。
	 */
	public abstract String[] samples();

	private String[] load() {
		return ArrayUtils.addAll(samples(), CSI_300);
	}

	/**
	 * 实现具体的交易。
	 * <p>
	 * getHistoryQuote方法可以获取到每个code的历史数据。
	 * 
	 * @param codes
	 */
	public abstract void trading(Portfolio portfolio);

	protected QuoteService quoteService = new TencentQuoteService();

	Map<String, List<QuantQuote>> quoteCache = new HashMap<String, List<QuantQuote>>();

	Map<String, List<QuantQuote>> historyQuote = new HashMap<String, List<QuantQuote>>();

	protected QuantQuote getCurrentQuote(String code) {// 收盘价上记录的是当前实时价格。
		List<QuantQuote> list = getHistoryQuote(code);
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	/**
	 * 当前历史交易数据（从缓存中取）。
	 * 
	 * @param code
	 * @return
	 */
	protected List<QuantQuote> getHistoryQuote(String code) {
		return historyQuote.get(code);// 如果为空，则考虑用stockQuoteService查询一次。
	}

	protected List<QuantQuote> queryHistoryQuote(String from, String to, String code) throws Exception {
		Quote quoteDetail = quoteService.get(code);
		if (quoteDetail == null) {
			return null;
		}
		List<Quote> list = quoteService.get(code, QuotePeriod.DAILY, from, to);
		if (list == null || list.isEmpty()) {
			return null;
		}

		// 计算技术指标
		return QuantQuote.compute(list);
	}

	protected double getMarketValue(Collection<Position> positions, String to) throws Exception {
		double marketValue = 0;
		String from1 = addDays(to, -15);// 从to的15天前开始取数据，以获取to当天的收盘价
		for (Position position : positions) {
			List<Quote> temp = quoteService.get(position.getCode(), QuotePeriod.DAILY, from1, to);
			marketValue += position.getSize() * temp.get(temp.size() - 1).getClose();
		}
		return marketValue;
	}

	/**
	 * 是否趋势向上。
	 * 
	 * @return
	 */
	public boolean marketTrend() {
		// 根据CSI_300的60天均线。分析市场趋势

		// 市场处于向上趋势，调整为趋势跟踪策略
		// 市场处于向下或震荡趋势，调整为均值回归策略
		QuantQuote current = getCurrentQuote(CSI_300);
		return current.getSlope60() > 0.04 && current.getSlope5() > 0.04;
	}

	private void trading0(Portfolio portfolio) {
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
	}

	public String mockTrading(Portfolio portfolio) throws Exception {
		String today = today();
		String from = queryFrom(observation, today);

		for (String temp : load()) {
			historyQuote.put(temp, queryHistoryQuote(from, today, temp));
		}
		// 量化交易
		trading0(portfolio);

		portfolio.setMarketValue(getMarketValue(portfolio.getPositions().values(), today));
		return getTradingLog(portfolio);
	}
	public void backTesting(Portfolio portfolio, String from, String to) throws Exception {
		backTesting(portfolio, from, to, true);
	}

	public void backTesting(Portfolio portfolio, String from, String to, boolean log) throws Exception {
		String from0 = queryFrom(observation, from);
		for (String temp : load()) {
			quoteCache.put(temp, queryHistoryQuote(from0, to, temp));
		}
		// 以csi300的每日交易日期为基准。
		for (QuantQuote temp : quoteCache.get(CSI_300)) {
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
		if(log) {
			printTradingLog(portfolio, from, to);
		}
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
		logger.info("{}, Backtesting from {} to {}, profit: {}, samples: {}", this.getClass().getSimpleName(), from, to,
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
	protected void stoploss(Portfolio portfolio) {
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
				orders.put(current, "stoploss: " + percentFormat(profit));
				continue;
			}
			// 控制回撤，回撤5%离场。
			double drawdown = position.getDrawdown(current.getClose());
			if (drawdown > portfolio.getDrawdownLimit()) {
				orders.put(current, "drawdown: " + percentFormat(drawdown));
			}
		}
		for (QuantQuote temp : orders.keySet()) {
			portfolio.order(temp, -1, orders.get(temp));
		}
	}

	public QuoteService getStockQuoteService() {
		return quoteService;
	}

	public void setStockQuoteService(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	protected final static String datePattern = "yyyy-MM-dd";
	protected static final NumberFormat floatFormat = new DecimalFormat("#.##");
	protected static final NumberFormat percentFormat = new DecimalFormat("#.##%");

	public static String queryFrom(int observation, String from) throws ParseException {
		return addDays(from, observation * 7 / 5 - 11);
	}

	public static String addDays(String date, int amount) throws ParseException {
		return DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(date, datePattern), amount), datePattern);
	}

	public static String today() {
		return DateFormatUtils.format(new Date(), datePattern);
	}

	public static String percentFormat(Object obj) {
		return percentFormat.format(obj);
	}

}
