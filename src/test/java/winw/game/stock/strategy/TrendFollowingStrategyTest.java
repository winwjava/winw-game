package winw.game.stock.strategy;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import winw.game.stock.Portfolio;
import winw.game.stock.Quote;
import winw.game.stock.QuoteType;
import winw.game.stock.StockList;
import winw.game.stock.StockQuote;
import winw.game.stock.StockQuoteService;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.Trade;
import winw.game.stock.analysis.Indicator;
import winw.game.stock.analysis.Signal;
import winw.game.stock.analysis.TechnicalAnalysis;

public class TrendFollowingStrategyTest {

	// @Resource
	// private TradeRepository tradeRepository;
	private TrendFollowingStrategy strategy = new TrendFollowingStrategy();
	private StockQuoteService service = new TencentStockQuoteService();

	private StrategyBacktesting backtesting = new StrategyBacktesting(strategy);

	private final double init = 100000;
	
	private int days = 365;

	// private final DecimalFormat decimalFormat = new DecimalFormat("##0.00");
	private final NumberFormat percentFormat = NumberFormat.getPercentInstance();

	@Test
	public void getRange() throws Exception {
		String code = "sh000001";
		StockQuote stockQuote = service.get(code);
		if (stockQuote == null) {
			return;
		}

		List<Quote> list = service.get(code, QuoteType.DAILY_QUOTE, days);

		// 过滤
		
		// 计算技术指标
		List<Indicator> indicators = Indicator.compute(list);

		TechnicalAnalysis.analysis(indicators);
		
		Map<String, String> range = strategy.getRange(indicators);
		
		System.out.println(range);
	}
	
	@Test
	public void testAll() throws Exception {
		int count = 0;
		double profit = 0;
		double totalInvestment = 0;
		List<Portfolio> portfolios = new ArrayList<Portfolio>();
		for (String temp : Arrays.asList(StockList.CSI_300)) {// "sh600436","sz000671", "sh600276", "sh601877"
			if (temp.startsWith("sz300")) {// 去掉创业板
				continue;
			}
			Portfolio portfolio = test(temp);
			if (portfolio == null || portfolio.getTradeList().isEmpty()) {
				continue;
			}
			count++;
			profit += portfolio.getCash() - init;
			totalInvestment += portfolio.getMaxInvestment();
			portfolios.add(portfolio);
		}
		System.out.println(profit);
		System.out.println(totalInvestment);
		System.out.println("Total: " + count + "\t" + percentFormat.format(profit / totalInvestment));

		Collections.sort(portfolios, Comparator.comparing(Portfolio::getCash));
		Collections.reverse(portfolios);
		// 排序后取Top 10 交易
		System.out.println("Top 10 return: ");
		for (int i = 0; i < Math.min(10, portfolios.size()); i++) {
			Portfolio portfolio = portfolios.get(i);
			List<Trade> tradeList = portfolio.getTradeList();
			System.out.println(tradeList.get(0).getCode() + "\t" + tradeList.get(0).getName() + "\t"
					+ percentFormat.format((portfolio.getCash() - init) / portfolio.getMaxInvestment()));
		}
		
		// 将所有交易记录合并起来，然后在时间轴上显示收益率
	}

	@Test
	public void testOne() throws Exception {
		Portfolio portfolio = test("sz002120");
		if (portfolio == null) {
			return;
		}

		// print trade Log
		// for (Trade trade : portfolio.getTradeList()) {
		// System.out.println(trade.toString());
		// }
	}

	public Portfolio test(String code) throws Exception {
		// if (name.startsWith("S") || name.startsWith("ST") || name.startsWith("*ST")
		// || name.startsWith("S*ST")
		// || name.startsWith("SST") || name.startsWith("退市")) {
		// return null;
		// }

		Portfolio portfolio = new Portfolio(init);// 初始金额
		backtesting.testing(portfolio, code, 365);
		return portfolio;
	}

	@Test
	public void testAnalysis() throws Exception {
		// 打印最近一个交易日的金叉和死叉。
		for (String temp : StockList.CSI_300) {
			StockQuote stockQuote = service.get(temp);
			if (stockQuote == null) {
				return;
			}
			List<Quote> quoteList = service.get(stockQuote.getCode(), QuoteType.DAILY_QUOTE, 100);

			List<Indicator> indicatorList = Indicator.compute(quoteList);
			TechnicalAnalysis.analysis(indicatorList);

			Indicator today = indicatorList.get(indicatorList.size() - 1);
			if (today.getSignalList().contains(Signal.ZERO_CROSSOVER)
					|| today.getSignalList().contains(Signal.GOLDEN_CROSSOVER)) {
				System.out.println(stockQuote.getCode() + "\t" + today.getDate() + "\t" + stockQuote.getName() + "\tB");
			}

			if (today.getSignalList().contains(Signal.DEATH_CROSSOVER)) {
				System.out.println(stockQuote.getCode() + "\t" + today.getDate() + "\t" + stockQuote.getName() + "\tS");
			}
		}
	}

}
