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

	// @Scheduled(cron = "55 14 * * 1-5")
	public static ConfigurableApplicationContext applicationContext;
	public static SpringApplicationBuilder springBuilder = new SpringApplicationBuilder(Application.class);

	public static void main(String[] args) throws Exception {
		applicationContext = springBuilder.headless(false).run(args);

		QuantTrader quantTrader = applicationContext.getBean(QuantTrader.class);

		// if (quantTrader.isTradable()) {
		quantTrader.beforeClose();
		// }

		applicationContext.close();
	}

}
