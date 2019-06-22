package winw.game.stock;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 交易。
 * 
 * @author winw
 *
 */
@Entity
@Table(name = "TRADE_LOG")
public class Trade {
	@Id
	@GeneratedValue
	protected long id;

	private String code;

	private String name;

	private String date;// 日期

	private double price;// 价格

	private int count;// 数量

	private double amount;// 金额

	private double commission;// 佣金

	private double diff;

	private double dea;

	private double macd;

	private double slope;

	public Trade() {
		super();
	}

	public Trade(String date, double price, int count) {
		super();
		this.date = date;
		this.price = price;
		this.count = count;
		this.amount = price * count;
	}

	public Trade(String date, String code, String name, double price, int count, double commission) {
		super();
		this.date = date;
		this.code = code;
		this.price = price;
		this.count = count;
		this.amount = price * count;
		this.commission = commission;
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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

	public double getDiff() {
		return diff;
	}

	public void setDiff(double diff) {
		this.diff = diff;
	}

	public double getDea() {
		return dea;
	}

	public void setDea(double dea) {
		this.dea = dea;
	}

	public double getMacd() {
		return macd;
	}

	public void setMacd(double macd) {
		this.macd = macd;
	}

	public double getSlope() {
		return slope;
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Trade [date=");
		builder.append(date);
		builder.append(", code=");
		builder.append(code);
		builder.append(", count");
		builder.append(count > 0 ? "+=" : "-=");
		builder.append(Math.abs(count));
		builder.append(", price=");
		builder.append(price);
		builder.append("]");
		return builder.toString();
	}

}
