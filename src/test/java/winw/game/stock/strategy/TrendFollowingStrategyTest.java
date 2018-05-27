package winw.game.stock.strategy;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

	private StockQuoteService service = new TencentStockQuoteService();

	private StrategyBacktesting backtesting = new StrategyBacktesting(new TrendFollowingStrategy());

	private final double init = 1000000;

	@Test
	public void testAll() throws Exception {
		List<Portfolio> portfolios = new ArrayList<Portfolio>();
		for (String temp : Arrays.asList(StockList.CSI_300)) {
			if (temp.startsWith("sz300")) {// 去掉创业板
				continue;
			}
			Portfolio portfolio = test(temp);
			if (portfolio == null || portfolio.getTradeList().isEmpty()) {
				continue;
			}
			portfolios.add(portfolio);
		}
		Collections.sort(portfolios, Comparator.comparing(Portfolio::getCash));
		Collections.reverse(portfolios);
		// 排序后取Top 10 交易
		System.out.println("Top 10 return: ");
		for (int i = 0; i < Math.min(10, portfolios.size()); i++) {
			Portfolio portfolio = portfolios.get(i);
			List<Trade> tradeList = portfolio.getTradeList();
			System.out.println(tradeList.get(0).getCode() + "\t" + tradeList.get(0).getName() + "\t"
					+ percentFormat.format((portfolio.getCash() - init) / init));
		}
		// System.out.println("Total: " + count + "\t" + percentFormat.format(profit /
		// totalInvestment));
	}

	@Test
	public void testOne() throws Exception {
		Portfolio portfolio = test("sz399300");
		if (portfolio == null) {
			return;
		}

		// print trade Log
		for (Trade trade : portfolio.getTradeList()) {
			System.out.println(trade.toString());
		}
	}

	// private final DecimalFormat decimalFormat = new DecimalFormat("##0.00");
	private final NumberFormat percentFormat = NumberFormat.getPercentInstance();

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
				System.out.println(stockQuote.getCode() + "\t" + stockQuote.getName() + "\tB");
			}

			if (today.getSignalList().contains(Signal.DEATH_CROSSOVER)) {
				System.out.println(stockQuote.getCode() + "\t" + stockQuote.getName() + "\tS");
			}
		}
	}

}
