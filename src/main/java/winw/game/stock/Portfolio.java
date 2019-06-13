package winw.game.stock;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投资组合。
 * 
 * @author winw
 *
 */
public class Portfolio {

	public final double init;// 现金
	private double cash;// 现金

	// private double maxInvestment;// 最大投资

	private double buyCost = 0.0003;// 买入，万3
	private double sellCost = 0.0013;// 卖出，千分之1.3
	private double minCost = 5;// 不足5元以5元计

	private List<Trade> tradeList = new ArrayList<Trade>();// 交易记录

	private Map<String, Integer> positions = new HashMap<String, Integer>();// 持仓

	private double marketValue = 0;

	public Portfolio(double init) {
		super();
		this.init = init;
		this.cash = init;
	}

	public Trade order(Quote quote, double percent) {
		if (percent <= 0 && !positions.containsKey(quote.getCode())) {
			return null;
		}

		int count = (percent <= 0) ? -positions.get(quote.getCode())
				: new Double(maxBuy(quote.getClose()) * percent).intValue();

		if (count == 0) {
			return null;
		}

		Trade trade = new Trade(quote.getDate(), quote.getCode(), quote.getName(), quote.getClose(), count,
				commission(quote.getClose() * count));
		if (percent > 0 && cash - trade.getAmount() - trade.getCommission() < 0) {
			return null;
		}

		if (positions.containsKey(quote.getCode())) {
			positions.put(quote.getCode(), positions.get(quote.getCode()) + count);
		} else {
			positions.put(quote.getCode(), count);
		}

		if (positions.get(quote.getCode()) <= 0) {
			positions.remove(quote.getCode());
		}

		cash = cash - trade.getAmount() - trade.getCommission();

		// maxInvestment = Math.max(maxInvestment, trade.getAmount() +
		// trade.getCommission());

		tradeList.add(trade);
		return trade;
	}

	public int getPosition(String symbol) {
		return positions.getOrDefault(symbol, 0);
	}

	public double commission(double amount) {
		// 计算佣金，买入是万3，卖出是千分之1.3,不足5元以5元计
		double commission = amount > 0 ? amount * buyCost : Math.abs(amount) * sellCost;
		return (commission < minCost) ? minCost : commission;
	}

	public int maxBuy(double price) {
		// 假设全部买入
		return (int) ((cash - commission(cash)) / price) / 100 * 100;
	}

	// private final DecimalFormat decimalFormat = new DecimalFormat("##0.00");
	private final DecimalFormat decimal4Format = new DecimalFormat("##0.0000");

	public List<Trade> getTradeLog() {
		for (int i = 0; i < tradeList.size(); i += 2) {
			Trade log = tradeList.get(i);
			log.setDiff(Double.parseDouble(decimal4Format.format(log.getDiff())));
			log.setDea(Double.parseDouble(decimal4Format.format(log.getDea())));
			// log.setMacd(Double.parseDouble(decimal4Format.format(.getMacd())));
			log.setSlope(Double.parseDouble(decimal4Format.format(log.getSlope())));
		}
		return tradeList;
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public double getBuyCost() {
		return buyCost;
	}

	public void setBuyCost(double buyCost) {
		this.buyCost = buyCost;
	}

	public double getSellCost() {
		return sellCost;
	}

	public void setSellCost(double sellCost) {
		this.sellCost = sellCost;
	}

	public double getMinCost() {
		return minCost;
	}

	public void setMinCost(double minCost) {
		this.minCost = minCost;
	}

	public Map<String, Integer> getPositions() {
		return positions;
	}

	public void setPositions(Map<String, Integer> positions) {
		this.positions = positions;
	}

	public List<Trade> getTradeList() {
		return tradeList;
	}

	public void setTradeList(List<Trade> tradeList) {
		this.tradeList = tradeList;
	}

	public double getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}

	public double getProfit() {
		return marketValue + cash - init;
	}

	public double getProfitRate() {
		return getProfit() / init;
	}
}
