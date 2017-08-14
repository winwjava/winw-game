package winw.game.analysis;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;

import winw.game.stock.Stock;
import winw.game.stock.StockQuote;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Indicators;
import winw.game.stock.analysis.TechnicalAnalysis;

public class TechnicalAnalysisTest {

	private TencentStockQuoteService service = new TencentStockQuoteService();

	@Test
	public void test() throws IOException, ParseException {
		for (int i = 1; i < 100; i++) {
			try {
				macdAnalysis("sh" + (600000 + i));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		macdAnalysis("sz002714");
		macdAnalysis("sh600233");
		macdAnalysis("sz002120");
		macdAnalysis("sz002352");
	}

	private void macdAnalysis(String code) throws IOException, ParseException {
		Stock stock = service.getStock(code);
		List<StockQuote> quoteList = service.getHistoricalQuote(stock.getCode());
		List<Indicators> indicators = Indicators.compute(quoteList);
		Advise advise = TechnicalAnalysis.macdAnalysis(indicators);

		// Advise
		System.out.println(stock.getCode() + " " + stock.getName() + " " + stock.getPrice() + ", " + advise.toString());
	}

}
