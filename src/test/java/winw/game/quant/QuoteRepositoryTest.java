package winw.game.quant;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import winw.game.quant.Quote;
import winw.game.quant.QuoteRepository;
import winw.game.quant.QuoteType;
import winw.game.quant.StockQuoteService;
import winw.game.quant.TencentStockQuoteService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuoteRepositoryTest {

	private StockQuoteService service = new TencentStockQuoteService();

	@Resource
	private QuoteRepository quoteRepository;

	@Test
	public void test() throws Exception {
		List<Quote> dailyQuote = service.get("sh600233", QuoteType.DAILY_QUOTE, null, null, 100);
		Assert.assertNotNull(dailyQuote);
		Assert.assertTrue(dailyQuote.size() > 0);

		quoteRepository.save(dailyQuote);
	}
}
