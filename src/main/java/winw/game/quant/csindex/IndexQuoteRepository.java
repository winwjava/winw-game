package winw.game.quant.csindex;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IndexQuoteRepository extends JpaRepository<IndexQuote, Integer> {

//	@Query("select o from IndexQuote o where o.indx_code = ?1")
//	List<IndexQuote> findByIndexCode(String IndexCode);

//	@Query("select o.tclose from IndexQuote o where o.indx_code = ?1 order by o.id desc")
//	List<String> findByIndexCode(String IndexCode, PageRequest page);

	@Query("select o.tradedate from IndexQuote o where o.indx_code = ?1 order by o.id desc")
	List<String> getLastDays(String IndexCode, PageRequest page);
	

	/**
	 * 取最近30天数据。
	 * 
	 * @param IndexCode
	 * @param page
	 * @return
	 */
	@Query("select o.tclose from IndexQuote o where o.indx_code = ?1 order by o.id desc")
	List<String> findByIndexCode(String IndexCode, PageRequest page);
}
