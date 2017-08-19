package winw.game.stock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 股票报价。
 * 
 * @author winw
 *
 */
public class StockQuote extends Quote {

	private String code;// 代码
	private String name;// 名称

	private Date time;// 报价时间

	private double price = 0.0;// 价格

	private double previousClose = 0.0;// 昨日收盘价

	private double pe = 0.0;// 市盈率

	// private double eps = 0.0;
	// private double marketcap = 0.0;
	// private String currency = "";
	// private double shortRatio = 0.0;
	// private String exchange;

	public StockQuote() {
		super();
	}

	public StockQuote(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	public static void addToday(List<Quote> quoteList, StockQuote stockQuote) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String today = dateFormat.format(new Date());

		Calendar instance = Calendar.getInstance();
		instance.setTime(stockQuote.getTime());
		// 如果当前时间是交易时间，则将当天的交易，也纳入到历史记录里计算
		if (instance.get(Calendar.HOUR_OF_DAY) < 15) {
			System.out.println("交易时间：" + stockQuote.getTime());
			Quote quote = new Quote();
			quote.setDate(today);
			quote.setOpen(stockQuote.getOpen());
			quote.setClose(stockQuote.getPrice());// FIXME 用当前价，当作收盘价
			quote.setHigh(stockQuote.getHigh());
			quote.setLow(stockQuote.getLow());
			quote.setVolume(stockQuote.getVolume());
			quoteList.add(quote);
		}
	}

	public QuoteType getQuoteType() {
		if (quoteType == null && time != null) {
			return QuoteType.REALTIME_QUOTE;
		}
		return quoteType;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPreviousClose() {
		return previousClose;
	}

	public void setPreviousClose(double previousClose) {
		this.previousClose = previousClose;
	}

	public double getPe() {
		return pe;
	}

	public void setPe(double pe) {
		this.pe = pe;
	}

	@Override
	public String toString() {
		if (getQuoteType() != QuoteType.REALTIME_QUOTE) {
			return super.toString();
		}

		StringBuilder builder = new StringBuilder();
		builder.append("StockQuote [code=");
		builder.append(code);
		builder.append(", name=");
		builder.append(name);
		builder.append(", price=");
		builder.append(price);
		builder.append("]");
		return builder.toString();
	}

}
