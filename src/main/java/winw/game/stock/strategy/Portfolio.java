package winw.game.stock.strategy;

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

	private double cash;// 现金

	private double buyCost = 0.0003;// 买入，万3
	private double sellCost = 0.0013;// 卖出，千分之1.3
	private double minCost = 5;// 不足5元以5元计

	private List<Trade> tradeLog = new ArrayList<Trade>();// 交易记录

	private Map<String, Integer> positions = new HashMap<String, Integer>();// 持仓

	public Portfolio() {
		super();
	}

	public Portfolio(double cash) {
		super();
		this.cash = cash;
	}

	public Integer getPosition(String symbol) {
		return positions.get(symbol);
	}

	public double commission(double amount) {
		// 计算佣金，买入是万3，卖出是千分之1.3,不足5元以5元计
		double commission = amount > 0 ? amount * buyCost : Math.abs(amount) * sellCost;
		return (commission < minCost) ? minCost : commission;
	}

	public boolean trading(Trade trade) {
		trade.setCommission(commission(trade.getAmount()));
		if (cash - trade.getAmount() - trade.getCommission() < 0) {
			return false;
		}

		cash = cash - trade.getAmount() - trade.getCommission();
		return tradeLog.add(trade);
	}

	public int maxBuy(double price) {
		// 假设全部买入
		return (int) ((cash - commission(cash)) / price) / 100 * 100;
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

	public List<Trade> getTradeLog() {
		return tradeLog;
	}

	public void setTradeLog(List<Trade> tradeLog) {
		this.tradeLog = tradeLog;
	}

	public Map<String, Integer> getPositions() {
		return positions;
	}

	public void setPositions(Map<String, Integer> positions) {
		this.positions = positions;
	}

}
