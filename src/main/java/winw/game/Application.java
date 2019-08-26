package winw.game;

import org.springframework.boot.Banner;
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

	public static ConfigurableApplicationContext applicationContext;

	public static void main(String[] args) throws Exception {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
		applicationContext = builder.headless(false).bannerMode(Banner.Mode.OFF).run(args);

		QuantTrader quantTrader = applicationContext.getBean(QuantTrader.class);

		if (quantTrader.isTradable()) {
			quantTrader.beforeClose();
		}

		applicationContext.close();
	}

	// @Scheduled(cron = "55 14 * * 1-5")

}
