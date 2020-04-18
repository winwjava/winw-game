package winw.game;

import java.util.Date;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;

import winw.game.quant.MailService;
import winw.game.quant.OrderRepository;
import winw.game.quant.Portfolio;
import winw.game.quant.PortfolioRepository;
import winw.game.quant.PositionRepository;
import winw.game.quant.QuoteService;
import winw.game.quant.strategy.MeanReversionStrategy;

/**
 * 量化投资建议。
 * 
 * @author winw
 *
 */
@ManagedBean
public class QuantAdvise {

	@Resource
	private MailService mailService;

	private double init = 500000;

	@Resource
	private OrderRepository orderRepository;
	@Resource
	private PositionRepository positionRepository;
	@Resource
	private PortfolioRepository portfolioRepository;

	private MeanReversionStrategy strategy = new MeanReversionStrategy();

	/**
	 * 发布投资建议。
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "30 30 14 * * 1-5")
	public void publish() throws Exception {
		// TODO 只发送次日的交易信号。用树莓派启动即可。
		// TODO 根据历史回测将沪深300分为三类：均值回归、趋势跟踪，或都不适宜。

		if (!QuoteService.getDefault().isTradingDay()) {
			return;
		}
		Portfolio portfolio = portfolioRepository.findByName("MR300TOP");
		if (portfolio == null) {
			portfolio = portfolioRepository.save(new Portfolio("MR300TOP", init, 2, 1, 1));
		} else {
			portfolio.putPositions(positionRepository.findByPid(portfolio.getPid()));
			portfolio.setDrawdownLimit(1);
			portfolio.setStoplossLimit(1);
		}

		// 根据模拟交易生成投资建议。
		String result = strategy.mockTrading(portfolio);

		portfolioRepository.save(portfolio);
		orderRepository.saveAll(portfolio.getOrderList());
		positionRepository.deleteAll();
		positionRepository.saveAll(portfolio.getPositions().values());

		mailService.send(String.format("%tF, %s mock trading", new Date(), portfolio.getOrderList().size()), result,
				"text/html;charset=utf-8");
	}
}
