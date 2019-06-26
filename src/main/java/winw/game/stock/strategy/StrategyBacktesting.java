package winw.game.stock.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import winw.game.stock.Portfolio;
import winw.game.stock.Quote;
import winw.game.stock.QuoteType;
import winw.game.stock.StockQuote;
import winw.game.stock.StockQuoteService;
import winw.game.stock.Trade;
import winw.game.stock.analysis.Indicator;
import winw.game.stock.util.MailService;

/**
 * 投资策略回溯测试。
 * 
 * @author winw
 *
 */
public abstract class StrategyBacktesting {
	private Logger logger = LoggerFactory.getLogger(StrategyBacktesting.class);

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

	public Portfolio testing(String code, String from, String to, int days, Portfolio portfolio) throws Exception {
		// System.out.println("Backtesting: " + code + ", from " + from + " to " + to);
		this.portfolio = portfolio;
		testing(code, from, to, days);
		logger.info("{} backtesting: {}, from {} to {}, profit: {}",this.getClass().getSimpleName(), code, from, to,
				percentFormat.format(portfolio.getProfitRate()));
		return this.portfolio;
	}

	public void testing(String code, String from, String to, int days) throws Exception {
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
}
