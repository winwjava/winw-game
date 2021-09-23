package winw.game.quant.csindex;

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
@Table(name = "Subject_Index_Quote")
public class IndexQuote {

	@Id
	private String id;

	/**
	 * 指数代码
	 */
	private String indx_code;
	/**
	 * 昨日收盘价
	 */
	private String lclose;
	/**
	 * 今日收盘价
	 */
	private String tclose;
	/**
	 * 交易日期，例如："2021-08-27 00:00:00"
	 */
	private String tradedate;
	/**
	 * 
	 */
	private String changes;
}
