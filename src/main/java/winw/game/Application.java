package winw.game;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目启动入口。
 * 
 * @author winw
 *
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
public class Application {

	private static SpringApplicationBuilder springBuilder = new SpringApplicationBuilder(Application.class);

	public static void main(String[] args) throws Exception {
		springBuilder.run(args);
		log.info("Application Started OK.");
//		ConfigurableApplicationContext applicationContext = springBuilder.headless(false).run(args);
//		QuantTrader quantTrader = applicationContext.getBean(QuantTrader.class);
//		quantTrader.beforeClose();
//		QuantAdvise quantAdvise = applicationContext.getBean(QuantAdvise.class);
//		quantAdvise.publish();
		Thread.sleep(Long.MAX_VALUE);
	}
}
