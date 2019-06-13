package winw.game.stock.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import winw.game.stock.Portfolio;
import winw.game.stock.Quote;
import winw.game.stock.QuoteType;
import winw.game.stock.StockQuote;
import winw.game.stock.StockQuoteService;
import winw.game.stock.analysis.Indicator;
import winw.game.stock.util.MailService;

/**
 * 投资策略回溯测试。
 * 
 * @author winw
 *
 */
public abstract class StrategyBacktesting {

	protected MailService mailService = new MailService();

	protected StockQuoteService stockQuoteService;

	public StockQuoteService getStockQuoteService() {
		return stockQuoteService;
	}

	public void setStockQuoteService(StockQuoteService stockQuoteService) {
		this.stockQuoteService = stockQuoteService;
	}

	private final int observation = 50;

	protected Portfolio portfolio;

	protected static final NumberFormat percentFormat = new DecimalFormat("##.##%");

	public void testing(String code, String from, String to, int days) throws Exception {
		StockQuote stockQuote = stockQuoteService.get(code);
		if (stockQuote == null) {
			return;
		}
		List<Quote> list = stockQuoteService.get(code, QuoteType.DAILY_QUOTE, from, to, days);
		if (list == null || list.isEmpty()) {
			return;
		}

		// 计算技术指标
		List<Indicator> indicators = Indicator.compute(list, days);

		for (int i = observation; i < indicators.size(); i++) {// 从第50天开始，各种指标的误差可以忽略
			List<Indicator> subList = indicators.subList(0, i);
			trading(subList);
		}

		String offsetDate = DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(to, "yyyy-MM-dd"), -15),
				"yyyy-MM-dd");

		double marketValue = 0;
		Map<String, Integer> positions = portfolio.getPositions();
		for (String c : positions.keySet()) {
			if (positions.get(c) > 0) {
				List<Quote> temp = stockQuoteService.get(c, QuoteType.DAILY_QUOTE, offsetDate, to, 50);
				marketValue += positions.get(c) * temp.get(temp.size() - 1).getClose();
			}
		}
		portfolio.setMarketValue(marketValue);
	}

	public abstract void trading(List<Indicator> indicators);
}
