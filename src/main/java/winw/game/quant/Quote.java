package winw.game.quant;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

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
	public final static String TIME_PATTERN = "HH:mm:ss";// 交易日期

	public static String today() {
		return DateFormatUtils.format(new Date(), DATE_PATTERN);
	}

	public static String addDays(String date, int amount) throws ParseException {
		return DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(date, DATE_PATTERN), amount), DATE_PATTERN);
	}

	public static String offset(String from, int observation) throws ParseException {
		return addDays(from, observation * 7 / 5 - 11);
	}

	public static int diff(String from, String to) throws ParseException {
		long fromTime = DateUtils.parseDate(from, Quote.DATE_PATTERN).getTime();
		long toTime = DateUtils.parseDate(to, Quote.DATE_PATTERN).getTime();
		int result = (int) ((toTime - fromTime) / 1000 / 3600 / 24) + 2;
		return result < 7 ? result : result * 7 / 5 + 1;
	}

	@Id
	@GeneratedValue
	protected long id;

	protected String code;// 代码

	@Transient
	protected String name;// 名称

	protected Double open; // 开盘价
	protected Double high; // 最高价
	protected Double low; // 最低价
	protected Double close; // 收盘价

	protected Integer volume; // 成交量
	protected Double amount; // 成交金额

	protected String date;// 交易日期
	@Transient
	protected String time;// 报价时间
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

	public void setTime(Date time) {
		this.date = DateFormatUtils.format(time, DATE_PATTERN);
		this.time = DateFormatUtils.format(time, TIME_PATTERN);
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
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
