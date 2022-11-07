package winw.game.quant.dataview;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 主题指数组成，成分权重配置。
 * 
 * @author winw
 *
 */
@Data
@Entity
@Table(name = "Index_Compose")
public class IndexCompose {

	/**
	 * 指数代码
	 */
	@Id
	private String indexCode;

	private String quoteCode;// 成分个股代码

	private Double quoteWeight;// 占指数的权重

	private Double quoteMarketValue;// 按总市值计算

}
