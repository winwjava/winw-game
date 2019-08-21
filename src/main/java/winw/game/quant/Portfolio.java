package winw.game.quant;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 投资组合。
 * 
 * @author winw
 *
 */
@Entity
@Table(name = "PORTFOLIO")
public class Portfolio {
	@Id
	@GeneratedValue
	private int pid;
	private String name;// 名称。
	private double init;// 初始资产。
	private double cash;// 现金余额。

	private String samples;// 样本代码。

	private int maxPosition = 1;// 最多持仓

	// 印花税0.1%，佣金0.03%，最小佣金5元。
	private double closeTax = 0.001;// 千分之1
	private double minCommission = 5;// 不足5元以5元计
	private double openCommission = 0.0003;// 买入，万3
	private double closeCommission = 0.0003;// 卖出，万3

	private double drawdownLimit = 1;// 回撤限制。
	private double stoplossLimit = 1;// 亏损限制。

	// 持仓市值。
	@Transient
	private double marketValue = 0;
	// 交易记录。
	@Transient
	private final List<Order> orderList = new ArrayList<Order>();
	// 持仓记录。
	@Transient
	private final Map<String, Position> positions = new HashMap<String, Position>();
	// 卖出后保持空仓天数
	@Transient
	private Map<String, Integer> emptyPositionDays = new HashMap<String, Integer>();

	private static final NumberFormat percentFormat = new DecimalFormat("#.##%");

	public Portfolio() {
	}

	public Portfolio(double init, int maxPosition, double drawdownLimit, double stoplossLimit) {
		this.init = init;
		this.cash = init;
		this.maxPosition = maxPosition;
		this.drawdownLimit = drawdownLimit;
		this.stoplossLimit = stoplossLimit;
	}

	public Portfolio(String name, double init, int maxPosition, double drawdownLimit, double stoplossLimit) {
		super();
		this.name = name;
		this.init = init;
		this.cash = init;
		this.maxPosition = maxPosition;
		this.drawdownLimit = drawdownLimit;
		this.stoplossLimit = stoplossLimit;
	}

	public int maxBuy(double price) {
		// 假设全部买入
		return (int) ((cash - commission(cash)) / price);
	}

	public double commission(double amount) {
		// 计算佣金，买入是万3，卖出是千分之1.3,不足5元以5元计
		double tax = (amount > 0 ? 0 : closeTax) * Math.abs(amount);
		double commission = (amount > 0 ? openCommission : closeCommission) * Math.abs(amount);
		BigDecimal b = new BigDecimal(tax + (commission < minCommission ? minCommission : commission));
		return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 是否已经满仓。
	 * 
	 * @return
	 */
	public boolean hasFullPosition() {
		return positions.size() >= maxPosition;
	}

	/**
	 * 下单百分比，有两种方案：
	 * <p>
	 * 平均持仓。每个持仓占1/N，如果很容易找到合适标的，则应该按照1/N的平均持仓。
	 * <p>
	 * 新建仓时先减仓。如果碰到合适标的的几率较少，仓位一直放100%，有发现合适机会后先将持有标的减仓（调仓）。
	 * 
	 * @return
	 */
	public double orderPercent(double percent) {
		int positionSize = positions.size();
		return (positionSize >= maxPosition ? 0 : 1d / (maxPosition - positionSize)) * percent;
	}

	public boolean hasPosition(String code) {
		return positions.containsKey(code);
	}

	public void updateEmptyPositionDays() {
		for (String code : emptyPositionDays.keySet()) {
			emptyPositionDays.put(code, emptyPositionDays.get(code) + 1);
		}
	}

	public int getEmptyPositionDays(String code, int defaultValue) {
		return emptyPositionDays.getOrDefault(code, defaultValue);
	}

	@Transient
	private LinkedList<Order> batchOrders = new LinkedList<Order>();

	/**
	 * 预添加订单。如果当天没有提交，则次日自动失效。
	 * 
	 * @param quote
	 * @param percent
	 * @param desc
	 */
	public void addBatch(Quote quote, double percent, String desc) {
		if (percent < 0 && !hasPosition(quote.getCode())) {
			return;
		}
		if (percent < 0) {
			batchOrders.addFirst(new Order(quote, pid, quote.getClose(), percent, desc));
		} else {
			batchOrders.addLast(new Order(quote, pid, quote.getClose(), percent, desc));
		}
	}

	/**
	 * 取消全部预添加订单。
	 */
	public void cancelBatch() {
		batchOrders.clear();
	}

	/**
	 * 提交预添加的订单。返回执行成功的订单。
	 * 
	 * @return
	 */
	public List<Order> commitBatch() {
		ArrayList<Order> resultList = new ArrayList<Order>();
		for (Order order : batchOrders) {
			String code = order.getCode();
			double percent = order.getPercent();
			if (percent < 0 && !hasPosition(code)) {
				continue;
			}

			int size = percent < 0 // 计算买入或者卖出的数量。
					? new Double(positions.get(code).getSize() * percent).intValue()
					: new Double(maxBuy(order.getPrice()) * orderPercent(percent)).intValue() / 100 * 100;

			if (size == 0) {
				continue;
			}

			order.setSize(size);
			order.setCommission(commission(order.getPrice() * size));
			order.setAmount(new BigDecimal(order.getPrice() * size).add(new BigDecimal(order.getCommission()))
					.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			resultList.add(order(order));
		}
		batchOrders.clear();
		return resultList;
	}

	public Order order(Order order) {
		int size = order.getSize();
		String code = order.getCode();

		if (size > 0 && cash - order.getAmount() < 0) {
			return null;
		}

		Position position = positions.getOrDefault(code, new Position(pid, code));
		if (position.getSize() + size == 0) {
			order.setProfit(percentFormat.format(position.getReturnRate(order.getPrice())));
			positions.remove(code);
			emptyPositionDays.put(code, 0);
		} else {
			positions.put(code, position.add(size, order.getAmount()));
		}
		cash = new BigDecimal(cash).subtract(new BigDecimal(order.getAmount())).setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();

		order.setBalance(cash);

		orderList.add(order);// 交易记录
		return order;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getInit() {
		return init;
	}

	public void setInit(double init) {
		this.init = init;
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public String getSamples() {
		return samples;
	}

	public void setSamples(String samples) {
		this.samples = samples;
	}

	public int getMaxPosition() {
		return maxPosition;
	}

	public void setMaxPosition(int maxPosition) {
		this.maxPosition = maxPosition;
	}

	public double getCloseTax() {
		return closeTax;
	}

	public void setCloseTax(double closeTax) {
		this.closeTax = closeTax;
	}

	public double getMinCommission() {
		return minCommission;
	}

	public void setMinCommission(double minCommission) {
		this.minCommission = minCommission;
	}

	public double getOpenCommission() {
		return openCommission;
	}

	public void setOpenCommission(double openCommission) {
		this.openCommission = openCommission;
	}

	public double getCloseCommission() {
		return closeCommission;
	}

	public void setCloseCommission(double closeCommission) {
		this.closeCommission = closeCommission;
	}

	public Map<String, Integer> getEmptyPositionDays() {
		return emptyPositionDays;
	}

	public void setEmptyPositionDays(Map<String, Integer> emptyPositionDays) {
		this.emptyPositionDays = emptyPositionDays;
	}

	public List<Order> getOrderList() {
		return orderList;
	}

	public Map<String, Position> getPositions() {
		return positions;
	}

	public void putPositions(List<Position> positionList) {
		for (Position position : positionList) {
			positions.put(position.getCode(), position);
		}
	}

	public double getDrawdownLimit() {
		return drawdownLimit;
	}

	public void setDrawdownLimit(double drawdownLimit) {
		this.drawdownLimit = drawdownLimit;
	}

	public double getStoplossLimit() {
		return stoplossLimit;
	}

	public void setStoplossLimit(double stoplossLimit) {
		this.stoplossLimit = stoplossLimit;
	}

	public double getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}

	public double getReturn() {
		return marketValue + cash - init;
	}

	public String getReturnRate() {
		return percentFormat.format(getReturn() / init);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(name);
		builder.append(", position: ").append(positions.size());
		builder.append(", trading today: ").append(orderList.size());
		builder.append(", return: ").append(getReturnRate());
		return builder.toString();
	}

}
