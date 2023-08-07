package winw.game.quant.dataview;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;

/**
 * 板块主题的成分。
 * 
 * @author winw
 *
 */
@Data
@Entity
@Table(name = "Subject_Entry", indexes = {
		@Index(name = "subject_entry_unique", columnList = "subjectId,code", unique = true) })
public class SubjectEntry {

	@Id
	@GeneratedValue
	private int id;

	private Integer subjectId;

	private String code;

	private String name;

	/**
	 * 权重，一般用总市值表示，当前的总市值占所有的总市值的比例，即是权重。
	 * 
	 * <Point>
	 * 权重是变化的，每天都需要重新设定。
	 */
	private Double weight;// 这里用流通市值

	public SubjectEntry() {
		super();
	}

	public SubjectEntry(Integer subjectId,String code, String name) {
		super();
		this.subjectId = subjectId;
		this.code = code;
		this.name = name;
	}

	public SubjectEntry(Integer subjectId, String code, String name, Double weight) {
		super();
		this.subjectId = subjectId;
		this.code = code;
		this.name = name;
		this.weight = weight;
	}

}
