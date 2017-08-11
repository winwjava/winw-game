package winw.game.analysis;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import winw.game.stock.Stock;
import winw.game.stock.TencentStockQuoteService;
import winw.game.stock.analysis.TechnicalAnalysis;

public class TradingAnalysisTest {

	TechnicalAnalysis tradingAnalysis = new TechnicalAnalysis();
	TencentStockQuoteService service = new TencentStockQuoteService();

	@Test
	public void test() throws IOException, ParseException {
		for (int i = 1; i < 500; i++) {
			try {
				analysis("sh" + (600000 + i));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		analysis("sz002714");
		analysis("sh600233");
		analysis("sz002120");
		analysis("sz002352");
	}

	private void analysis(String code) throws IOException, ParseException {
		Stock stock = service.getStock(code);
		tradingAnalysis.analysis(stock, service.getHistoricalQuote(stock.getCode()));
	}

}
