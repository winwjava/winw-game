package winw.game.stock;

import javax.persistence.Column;
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

	private Double buyPrice;
	private Double sellPrice;

	private Double buyDiff;
	private Double sellDiff;

	private Double buyDea;
	private Double sellDea;

	@Column(nullable = true)
	private Double buyMacd;

	@Column(nullable = true)
	private Double sellMacd;

	private Double profit;

	private Double profitRate;

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

	public String getBuyDate() {
		return buyDate;
	}

	public void setBuyDate(String buyDate) {
		this.buyDate = buyDate;
	}

	public Double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(Double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Double getBuyDiff() {
		return buyDiff;
	}

	public void setBuyDiff(Double buyDiff) {
		this.buyDiff = buyDiff;
	}

	public Double getBuyDea() {
		return buyDea;
	}

	public void setBuyDea(Double buyDea) {
		this.buyDea = buyDea;
	}

	public String getSellDate() {
		return sellDate;
	}

	public void setSellDate(String sellDate) {
		this.sellDate = sellDate;
	}

	public Double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(Double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Double getSellDiff() {
		return sellDiff;
	}

	public void setSellDiff(Double sellDiff) {
		this.sellDiff = sellDiff;
	}

	public Double getSellDea() {
		return sellDea;
	}

	public void setSellDea(Double sellDea) {
		this.sellDea = sellDea;
	}

	public Double getBuyMacd() {
		return buyMacd;
	}

	public void setBuyMacd(Double buyMacd) {
		this.buyMacd = buyMacd;
	}

	public Double getSellMacd() {
		return sellMacd;
	}

	public void setSellMacd(Double sellMacd) {
		this.sellMacd = sellMacd;
	}

	public Double getProfit() {
		return profit;
	}

	public void setProfit(Double profit) {
		this.profit = profit;
	}

	public Double getProfitRate() {
		return profitRate;
	}

	public void setProfitRate(Double profitRate) {
		this.profitRate = profitRate;
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
