package winw.game.quant;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import winw.game.TradingConfig;

/**
 * 券商接口的空实现，可用于模拟交易。
 * 
 * <p>
 * 子类应当实现执行交易指令功能。
 * 
 * @author winw
 *
 */
@ManagedBean("defaultBrokerService")
public class BrokerService {

	@Resource
	private TradingConfig config;
	@Resource
	private OrderRepository orderRepository;
	@Resource
	private PositionRepository positionRepository;
	@Resource
	private PortfolioRepository portfolioRepository;

	// boolean login(String username, String password);

	/**
	 * 委托交易指令。
	 * 
	 * @param order
	 * @return
	 */
	public Order delegate(Order order) {
		return null;
	}

	// 当日成交。
	// 当前持仓。

	public Portfolio find(String name) throws Exception {
		Portfolio portfolio = portfolioRepository.findByName(name);
		if (portfolio == null) {
			return new Portfolio(name, config.getInitAssets(), config.getMaxPosition(), config.getDrawdownLimit(),
					config.getStoplossLimit());
		}
		portfolio.putPositions(positionRepository.findByPid(portfolio.getPid()));
		return portfolio;
	}

	public void save(Portfolio portfolio) {
		portfolioRepository.save(portfolio);
		orderRepository.saveAll(portfolio.getOrderList());
		positionRepository.saveAll(portfolio.getPositions().values());
	}

}
