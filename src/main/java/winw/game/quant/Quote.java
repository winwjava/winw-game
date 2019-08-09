package winw.game.quant;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 报价。
 * 
 * 分为实时报价和分段报价，分段报价有：每日、每周、每月。
 * 
 * @author winw
 *
 */

@Entity
@Table(name = "QUOTE")
public class Quote {

	public final static String DATE_PATTERN = "yyyy-MM-dd";// 交易日期

	@Id
	@GeneratedValue
	protected long id;

	protected String code;// 代码

	@Transient
	protected String name;// 名称

	protected String date;// 交易日期

	protected Double open; // 开盘价
	protected Double high; // 最高价
	protected Double low; // 最低价
	protected Double close; // 收盘价

	protected Integer volume; // 成交量
	protected Double amount; // 成交金额

	protected QuotePeriod quotePeriod;// 报价类型

	// 实时报价，买一价，卖一价
	@Transient
	protected Date time;// 报价时间
	@Transient
	protected Double price = 0.0;// 价格
	@Transient
	protected Double previousClose = 0.0;// 昨日收盘价
	@Transient
	protected Double pe = 0.0;// 市盈率
	// private double eps = 0.0;
	@Transient
	protected Double marketCap = 0.0;// 总市值
	@Transient
	protected Double marketVal = 0.0;// 流通市值

	public boolean realtime() {
		return QuotePeriod.REALTIME.equals(quotePeriod);
	}

	public Quote() {
		super();
	}

	public Quote(String date, double open, double high, double low, double close, int volume, double amount) {
		super();
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.amount = amount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public QuotePeriod getQuotePeriod() {
		return quotePeriod;
	}

	public void setQuotePeriod(QuotePeriod quotePeriod) {
		this.quotePeriod = quotePeriod;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getPreviousClose() {
		return previousClose;
	}

	public void setPreviousClose(Double previousClose) {
		this.previousClose = previousClose;
	}

	public Double getPe() {
		return pe;
	}

	public void setPe(Double pe) {
		this.pe = pe;
	}

	public Double getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(Double marketCap) {
		this.marketCap = marketCap;
	}

	public Double getMarketVal() {
		return marketVal;
	}

	public void setMarketVal(Double marketVal) {
		this.marketVal = marketVal;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Quote [date=");
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
