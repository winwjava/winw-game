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

		List<Quote> dailyQuote = service.get("sh600233", QuoteType.DAILY_QUOTE);
		Assert.assertNotNull(dailyQuote);
		Assert.assertTrue(dailyQuote.size() > 0);
		for (Quote temp : dailyQuote) {
			System.out.println(temp);
		}

		List<Quote> monthlyQuote = service.get("sh600233", QuoteType.MONTHLY_QUOTE);
		Assert.assertNotNull(monthlyQuote);
		Assert.assertTrue(monthlyQuote.size() > 0);
		for (Quote temp : monthlyQuote) {
			System.out.println(temp);
		}
	}

}
