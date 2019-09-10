package winw.game.quant;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import winw.game.TradingConfig;

/**
 * 券商接口的空实现，可用于模拟交易。
 * 
 * <p>
 * 子类应当覆盖所有模拟方法。
 * 
 * @author winw
 *
 */
@ManagedBean("defaultBrokerService")
public class BrokerService {

	@Resource
	private OrderRepository orderRepository;
	@Resource
	private PositionRepository positionRepository;
	@Resource
	private PortfolioRepository portfolioRepository;

	/**
	 * 获得投资组合配置。
	 * 
	 * @return
	 * @throws Exception
	 */
	public Portfolio getPortfolio(TradingConfig config) throws Exception {
		Portfolio portfolio = portfolioRepository.findByName(config.getPortfolio());
		if (portfolio == null) {
			return new Portfolio(config.getPortfolio(), config.getInitAssets(), config.getMaxPosition(),
					config.getDrawdownLimit(), config.getStoplossLimit());
		}
		portfolio.putPositions(positionRepository.findByPid(portfolio.getPid()));
		return portfolio;
	}

	/**
	 * 委托交易指令。
	 * 
	 * @param order
	 * @return
	 */
	public void delegate(Portfolio portfolio, Order order) {
		order.setTime(Quote.times());
		orderRepository.save(order);
		portfolioRepository.save(portfolio);
		positionRepository.deleteAll();
		positionRepository.saveAll(portfolio.getPositions().values());
	}

	/**
	 * 全部撤单。
	 * 
	 * @param portfolio
	 */
	public void withdrawal(Portfolio portfolio) {

	}

	/**
	 * 获得当日成交的订单。
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Order> getTradings(Portfolio portfolio) throws Exception {
		// return orderRepository.findByDate(Quote.today());
		return portfolio.getOrderList();
	}

	@PreDestroy
	public void destroy() {

	}
}
