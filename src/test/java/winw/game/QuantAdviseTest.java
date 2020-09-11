package winw.game;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import winw.game.quant.MailService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QuantAdviseTest {

	@Resource
	private QuantAdvise quantAdvise;
	@Resource
	private MailService mailService;

	@Test
	public void testPublish() throws Exception {
		mailService.setDefaultRecipients("winwgame@sina.com");
		quantAdvise.publish();
	}
}
