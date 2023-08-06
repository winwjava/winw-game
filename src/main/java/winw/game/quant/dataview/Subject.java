package winw.game.quant.dataview;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * 主题（题材）、概念
 * 
 * @author winw
 *
 */
@Data
@Entity
@Table(name = "Subject")
public class Subject {

	@Id
	@GeneratedValue
	private int id;

	private String name;

	private Integer size = 0;// 成分数量
	
	// 包含哪些个股，每个个股的权重
	@OneToMany(mappedBy = "subjectId")
	private Set<SubjectEntry> entrySet;

}
