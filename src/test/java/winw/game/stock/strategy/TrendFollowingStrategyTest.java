package winw.game.stock.strategy;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import winw.game.stock.Portfolio;
import winw.game.stock.StockList;
import winw.game.stock.Trade;

public class TrendFollowingStrategyTest {

//	@Resource
//	private TradeRepository tradeRepository;

	private StrategyBacktesting backtesting = new StrategyBacktesting(new TrendFollowingStrategy());

	private final double init = 100000;

	@Test
	public void testAll() throws Exception {
		// tradeRepository.deleteAll();
		int count = 0;
		double totalInvestment = 0;
		double profit = 0;

		Set<String> set = new TreeSet<String>();
		// set.addAll(Arrays.asList(StockList.CSI_300));
		set.addAll(Arrays.asList(StockList.CSI_300));
		for (String temp : set) {
			if (temp.startsWith("sz300")) {// 去掉创业板
				continue;
			}
			
			Portfolio portfolio = test(temp);
			if (portfolio == null || portfolio.getTradeList().isEmpty()) {
				continue;
			}
			// tradeRepository.save(portfolio.getTradeLog());
			count++;
			totalInvestment += portfolio.getMaxInvestment();

			profit += portfolio.getCash() - init;
		}

		// double profit = total - init * count;
		System.out.println("Total: " + count + "\t" + percentFormat.format(profit / totalInvestment));
		// System.out.println("Total: " + count + "\t" + percentFormat.format(profit /
		// (init * count)));
	}

	@Test
	public void testOne() throws Exception {
		Portfolio portfolio = test("sz000718");
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

		backtesting.testing(code, 365, portfolio);

		percentFormat.setMinimumFractionDigits(2);

		double profit = portfolio.getCash() - init;

		System.out.println(code + "\t" + percentFormat.format(profit / init));
		return portfolio;
	}

}
