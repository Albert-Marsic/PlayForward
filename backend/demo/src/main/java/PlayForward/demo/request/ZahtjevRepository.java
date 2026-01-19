package PlayForward.demo.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ZahtjevRepository extends JpaRepository<Zahtjev, Long> {

    List<Zahtjev> findByPrimatelj_IdOrderByDatumZahtjevaDesc(Long primateljId);

    List<Zahtjev> findByPrimatelj_IdAndStatusOrderByDatumZahtjevaDesc(Long primateljId, StatusZahtjeva status);

    List<Zahtjev> findByDonator_IdOrderByDatumZahtjevaDesc(Long donatorId);

    boolean existsByIgracka_IdAndStatusIn(Long igrackaId, Collection<StatusZahtjeva> statuses);

    void deleteByPrimatelj_Id(Long primateljId);

    void deleteByDonator_Id(Long donatorId);

    List<Zahtjev> findByIgracka_Id(Long igrackaId);
}
