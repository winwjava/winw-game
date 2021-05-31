package winw.game.quant;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 交易指令。
 * 
 * @author winw
 *
 */
@Entity
@Table(name = "ORDERS")
public class Order {
	@Id
	@GeneratedValue
	private int id;

	private int pid;// 投资组合

	private String code;// 代码

	private String date;// 日期

	private String time;// 时间

	private int size;// 数量

	private double currentPrice;// 当前价
	private double holdingPrice;// 持有价

	private double commission;// 佣金

	private double amount;// 金额

	private double balance;// 结余

	private String profit;// 收益

	private String comment;// 备注

	@Transient
	private double percent;// 下单比例

	public Order() {
		super();
	}

	public Order(Quote quote, int pid, double price, double percent, String comment) {
		super();
		this.pid = pid;
		this.date = quote.getDate();
		this.code = quote.getCode();
		this.currentPrice = price;
		this.percent = percent;
		this.comment = comment;
	}

	// public Order(Quote quote, int pid, double price, int size, double commission)
	// {
	// super();
	// this.pid = pid;
	// this.date = quote.getDate();
	// this.code = quote.getCode();
	// this.price = price;
	// this.size = size;
	// this.commission = commission;
	//
	// // amount，应该怎么算
	// this.amount = new BigDecimal(price * size).add(new
	// BigDecimal(commission)).setScale(2, BigDecimal.ROUND_HALF_UP)
	// .doubleValue();
	// }

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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getHoldingPrice() {
		return holdingPrice;
	}

	public void setHoldingPrice(double holdingPrice) {
		this.holdingPrice = holdingPrice;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	private static final NumberFormat floatFormat = new DecimalFormat("#.##");

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(code).append(size > 0 ? " + " : " - ");
		builder.append(Math.abs(size));
		builder.append(" * ");
		builder.append(currentPrice);
		builder.append(" = ");
		builder.append(Math.abs(amount));
		// builder.append(", ");
		// builder.append(comment);
		if (profit != null) {
			double diffPrice = currentPrice - holdingPrice;
			builder.append(", ").append(diffPrice > 0 ? "+" : "-");
			builder.append(floatFormat.format(Math.abs(diffPrice))).append("/");
			builder.append(floatFormat.format(holdingPrice)).append(" = ");
			builder.append(profit);
		}
		return builder.toString();
	}

}
