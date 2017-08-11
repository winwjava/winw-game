package winw.game.stock;

import java.util.Date;

/**
 * stock
 * 
 * @author winw
 *
 */
public class Stock {
	private String code;// ����
	private String name;// ����

	private Date time;// ����ʱ��

	private double price = 0.0;// �۸�
	private double open = 0.0;// ���̼�
	private double high = 0.0;// ��߼�
	private double low = 0.0;// ��ͼ�

	private double previousClose = 0.0;// �������̼�

	private int volume = 0;// �ɽ���
	private double pe = 0.0;// ��ӯ��

	// private double eps = 0.0;
	// private double marketcap = 0.0;
	// private String currency = "";
	// private double shortRatio = 0.0;
	// private String exchange;

	public Stock() {
		super();
	}

	public Stock(String code, String name) {
		super();
		this.code = code;
		this.name = name;
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

	public double getPreviousClose() {
		return previousClose;
	}

	public void setPreviousClose(double previousClose) {
		this.previousClose = previousClose;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public double getPe() {
		return pe;
	}

	public void setPe(double pe) {
		this.pe = pe;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Stock [code=");
		builder.append(code);
		builder.append(", name=");
		builder.append(name);
		builder.append(", price=");
		builder.append(price);
		builder.append("]");
		return builder.toString();
	}

}
