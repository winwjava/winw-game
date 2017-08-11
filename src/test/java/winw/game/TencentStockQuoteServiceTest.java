package winw.game;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;

import winw.game.stock.Stock;
import winw.game.stock.StockQuote;
import winw.game.stock.TencentStockQuoteService;

public class TencentStockQuoteServiceTest {

	@Test
	public void test() throws IOException, ParseException {
		TencentStockQuoteService service = new TencentStockQuoteService();
		Stock quote = service.getStock("sh600233");

		System.out.println(quote);
		List<StockQuote> list = service.getHistoricalQuote("sh600233");

		System.out.println(list);
	}

}
