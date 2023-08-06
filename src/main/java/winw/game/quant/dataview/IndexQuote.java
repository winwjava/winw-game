package winw.game.quant.dataview;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 指数每日报价。
 * 
 * @author winw
 *
 */
@Data
@Entity
@Table(name = "Indices_Quote")
public class IndexQuote {

	@Id
	private String id;

	/**
	 * 指数代码
	 */
	private String indexCode;
	private String tradeDate;//20210825
	private Double open;
	private Double close;
	private Double high;
	private Double low;
	private Double change;
	private Double tradingVol;
	private Double tradingValue;
}
