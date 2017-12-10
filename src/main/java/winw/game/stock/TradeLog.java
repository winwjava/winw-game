package winw.game.stock;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author winw
 *
 */
@Entity
@Table(name = "TRADE_LOG")
public class TradeLog {

	@Id
	@GeneratedValue
	protected long id;

	protected String code;

	protected String name;

	private String buyDate;
	private String sellDate;

	private double buyPrice;
	private double sellPrice;

	private double buyDiff;
	private double sellDiff;

	private double buyDea;
	private double sellDea;

	private double profit;

	private double profitRate;

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

	public String getBuyDate() {
		return buyDate;
	}

	public void setBuyDate(String buyDate) {
		this.buyDate = buyDate;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public double getBuyDiff() {
		return buyDiff;
	}

	public void setBuyDiff(double buyDiff) {
		this.buyDiff = buyDiff;
	}

	public double getBuyDea() {
		return buyDea;
	}

	public void setBuyDea(double buyDea) {
		this.buyDea = buyDea;
	}

	public String getSellDate() {
		return sellDate;
	}

	public void setSellDate(String sellDate) {
		this.sellDate = sellDate;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public double getSellDiff() {
		return sellDiff;
	}

	public void setSellDiff(double sellDiff) {
		this.sellDiff = sellDiff;
	}

	public double getSellDea() {
		return sellDea;
	}

	public void setSellDea(double sellDea) {
		this.sellDea = sellDea;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public double getProfitRate() {
		return profitRate;
	}

	public void setProfitRate(double profitRate) {
		this.profitRate = profitRate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradeLog [id=");
		builder.append(id);
		builder.append(", buyDate=");
		builder.append(buyDate);
		builder.append(", buyPrice=");
		builder.append(buyPrice);
		builder.append(", buyDiff=");
		builder.append(buyDiff);
		builder.append(", buyDea=");
		builder.append(buyDea);
		builder.append(", sellDate=");
		builder.append(sellDate);
		builder.append(", sellPrice=");
		builder.append(sellPrice);
		builder.append(", sellDiff=");
		builder.append(sellDiff);
		builder.append(", sellDea=");
		builder.append(sellDea);
		builder.append(", profit=");
		builder.append(profit);
		builder.append(", profitRate=");
		builder.append(profitRate);
		builder.append("]");
		return builder.toString();
	}

}
