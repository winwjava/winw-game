package winw.game.quant;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * 持仓。
 * 
 * @author winw
 *
 */
@Entity
@Table(name = "POSITIONS", uniqueConstraints = { @UniqueConstraint(columnNames = { "pid", "code" }) })
public class Position {

	@Id
	@GeneratedValue
	private int id;
	private int pid;// 投资组合。

	private String code;// 持仓代码。
	private String name;// 持仓简称。

	private int size;// 持仓数量。
	private int sellable;// 可卖数量。

	private double holdingPrice;// 持有价
	private double currentPrice;// 当前价

	private double marketValue;// 市值。
	// private double boughtValue;

	@Transient
	private double weight;// 仓位占比。

	private int holdingDays = -1;// 持仓天数。

	/**
	 * （持仓过程中的）最高市值。用于计算最大回撤。
	 * <p>
	 * 考虑到有价格复权的情况，价格不能作为计算依据。
	 */
	private double highestMarketValue;

	public Position() {
	}

	public Position(int pid, String code) {
		this.pid = pid;
		this.code = code;
	}

	/**
	 * 增加持仓。
	 * 
	 * @return
	 */
	public Position add(int size, double cost) {
		if (this.size + size < 0) {
			throw new IllegalArgumentException("position size cannot be negative.");
		}
		if (this.size + size > 0) {
			this.holdingPrice = (this.size * this.holdingPrice + cost) / (size + this.size);
		}
		this.size += size;
		return this;
	}

	public void addHoldingDays(int amount) {
		this.holdingDays += amount;
	}

	/**
	 * 记录当前价，当前市值，最高市值。
	 * 
	 * @param currentPrice
	 */
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
		this.marketValue = new BigDecimal(Double.toString(currentPrice)).multiply(new BigDecimal(size)).doubleValue();
		highestMarketValue = Math.max(highestMarketValue, marketValue);
	}

	/**
	 * 记录当前价，当前市值，最高市值，并返回市值回撤百分比。
	 * 
	 * @param currentPrice
	 * @return
	 */
	public double getDrawdown(double currentPrice) {
		this.setCurrentPrice(currentPrice);
		return 1 - marketValue / highestMarketValue;
	}

	/**
	 * 收益率。
	 * 
	 * @param currentPrice
	 * @return
	 */
	public double getReturnRate(double currentPrice) {
		return currentPrice / holdingPrice - 1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSellable() {
		return sellable;
	}

	public void setSellable(int sellable) {
		this.sellable = sellable;
	}

	public double getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}

	public double getHoldingPrice() {
		return holdingPrice;
	}

	public void setHoldingPrice(double holdingPrice) {
		this.holdingPrice = holdingPrice;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public int getHoldingDays() {
		return holdingDays;
	}

	public void setHoldingDays(int holdingDays) {
		this.holdingDays = holdingDays;
	}

	public double getHighestMarketValue() {
		return highestMarketValue;
	}

	public void setHighestMarketValue(double highestMarketValue) {
		this.highestMarketValue = highestMarketValue;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	private static final NumberFormat floatFormat = new DecimalFormat("#.##");
	private static final NumberFormat percentFormat = new DecimalFormat("#.##%");

	@Override
	public String toString() {
		if (currentPrice == 0) {
			setCurrentPrice(holdingPrice);
		}
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(code).append(":  ");
		builder.append(size).append(" * ").append(currentPrice).append(" = ").append(marketValue);
		double diffPrice = currentPrice - holdingPrice;
		builder.append(", ").append(diffPrice > 0 ? "+" : "-");
		builder.append(floatFormat.format(Math.abs(diffPrice))).append("/");
		builder.append(floatFormat.format(holdingPrice)).append(" = ");
		builder.append(percentFormat.format(getReturnRate(currentPrice)));
		builder.append("]");
		return builder.toString();
	}

}
