package PlayForward.demo.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecenzijaRepository extends JpaRepository<Recenzija, Long> {
    boolean existsByZahtjev_Id(Long zahtjevId);

    List<Recenzija> findByDonator_IdOrderByIdDesc(Long donatorId);

    void deleteByDonator_Id(Long donatorId);

    void deleteByPrimatelj_Id(Long primateljId);
}
