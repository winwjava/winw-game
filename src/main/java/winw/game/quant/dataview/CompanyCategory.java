package winw.game.quant.dataview;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

/**
 * 公司所属分类。
 * 
 * @author winw
 *
 */
@Deprecated
@Data
public class CompanyCategory {

	@Id
	@GeneratedValue
	private int id;
	
	private String companyCode;
	private String companyName;
	private String categoryCode;
}
