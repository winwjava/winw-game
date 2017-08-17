package winw.game.stock;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import winw.game.stock.Stock;
import winw.game.stock.StockQuote;

public class TencentStockQuoteServiceTest {

	private TencentStockQuoteService service = new TencentStockQuoteService();

	@Test
	public void test() throws IOException, ParseException {
		Stock quote = service.getStock("sh600233");

		Assert.assertNotNull(quote);

		List<StockQuote> list = service.getHistoricalQuote("sh600233");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);

		System.out.println(quote);
		System.out.println(list);
	}

}
