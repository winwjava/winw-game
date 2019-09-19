package winw.game;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 项目启动入口。
 * 
 * @author winw
 *
 */
@EnableScheduling
@SpringBootApplication
public class Application {

	private static ConfigurableApplicationContext applicationContext;
	private static SpringApplicationBuilder springBuilder = new SpringApplicationBuilder(Application.class);

	public static void main(String[] args) throws Exception {
		applicationContext = springBuilder.headless(false).run(args);
		applicationContext.wait();
	}

	@Scheduled(cron = "30 55 14 * * ?")
	public static void beforeClose() throws Exception {
		QuantTrader quantTrader = applicationContext.getBean(QuantTrader.class);
		quantTrader.beforeClose();
		Runtime.getRuntime().exec("shutdown /h");
	}
}
