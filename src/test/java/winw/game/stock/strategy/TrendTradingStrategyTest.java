package winw.game.stock.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.junit.Test;

import winw.game.stock.QuoteType;
import winw.game.stock.StockList;
import winw.game.stock.StockQuote;
import winw.game.stock.StockQuoteService;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.analysis.Advise.Market;
import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Indicator;
import winw.game.stock.analysis.TechnicalAnalysis;

public class TrendTradingStrategyTest {

	private StockQuoteService service = new TencentStockQuoteService();

	private TrendTradingStrategy strategy = new TrendTradingStrategy();

	private final double init = 100000;

	@Test
	public void testAll() throws Exception {
		int count = 0;
		double total = 0;
		for (String temp : StockList.CSI_300) {
			Portfolio portfolio = test(temp);
			if (portfolio == null || portfolio.getTradeLog().isEmpty()) {
				continue;
			}
			count++;
			total += portfolio.getCash();
		}
		double profit = total - init * count;
		System.out.println("CSI_300\t" + count + "\t" + decimalFormat.format(profit) + "\t"
				+ percentFormat.format(profit / (init * count)));
	}

	@Test
	public void testOne() throws Exception {
		Portfolio portfolio = test("sz000503");
		if (portfolio == null) {
			return;
		}

		// print trade Log
		for (Trade trade : portfolio.getTradeLog()) {
			System.out.println(trade.toString());
		}
	}

	private final DecimalFormat decimalFormat = new DecimalFormat("##0.00");
	private final NumberFormat percentFormat = NumberFormat.getPercentInstance();

	public Portfolio test(String code) throws Exception {
		StockQuote stockQuote = service.get(code);
		if (stockQuote == null) {
			return null;// FIXME Warn
		}
		String name = stockQuote.getName();
		if (name.startsWith("S") || name.startsWith("ST") || name.startsWith("*ST") || name.startsWith("S*ST")
				|| name.startsWith("SST") || name.startsWith("退市")) {
			return null;
		}

		// if (getMarket(code) == Market.BEAR_MARKET) {
		//
		// System.out.println(stockQuote.getName() + "\tMarket=BEAR_MARKET");
		// return null;
		// }
		List<StockQuote> dailyQuote = service.get(code, QuoteType.DAILY_QUOTE);

		Portfolio portfolio = new Portfolio(init);// 初始金额
		strategy.test(portfolio, Indicator.compute(dailyQuote));

		double profit = portfolio.getCash() - init;
		percentFormat.setMinimumFractionDigits(2);

		System.out.println(stockQuote.getName() + "\t" + stockQuote.getCode() + "\t" + decimalFormat.format(profit)
				+ "\t" + percentFormat.format(profit / init));
		return portfolio;
	}

	public Market getMarket(String code) throws Exception {
		List<StockQuote> monthlyQuote = service.get(code, QuoteType.MONTHLY_QUOTE);
		List<Indicator> indicator = Indicator.compute(monthlyQuote);

		Advise advise = TechnicalAnalysis.analysisMACD(indicator.subList(0, indicator.size() - 2));

		return advise.getMarket();
	}

	/**
	 * 收益概况
	 * <ul>
	 * <li><b>Annualized Returns: </b>策略年化收益率。表示投资期限为一年的预期收益率。具体计算方式为 (策略最终价值 /
	 * 策略初始价值 - 1) / 回测交易日数量 × 250
	 * <li><b>Benchmark Returns：</b>参考标准年化收益率。具体计算方式为 (参考标准最终指数 / 参考标准初始指数 - 1) /
	 * 回测交易日数量 × 250 。
	 * <li><b>Alpha：</b>阿尔法。具体计算方式为 (策略年化收益 - 无风险收益) - beta × (参考标准年化收益 -
	 * 无风险收益)，这里的无风险收益指的是中国固定利率国债收益率曲线上10年期国债的年化到期收益率。
	 * <li>Beta：贝塔。具体计算方法为 策略每日收益与参考标准每日收益的协方差 / 参考标准每日收益的方差 。
	 * <li><b>Sharpe Ratio：</b>夏普比率。表示每承受一单位总风险，会产生多少的超额报酬。具体计算方法为 (策略年化收益率 -
	 * 回测起始交易日的无风险利率) / 策略收益波动率 。
	 * <li><b>Volatility：</b>策略收益波动率。用来测量资产的风险性。具体计算方法为 策略每日收益的年化标准差 。
	 * <li><b>Information Ratio：</b>信息比率。衡量超额风险带来的超额收益。具体计算方法为 (策略每日收益 -
	 * 参考标准每日收益)的年化均值 / 年化标准差 。
	 * <li><b>Max Drawdown：</b>最大回撤。描述策略可能出现的最糟糕的情况。具体计算方法为 max(1 - 策略当日价值 /
	 * 当日之前虚拟账户最高价值)
	 * </ul>
	 */
	public void profit() {

	}

}
