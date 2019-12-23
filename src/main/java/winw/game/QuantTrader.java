package winw.game;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import winw.game.quant.BrokerService;
import winw.game.quant.MailService;
import winw.game.quant.Order;
import winw.game.quant.Portfolio;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.QuoteService;

/**
 * 根据策略名称、券商接口做量化交易。
 * 
 * @author winw
 *
 */
//@ManagedBean
public class QuantTrader {
	private Logger logger = LoggerFactory.getLogger(Application.class);

	@Resource
	protected QuantConfig config;

	@Resource
	private MailService mailService;

	@Resource
	private BrokerService brokerService;

	private QuoteService quoteService = QuoteService.getDefault();

	public QuantTrader() {
	}

	public boolean isTradable() throws Exception {
		if (!quoteService.isTradingDay()) {
			logger.info("Today is not a trading day.");
			return false;
		}
		if (!quoteService.isTradingTime()) {
			logger.info("It is not trading time now.");
			return false;
		}
		// brokerService.login();
		return true;
	}

	/**
	 * 在开盘之后执行。
	 */
	public void afterOpen() {

	}

	/**
	 * 在收盘之前执行。
	 * 
	 * @throws Exception
	 */
	// @Scheduled(cron = "30 50 14 * * 1-5")
	public void beforeClose() throws Exception {
//		if (!isTradable()) {
//			return;
//		}
		Portfolio portfolio = brokerService.getPortfolio(config);
		QuantTradingStrategy strategy = config.getStrategy().getDeclaredConstructor().newInstance();
		strategy.addSamples(portfolio.getPositions().keySet());
		String result = strategy.mockTrading(portfolio);
		for (Order order : portfolio.getOrderList()) {
			long t0 = System.currentTimeMillis();
			// TODO 2、考虑成交后再委托下一个订单。
			// getBrokerService().delegate(portfolio, order);
			logger.info("Delegate: {}, cost {}ms.", order, System.currentTimeMillis() - t0);
		}
		brokerService.destroy();
		// TODO 1、邮件里可以带上图表，方便查看分析。
		mailService.send(String.format("%tF, %s mock trading", new Date(), portfolio.getOrderList().size()), result,
				"text/html;charset=utf-8");
		// Runtime.getRuntime().exec("shutdown /h");
	}

}
