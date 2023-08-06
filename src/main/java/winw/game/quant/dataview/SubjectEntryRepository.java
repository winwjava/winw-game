package winw.game.quant.dataview;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubjectEntryRepository extends JpaRepository<SubjectEntry, Integer> {

	@Query("select o from SubjectEntry o where o.subjectId = ?1")
	List<SubjectEntry> findBySubjectId(Integer subjectId);
	
	@Query("select o from SubjectEntry o where o.code = ?1")
	List<SubjectEntry> findByCode(String code);

	@Query("select o from SubjectEntry o where o.code = ?1 and o.subjectId = ?2")
	boolean existCodeAndSubjectId(String code, Integer subjectId);

}
