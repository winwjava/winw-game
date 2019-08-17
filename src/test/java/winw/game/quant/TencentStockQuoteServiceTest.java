package winw.game.quant;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TencentStockQuoteServiceTest {

	private QuoteService service = new TencentQuoteService();

	@Test
	public void test() throws Exception {
		Quote quote = service.get(Quote.class, "sh000300");
		// System.out.println(quote);
		Assert.assertNotNull(quote);

		List<Quote> dailyQuote = service.get(Quote.class, "sh000300", null, null);
		Assert.assertNotNull(dailyQuote);
		Assert.assertTrue(dailyQuote.size() > 0);
		int i = 1 ;
		for (Quote temp : dailyQuote) {
			//[48.05, 70.50],
			System.out.println("[" + (i++ )+ ", "+ temp.toString()+"],");
		}

//		List<Quote> monthlyQuote = service.get("sh600233", QuoteType.MONTHLY_QUOTE, 100);
//		Assert.assertNotNull(monthlyQuote);
//		Assert.assertTrue(monthlyQuote.size() > 0);
//		for (Quote temp : monthlyQuote) {
//			System.out.println(temp);
//		}
	}

}
