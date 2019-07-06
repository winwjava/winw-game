package winw.game.quant.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import winw.game.quant.Portfolio;
import winw.game.quant.Quote;
import winw.game.quant.QuoteType;
import winw.game.quant.StockQuote;
import winw.game.quant.StockQuoteService;
import winw.game.quant.Trade;
import winw.game.quant.analysis.Indicator;
import winw.game.quant.util.MailService;

/**
 * 所有投资策略的基类。
 * <p>
 * 支持回溯测试。
 * <p>
 * 包含止盈止损规则。
 * <p>
 * 交易信号邮件通知。
 * 
 * 
 * @author winw
 *
 */
public abstract class AbstractStrategy {

	private Logger logger = LoggerFactory.getLogger(AbstractStrategy.class);

	protected StockQuoteService stockQuoteService;

	public StockQuoteService getStockQuoteService() {
		return stockQuoteService;
	}

	public void setStockQuoteService(StockQuoteService stockQuoteService) {
		this.stockQuoteService = stockQuoteService;
	}

	private final int observation = -120;

	protected Portfolio portfolio;

	protected static final NumberFormat floatFormat = new DecimalFormat("#.###");
	protected static final NumberFormat percentFormat = new DecimalFormat("#.##%");

	// TODO 实现同时操作多只股票，提高资金利用率。

	public Portfolio backtesting(String from, String to, int days, Portfolio portfolio, String... codes)
			throws Exception {
		// System.out.println("Backtesting: " + code + ", from " + from + " to " + to);
		this.portfolio = portfolio;
		backtesting(from, to, days, codes);
		logger.info("{} backtesting: {}, from {} to {}, profit: {}", this.getClass().getSimpleName(), codes, from, to,
				percentFormat.format(portfolio.getProfitRate()));
		return this.portfolio;
	}

	private List<Indicator> getHistoryQuote(String from, String to, int days, String code) throws Exception {
		StockQuote stockQuote = stockQuoteService.get(code);
		if (stockQuote == null) {
			return null;
		}
		List<Quote> list = stockQuoteService.get(code, QuoteType.DAILY_QUOTE, from, to, days);
		if (list == null || list.isEmpty()) {
			return null;
		}

		// 计算技术指标
		return Indicator.compute(list, days);
	}

	private String currentDate;

	private Map<String, List<Indicator>> currentHistoryQuote = new HashMap<String, List<Indicator>>();

	private Map<String, List<Indicator>> quoteCache = new HashMap<String, List<Indicator>>();

	protected String getCurrentDate() {
		return currentDate;
	}

	protected List<Indicator> getHistoryQuote(String code) {
		return currentHistoryQuote.get(code);
	}

	/**
	 * 收盘价上记录的是当前实时价格。
	 * 
	 * @param code
	 * @return
	 */
	protected Indicator getCurrentQuote(String code) {
		List<Indicator> list = getHistoryQuote(code);
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	private void updateHistoryQuote() {
		for (String code : quoteCache.keySet()) {
			List<Indicator> list = quoteCache.get(code);
			if (list == null) {
				continue;
			}
			for (int i = 0; i < list.size(); i++) {
				String date = list.get(i).getDate();
				if (date.compareTo(currentDate) <= 0) {
					continue;
				}
				currentHistoryQuote.put(code, list.subList(0, i));
				break;
			}
		}
	}

	/**
	 * 实现具体的交易。
	 * <p>
	 * getHistoryQuote方法可以获取到每个code的历史数据。
	 * 
	 * @param codes
	 */
	public abstract void trading(String... codes);

	private void backtesting(String from, String to, int days, String... codes) throws Exception {
		// 数据从observation开始加载
		String from0 = addDays(from, observation * 7 / 5 - 11);
		for (String temp : codes) {
			quoteCache.put(temp, getHistoryQuote(from0, to, days, temp));
		}

		// 从from开始，以csi300的日期为基准。
		List<Indicator> csi300 = getHistoryQuote(from0, to, days, "sh000300");
		for (Indicator temp : csi300) {
			currentDate = temp.getDate();
			if (currentDate.compareTo(from) <= 0) {
				continue;
			}
			// 更新当前的历史交易数据。
			updateHistoryQuote();
			// 模拟交易
			trading(codes);
			// 交易后止损。
			stoploss();
		}

		// 计算回测完毕后持仓的市值。
		// 从to的15天前开始取数据，以获取to当天的收盘价
		double marketValue = 0;
		String from1 = addDays(to, -15);
		Map<String, Integer> positions = portfolio.getPositions();
		for (String c : positions.keySet()) {
			if (positions.get(c) > 0) {
				List<Quote> temp = stockQuoteService.get(c, QuoteType.DAILY_QUOTE, from1, to, 50);
				marketValue += positions.get(c) * temp.get(temp.size() - 1).getClose();
			}
		}
		portfolio.setMarketValue(marketValue);
	}

	// public abstract void close(List<Indicator> indicators);
	// public abstract void open(List<Indicator> indicators);

	/**
	 * 最大回撤
	 */
	protected double maxDrawdown = 0.05;

	// 最高市值
	protected Map<String, Double> largestValues = new HashMap<String, Double>();
	// 持仓天数
	protected Map<String, Integer> positionDays = new HashMap<String, Integer>();
	// 空仓天数
	protected Map<String, Integer> emptyPositionDays = new HashMap<String, Integer>();

	public double getMaxDrawdown() {
		return maxDrawdown;
	}

	public void setMaxDrawdown(double maxDrawdown) {
		this.maxDrawdown = maxDrawdown;
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
	protected void stoploss() {
		// 只保留 Positions中有的记录
		Map<String, Integer> positions = portfolio.getPositions();
		positionDays.keySet().retainAll(positions.keySet());
		largestValues.keySet().retainAll(positions.keySet());
		for (String code : positions.keySet()) {
			Indicator current = getCurrentQuote(code);
			double positionCost = portfolio.getPositionCost(code);
			positionDays.put(code, positionDays.getOrDefault(code, -1) + 1);
			double increase = (current.getClose() - positionCost) / positionCost;
			if (increase < -0.01 // 亏损超过1%，止损
			// || (positionDays.get(code) >= 5 && increase <= 0.01) // n天内涨幅小于m%离场。
			) {
				Trade order = portfolio.order(current, 0);
				notify(order, ", SlowRise: " + percentFormat.format(increase) + ", Profit: "
						+ percentFormat.format(increase));
				positionDays.remove(code);
				largestValues.remove(code);
				emptyPositionDays.put(code, 0);
				continue;
			}
			// 最大回撤
			double marketValue = positions.get(code) * current.getClose();
			double largestValue = Math.max(marketValue, largestValues.getOrDefault(code, new Double(0)));
			largestValues.put(code, largestValue);
			double drawdown = (1 - marketValue / largestValue);
			if (drawdown > maxDrawdown) {
				Trade order = portfolio.order(current, 0);
				notify(order, ", Drawdown: " + percentFormat.format(drawdown) + ", Profit: "
						+ percentFormat.format(increase));
				positionDays.remove(code);
				largestValues.remove(code);
				emptyPositionDays.put(code, 0);
			}
		}

		for (String code : emptyPositionDays.keySet()) {
			emptyPositionDays.put(code, emptyPositionDays.get(code) + 1);
		}
	}

	private boolean mailNotify = false;

	protected MailService mailService = new MailService();

	protected boolean notify(Trade order, String text) {
		String ss = order + text;
		if (order.getCount() > 0) {
			logger.info(ss);
		} else {
			logger.info(ss);
		}
		if (mailNotify) {
			return mailService.send(order.toString(), text);
		}
		return true;
	}

	public static String addDays(String date, int amount) throws ParseException {
		return DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(date, "yyyy-MM-dd"), amount), "yyyy-MM-dd");
	}
}
