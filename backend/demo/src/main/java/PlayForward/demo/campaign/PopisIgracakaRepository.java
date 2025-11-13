package PlayForward.demo.campaign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopisIgracakaRepository extends JpaRepository<PopisIgracaka, PopisIgracakaId> {
}
