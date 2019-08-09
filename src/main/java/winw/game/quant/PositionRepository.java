package winw.game.quant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Integer> {

	List<Position> findByPid(int pid);

}
