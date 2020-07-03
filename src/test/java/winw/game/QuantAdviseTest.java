package winw.game;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QuantAdviseTest {

	@Resource
	private QuantAdvise quantAdvise;

	@Test
	public void testAddUser() throws Exception {
		quantAdvise.publish();
	}
}
