package winw.game.quant.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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

	public Portfolio backtesting(String code, String from, String to, int days, Portfolio portfolio) throws Exception {
		// System.out.println("Backtesting: " + code + ", from " + from + " to " + to);
		this.portfolio = portfolio;
		backtesting(code, from, to, days);
		logger.info("{} backtesting: {}, from {} to {}, profit: {}", this.getClass().getSimpleName(), code, from, to,
				percentFormat.format(portfolio.getProfitRate()));
		return this.portfolio;
	}

	public void backtesting(String code, String from, String to, int days) throws Exception {
		// 数据从observation开始加载
		String from0 = DateFormatUtils.format(
				DateUtils.addDays(DateUtils.parseDate(from, "yyyy-MM-dd"), observation * 7 / 5 - 11), "yyyy-MM-dd");
		StockQuote stockQuote = stockQuoteService.get(code);
		if (stockQuote == null) {
			return;
		}
		List<Quote> list = stockQuoteService.get(code, QuoteType.DAILY_QUOTE, from0, to, days);
		if (list == null || list.isEmpty()) {
			return;
		}

		// 计算技术指标
		List<Indicator> indicators = Indicator.compute(list, days);

		// 从from开始
		for (int i = 1; i < indicators.size(); i++) {
			String date = indicators.get(i).getDate();
			if (date.compareTo(from) <= 0) {
				continue;
			}
			List<Indicator> subList = indicators.subList(0, i + 1);
			if (subList.size() == 0) {
				return;
			}
			trading(subList);// 模拟交易
		}

		// 从to的15天前开始取数据，以获取to当天的收盘价
		String from1 = DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(to, "yyyy-MM-dd"), -15),
				"yyyy-MM-dd");

		double marketValue = 0;
		Map<String, Integer> positions = portfolio.getPositions();
		for (String c : positions.keySet()) {
			if (positions.get(c) > 0) {
				List<Quote> temp = stockQuoteService.get(c, QuoteType.DAILY_QUOTE, from1, to, 50);
				marketValue += positions.get(c) * temp.get(temp.size() - 1).getClose();
			}
		}
		portfolio.setMarketValue(marketValue);
	}

	public abstract void trading(List<Indicator> indicators);

	// public abstract void close(List<Indicator> indicators);
	// public abstract void open(List<Indicator> indicators);

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
	 * @param indicators
	 */
	public void stoploss(List<Indicator> indicators) {
		// 只保留 Positions中有的记录
		Map<String, Integer> positions = portfolio.getPositions();
		positionDays.keySet().retainAll(positions.keySet());
		largestValues.keySet().retainAll(positions.keySet());
		for (String code : positions.keySet()) {
			Indicator current = indicators.get(indicators.size() - 1);
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

	public double getMaxDrawdown() {
		return maxDrawdown;
	}

	public void setMaxDrawdown(double maxDrawdown) {
		this.maxDrawdown = maxDrawdown;
	}

}
