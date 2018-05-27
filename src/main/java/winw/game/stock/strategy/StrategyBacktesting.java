package winw.game.stock.strategy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import winw.game.stock.Portfolio;
import winw.game.stock.Quote;
import winw.game.stock.QuoteType;
import winw.game.stock.StockQuote;
import winw.game.stock.StockQuoteService;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.Trade;
import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Advise.Trading;
import winw.game.stock.analysis.Indicator;
import winw.game.stock.analysis.TechnicalAnalysis;

/**
 * 投资策略回溯测试。
 * 
 * @author winw
 *
 */
public class StrategyBacktesting {

	private Strategy strategy;

	private StockQuoteService service = new TencentStockQuoteService();

	public StrategyBacktesting(Strategy strategy) {
		super();
		this.strategy = strategy;
	}

	public void testing(Portfolio portfolio, String code, int days) throws Exception {//TODO , Date from, Date to
		StockQuote stockQuote = service.get(code);
		if (stockQuote == null) {
			return;
		}

		List<Quote> list = service.get(code, QuoteType.DAILY_QUOTE, days);

		// 过滤
		
		// 计算技术指标
		List<Indicator> indicators = Indicator.compute(list.subList(formIndex(days, list), list.size()));
		// 技术分析
		TechnicalAnalysis.analysis(indicators);

		testing(stockQuote, portfolio, indicators);
	}

	/**
	 * 取不超过days时间的天
	 * @param days
	 * @param list
	 * @return
	 * @throws ParseException
	 */
	private int formIndex(int days, List<Quote> list) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, (int) (-days - (50 * 1.2)));
		Date start = calendar.getTime();
		DateFormat dateFormat = new SimpleDateFormat(Quote.DATE_PATTERN);
		int from = 0;// -1
		for (int i = 0; i < list.size(); i++) {
			if (start.before(dateFormat.parse(list.get(i).getDate()))) {
				from = i;
				break;
			}
		}
		return from;
	}

	protected void testing(StockQuote stockQuote, Portfolio portfolio, List<Indicator> indicator) {
		int position = 0; // 持仓
		for (int i = 50; i < indicator.size(); i++) {// 从第50开始，各种指标的误差可以忽略
			Indicator current = indicator.get(i - 1);
			Indicator yestday = indicator.get(i - 2);
			current.setCode(stockQuote.getCode());
			current.setName(stockQuote.getName());

			Advise advise = strategy.analysis(indicator.subList(0, i));

			if (advise.getSignal() == Trading.BUY_SIGNAL && position == 0) {// 买入
				int buy = portfolio.maxBuy(current.getClose()) / 2;// 建仓
				trading(portfolio, current, yestday, buy);
				position += buy;
			} else if (advise.getSignal() == Trading.BUY_SIGNAL && position > 0) {// 买入
				int buy = portfolio.maxBuy(current.getClose());// 加仓
				trading(portfolio, current, yestday, buy);
				position += buy;
			}

			if (advise.getSignal() == Trading.SELL_SIGNAL && position > 0) {// 卖出
				trading(portfolio, current, yestday, -position);
				position = 0;
			}

			// if (position > 0 && (buyPrice - current.getLow()) / buyPrice >= 0.13) {// 止损
		}
		if (position > 0) {// 清算
			trading(portfolio, indicator.get(indicator.size() - 1), indicator.get(indicator.size() - 2), -position);
		}
	}

	protected void trading(Portfolio portfolio, Indicator current, Indicator yestday, int position) {
		Trade trade = new Trade(current.getDate(), current.getClose(), position);
		trade.setCode(current.getCode());
		trade.setName(current.getName());
		trade.setDiff(current.getDiff());
		trade.setDea(current.getDea());
		trade.setMacd(current.getMacd());

		trade.setSlope((current.getDiff() - yestday.getDiff()) / (1 - 0));
		portfolio.trading(trade);
	}
}
