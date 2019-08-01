package winw.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 项目启动入口。
 * 
 * @author winw
 *
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
		applicationContext.getBean(MockTrading.class).trading();// Mock trading
		applicationContext.close();// Shutdown
	}

}
