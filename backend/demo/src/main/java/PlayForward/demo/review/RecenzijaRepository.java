package PlayForward.demo.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecenzijaRepository extends JpaRepository<Recenzija, Long> {
    List<Recenzija> findByDonatorIdOrderByIdDesc(Long idDonator);
}
