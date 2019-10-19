package winw.game;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 项目启动入口。
 * 
 * @author winw
 *
 */
@EnableScheduling
@SpringBootApplication
public class Application {

	private static SpringApplicationBuilder springBuilder = new SpringApplicationBuilder(Application.class);

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext applicationContext = springBuilder.headless(false).run(args);
		QuantTrader quantTrader = applicationContext.getBean(QuantTrader.class);
		quantTrader.beforeClose();
//		QuantAdvise quantAdvise = applicationContext.getBean(QuantAdvise.class);
//		quantAdvise.publish();
		Thread.sleep(Long.MAX_VALUE);
	}

	// TODO 在14:30做一次邮件通知预报当天是否触发交易。如果当天有交易则在最后5分钟下单。
}
