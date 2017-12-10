package winw.game.stock;

/**
 * 交易。
 * 
 * @author winw
 *
 */
public class Trade {

	private String code;
	
	private String name;
	
	private String date;// 日期

	private double price;// 价格

	private int count;// 数量

	private double amount;// 金额

	private double commission;// 佣金

	private double diff;

	private double dea;

	public Trade(String date, double price, int count) {
		super();
		this.date = date;
		this.price = price;
		this.count = count;
		this.amount = price * count;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Trade [date=");
		builder.append(date);
		builder.append(", price=");
		builder.append(price);
		builder.append(", count=");
		builder.append(count);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", commission=");
		builder.append(commission);
		builder.append("]");
		return builder.toString();
	}

}
