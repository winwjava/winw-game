package winw.game.quant;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 交易订单。
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

	private int size;// 数量

	private double price;// 价格

	private double commission;// 佣金

	private double amount;// 金额

	private double balance;// 结余

	private String comment;

	public Order() {
		super();
	}

	public Order(Quote quote, int pid, double price, int size, double commission) {
		super();
		this.pid = pid;
		this.date = quote.getDate();
		this.code = quote.getCode();
		this.price = price;
		this.size = size;
		this.commission = commission;

		this.amount = new BigDecimal(price * size).add(new BigDecimal(commission)).setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Trading [").append(code).append(size > 0 ? " +" : " -");
		builder.append(Math.abs(size));
		builder.append(", price=");
		builder.append(price);
		builder.append(", amount=");
		builder.append(Math.abs(amount));
		builder.append(", comment=");
		builder.append(comment);
		builder.append("]");
		return builder.toString();
	}

}
