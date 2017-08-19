package winw.game.stock;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TencentStockQuoteServiceTest {

	private TencentStockQuoteService service = new TencentStockQuoteService();

	@Test
	public void test() throws IOException, ParseException {
		StockQuote quote = service.get("sh600233");

		Assert.assertNotNull(quote);

		List<StockQuote> list = service.get("sh600233", QuoteType.DAILY_QUOTE);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);

		System.out.println(quote);
		System.out.println(list);
	}

}
