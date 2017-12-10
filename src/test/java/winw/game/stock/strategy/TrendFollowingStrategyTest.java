package winw.game.stock.strategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import winw.game.stock.Portfolio;
import winw.game.stock.Quote;
import winw.game.stock.QuoteType;
import winw.game.stock.StockList;
import winw.game.stock.StockQuote;
import winw.game.stock.StockQuoteService;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.Trade;
import winw.game.stock.TradeLog;
import winw.game.stock.TradeLogRepository;
import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Advise.Market;
import winw.game.stock.analysis.Indicator;
import winw.game.stock.analysis.TechnicalAnalysis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TrendFollowingStrategyTest {

	private StockQuoteService service = new TencentStockQuoteService();

	private TrendFollowingStrategy strategy = new TrendFollowingStrategy();

	@Resource
	private TradeLogRepository tradeLogRepository;

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
		Portfolio portfolio = test("sz002839");
		if (portfolio == null) {
			return;
		}

		// print trade Log
		for (Trade trade : portfolio.getTradeLog()) {
			System.out.println(trade.toString());
		}
	}

	private final DecimalFormat decimalFormat = new DecimalFormat("##0.00");
	private final DecimalFormat decimal4Format = new DecimalFormat("##0.0000");
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
		List<Quote> dailyQuote = service.get(code, QuoteType.DAILY_QUOTE, 365);

		Portfolio portfolio = new Portfolio(init);// 初始金额
		strategy.test(portfolio, Indicator.compute(dailyQuote));

		List<Trade> tradeLog = portfolio.getTradeLog();
		for (int i = 0; i < tradeLog.size(); i += 2) {
			Trade buy = tradeLog.get(i);
			Trade sell = tradeLog.get(i + 1);

			TradeLog log = new TradeLog();
			log.setCode(code);
			log.setName(name);
			
			log.setBuyDate(buy.getDate());
			log.setBuyPrice(buy.getPrice());
			log.setBuyDiff(Double.parseDouble(decimal4Format.format(buy.getDiff())));
			log.setBuyDea(Double.parseDouble(decimal4Format.format(buy.getDea())));

			log.setSellDate(sell.getDate());
			log.setSellPrice(sell.getPrice());
			log.setSellDiff(Double.parseDouble(decimal4Format.format(sell.getDiff())));
			log.setSellDea(Double.parseDouble(decimal4Format.format(sell.getDea())));

			double profit = Math.abs(sell.getAmount()) - Math.abs(buy.getAmount()) - sell.getCommission() - buy.getCommission();
			log.setProfit(Double.parseDouble(decimalFormat.format(profit)));

			double profitRate = profit / (Math.abs(buy.getAmount()) + buy.getCommission());
			
			log.setProfitRate(Double.parseDouble(decimal4Format.format(profitRate)));

			tradeLogRepository.save(log);
		}

		percentFormat.setMinimumFractionDigits(2);
		
		double profit = portfolio.getCash() - init;

		System.out.println(stockQuote.getName() + "\t" + stockQuote.getCode() + "\t" + decimalFormat.format(profit)
				+ "\t" + percentFormat.format(profit / init));
		return portfolio;
	}

	public Market getMarket(String code) throws Exception {
		List<Quote> monthlyQuote = service.get(code, QuoteType.MONTHLY_QUOTE, 100);
		List<Indicator> indicator = Indicator.compute(monthlyQuote);

		Advise advise = TechnicalAnalysis.analysisMACD(indicator.subList(0, indicator.size() - 2));

		return advise.getMarket();
	}

}
