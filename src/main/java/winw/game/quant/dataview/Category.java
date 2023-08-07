package winw.game.quant.dataview;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 分类，根据证监会发布的上市公司行业分类《2021年3季度上市公司行业分类结果》
 * <Point>
 * 或自定义分类。
 * 
 * @author winw
 *
 */
@Deprecated
@Data
@Entity
@Table(name = "Category")
public class Category {

	@Id
	private String code;
	private String name;

	public Category() {
		super();
	}

	public Category(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

}
