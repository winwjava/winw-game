package winw.game.stock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Historical prices and volumes. Historical price trends can indicate the
 * future direction of a stock.
 * 
 * @author winw
 *
 */
public class StockQuote {// 历史报价（行情）
	protected String date;// 交易日期

	protected double open; // 开盘价
	protected double high; // 最高价
	protected double low; // 最低价
	protected double close; // 收盘价

	protected int volume; // 成交量
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

	public static void addToday(List<StockQuote> quoteList, Stock stock) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String today = dateFormat.format(new Date());

		Calendar instance = Calendar.getInstance();
		instance.setTime(stock.getTime());
		// 如果当前时间是交易时间，则将当天的交易，也纳入到历史记录里计算
		if (instance.get(Calendar.HOUR_OF_DAY) < 15) {
			System.out.println("交易时间：" + stock.getTime());
			StockQuote quote = new StockQuote();
			quote.setDate(today);
			quote.setOpen(stock.getOpen());
			quote.setClose(stock.getPrice());// FIXME 用当前价，当作收盘价
			quote.setHigh(stock.getHigh());
			quote.setLow(stock.getLow());
			quote.setVolume(stock.getVolume());
			quoteList.add(quote);
		}
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
