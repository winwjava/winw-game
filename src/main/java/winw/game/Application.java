package winw.game;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import winw.game.quant.QuantTrader;

/**
 * 项目启动入口。
 * 
 * @author winw
 *
 */
@SpringBootApplication
public class Application {

	private static ConfigurableApplicationContext applicationContext;
	private static SpringApplicationBuilder springBuilder = new SpringApplicationBuilder(Application.class);

	public static void main(String[] args) throws Exception {
		applicationContext = springBuilder.headless(false).run(args);
		QuantTrader quantTrader = applicationContext.getBean(QuantTrader.class);
		quantTrader.beforeClose();
	}

	// @EnableScheduling
	// @Scheduled(cron = "0 35 15 * * ?")
	public static void beforeClose() throws Exception {
		// if (quantTrader.isTradable()) {
		// }
	}
}
