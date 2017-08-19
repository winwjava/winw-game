package winw.game.stock.analysis;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;

import winw.game.stock.QuoteType;
import winw.game.stock.StockQuote;
import winw.game.stock.TencentStockQuoteService;

public class TechnicalAnalysisTest {

	private TencentStockQuoteService service = new TencentStockQuoteService();

	// @Test
	// public void test() throws IOException, ParseException {
	// for (int i = 1; i < 100; i++) {
	// try {
	// macdAnalysis("sh" + (600000 + i));
	// } catch (Exception e) {
	// // e.printStackTrace();
	// }
	// }
	//
	// macdAnalysis("sz002714");
	// macdAnalysis("sh600233");
	// macdAnalysis("sz002120");
	// macdAnalysis("sz002352");
	// }

	@Test
	public void test() throws IOException, ParseException {
		StockQuote stockQuote = service.get("sh600161");
		List<StockQuote> quoteList = service.get(stockQuote.getCode(), QuoteType.DAILY_QUOTE);
		List<Indicator> indicatorsList = Indicator.compute(quoteList);

		for (Indicator indicator : indicatorsList) {
			System.out.println(indicator.toString());
		}

		Advise advise = TechnicalAnalysis.macdAnalysis(indicatorsList);

		// Advise
		System.out.println(stockQuote.getCode() + " " + stockQuote.getName() + " " + stockQuote.getPrice() + ", "
				+ advise.toString());

	}

}
