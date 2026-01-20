package PlayForward.demo.campaign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KampanjaRepository extends JpaRepository<Kampanja, Long> {
    java.util.List<Kampanja> findByPrimatelj_Id(Long primateljId);
}
