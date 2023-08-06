package winw.game.quant.dataview;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

	@Query("select o from Subject o where o.size > ?1")
	List<Subject> findBySize(int size);

	@Modifying
	@Query("update Subject s set s.size = (select count(o) from SubjectEntry o where o.subjectId = ?1 )  where id = ?1")
	int updateSubjectNum(Integer subjectId);
}
