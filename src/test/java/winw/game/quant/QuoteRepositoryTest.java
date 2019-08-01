package winw.game.quant;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuoteRepositoryTest {

	private QuoteService service = new TencentQuoteService();

	@Resource
	private QuoteRepository quoteRepository;

	@Test
	public void test() throws Exception {
		List<Quote> dailyQuote = service.get("sh600233", QuotePeriod.DAILY, null, null);
		Assert.assertNotNull(dailyQuote);
		Assert.assertTrue(dailyQuote.size() > 0);

		quoteRepository.saveAll(dailyQuote);

		Thread.sleep(10000000);
	}
}
