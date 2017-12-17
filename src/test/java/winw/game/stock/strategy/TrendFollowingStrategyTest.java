package winw.game.stock.strategy;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import winw.game.stock.Portfolio;
import winw.game.stock.StockList;
import winw.game.stock.Trade;
import winw.game.stock.TradeLogRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TrendFollowingStrategyTest {

	@Resource
	private TradeLogRepository tradeLogRepository;

	private StrategyBacktesting backtesting = new StrategyBacktesting(new TrendFollowingStrategy());

	private final double init = 100000;

	@Test
	public void testAll() throws Exception {
		tradeLogRepository.deleteAll();
		int count = 0;
		double total = 0;

		Set<String> set = new TreeSet<String>();
		set.addAll(Arrays.asList(StockList.CSI_300));
		set.addAll(Arrays.asList(StockList.CSI_500));
		for (String temp : set) {
			Portfolio portfolio = test(temp);
			if (portfolio == null || portfolio.getTradeList().isEmpty()) {
				continue;
			}
			tradeLogRepository.save(portfolio.getTradeLog());
			count++;
			total += portfolio.getCash();
		}
		double profit = total - init * count;
		System.out.println("Total: "+count + "\t" + percentFormat.format(profit / (init * count)));
	}

	// @Test
	public void testOne() throws Exception {
		Portfolio portfolio = test("sz002839");
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
