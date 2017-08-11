package winw.game.stock;

/**
 * Historical prices and volumes. Historical price trends can indicate the
 * future direction of a stock.
 * 
 * @author winw
 *
 */
public class StockQuote {// 历史报价（行情）
	protected String date;// 交易日期

	protected double open; // �?盘价
	protected double high; // �?高价
	protected double low; // �?低价
	protected double close; // 收盘�?

	protected int volume; // 成交�?
	protected double amount; // 成交金额

	public StockQuote() {
		super();
	}

	public StockQuote(String date, double open, double high, double low, double close, int volume, double amount) {
		super();
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.amount = amount;
	}



	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HistoricalQuote [date=");
		builder.append(date);
		builder.append(", open=");
		builder.append(open);
		builder.append(", high=");
		builder.append(high);
		builder.append(", low=");
		builder.append(low);
		builder.append(", close=");
		builder.append(close);
		builder.append(", volume=");
		builder.append(volume);
		builder.append(", amount=");
		builder.append(amount);
		builder.append("]");
		return builder.toString();
	}

}
