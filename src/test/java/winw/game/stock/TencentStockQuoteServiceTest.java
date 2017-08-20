package winw.game.stock;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TencentStockQuoteServiceTest {

	private StockQuoteService service = new TencentStockQuoteService();

	@Test
	public void test() throws Exception {
		StockQuote quote = service.get("sh600233");
		System.out.println(quote);
		Assert.assertNotNull(quote);

		List<StockQuote> list = service.get("sh600233", QuoteType.DAILY_QUOTE);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);

		for (StockQuote temp : list) {
			System.out.println(temp);
		}
	}

}
