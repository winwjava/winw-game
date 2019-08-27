package winw.game.quant;

import java.util.Date;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import winw.game.Application;
import winw.game.TradingConfig;
import winw.game.quant.util.MailService;

/**
 * 根据策略名称、券商接口做量化交易。
 * 
 * @author winw
 *
 */
@ManagedBean
public class QuantTrader {
	private Logger logger = LoggerFactory.getLogger(Application.class);

	@Resource
	protected TradingConfig config;

	@Resource
	private MailService mailService;

	@Resource
	private BrokerService defaultBrokerService;
	@Resource
	private BrokerService generalBrokerService;

	private QuoteService quoteService = QuoteService.getDefault();

	public QuantTrader() {
	}

	private BrokerService getBrokerService() {
		return (config.getBroker() == null || config.getBroker().isEmpty()) ? defaultBrokerService
				: generalBrokerService;
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
	public void beforeClose() throws Exception {
		Portfolio portfolio = getBrokerService().getPortfolio(config);
		System.out.println("Balance: " + portfolio.getCash());

		for (Position position : portfolio.getPositions().values()) {
			System.out.println(position);
		}
		QuantTradingStrategy strategy = config.getStrategy().newInstance();
		strategy.addSamples(portfolio.getPositions().keySet());
		String result = strategy.mockTrading(portfolio);
		for (Order order : portfolio.getOrderList()) {
			getBrokerService().delegate(portfolio, order);
		}
		mailService.send(String.format("%tF, %s mock trading", new Date(), portfolio.getOrderList().size()), result,
				"text/html;charset=utf-8");
	}

	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

}
