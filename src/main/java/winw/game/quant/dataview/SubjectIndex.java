package winw.game.quant.dataview;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "Subject_Index")
public class SubjectIndex {

	/**
	 * 指数代码
	 */
	@Id
	private String indexCode;
	private String indexName;

	private String consNumber;
	private Double latestClose;

	private Double monthlyReturn;

	private String publishDate;

	private String region;// 国家地区：境内
	private String currency;// 货币类别：人民币

	/**
	 * 关键指数，1是0否。用于筛选重点关注的指数。
	 */
	private Integer pivotal = 0;
}
