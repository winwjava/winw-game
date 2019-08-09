package winw.game;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import winw.game.quant.OrderRepository;
import winw.game.quant.Portfolio;
import winw.game.quant.PortfolioRepository;
import winw.game.quant.PositionRepository;
import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.QuoteService;
import winw.game.quant.TencentQuoteService;
import winw.game.quant.strategy.MeanReversionStrategy;
import winw.game.quant.strategy.TrendFollowingStrategy;
import winw.game.quant.util.MailService;

@Component
public class MockTrading {
	private Logger logger = LoggerFactory.getLogger(Application.class);

	private double init = 100000;

	@Resource
	private OrderRepository orderRepository;
	@Resource
	private PositionRepository positionRepository;
	@Resource
	private PortfolioRepository portfolioRepository;

	public static String MEAN_REVERSION_300TOP = "MeanReversionStrategy(300_TOP)";
	public static String TREND_FOLLOWING_300ETF = "TrendFollowingStrategy(300_ETF)";
	public static String TREND_FOLLOWING_300TOP = "TrendFollowingStrategy(300_TOP)";

	public static QuantTradingStrategy getStrategy(String name) {
		if (MEAN_REVERSION_300TOP.equals(name)) {
			return new MeanReversionStrategy();
		}
		if (TREND_FOLLOWING_300ETF.equals(name)) {
			return new TrendFollowingStrategy(QuantTradingStrategy.CSI_300_ETF);
		}
		if (TREND_FOLLOWING_300TOP.equals(name)) {
			return new TrendFollowingStrategy(QuantTradingStrategy.CSI_300_TOP);
		}
		return null;
	}

	public void trading() throws Exception {
		QuoteService service = new TencentQuoteService();
		if (!service.isTradingDay()) {
			logger.info("Today is not a trading day.");
			return;
//		} else if (!service.isTradingTime()) {
//			logger.info("It is not trading time now.");
//			return;
		}

		List<Portfolio> list = portfolioRepository.findAll();
		if (list == null || list.isEmpty()) {
			list.add(portfolioRepository.save(new Portfolio(MEAN_REVERSION_300TOP, init, 1, 0.1, 0.1)));
			list.add(portfolioRepository.save(new Portfolio(TREND_FOLLOWING_300ETF, init, 1, 0.07, 0.05)));
			// list.add(portfolioRepository.save(new Portfolio(TREND_FOLLOWING_300TOP, init,
			// 1, 0.07, 0.05)));
		}

		trading(list);
	}

	private void trading(List<Portfolio> portfolioList) throws Exception {
		int trading = 0;
		StringBuilder html = new StringBuilder();
		for (Portfolio portfolio : portfolioList) {
			portfolio.putPositions(positionRepository.findByPid(portfolio.getPid()));
			html.append(getStrategy(portfolio.getName()).mockTrading(portfolio));
			trading += portfolio.getOrderList().size();
			portfolioRepository.save(portfolio);
			orderRepository.saveAll(portfolio.getOrderList());
			positionRepository.saveAll(portfolio.getPositions().values());
		}
		new MailService().send(String.format("%tF, %s mock trading", new Date(), trading), html.toString(),
				"text/html;charset=utf-8");
	}

}
