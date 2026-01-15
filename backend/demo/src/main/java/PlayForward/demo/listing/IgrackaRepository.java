package PlayForward.demo.listing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IgrackaRepository extends JpaRepository<Igracka, Long> {

    // Primatelj: pregled dostupnih igračaka
    List<Igracka> findByStatus(StatusIgracke status);

    List<Igracka> findByStatusAndKategorijaIgnoreCase(StatusIgracke status, String kategorija);

    List<Igracka> findByStatusAndStanje(StatusIgracke status, StanjeIgracke stanje);

    List<Igracka> findByStatusAndKategorijaIgnoreCaseAndStanje(
            StatusIgracke status, String kategorija, StanjeIgracke stanje
    );

    // Donator: “obavijesti” = sve njegove igračke koje imaju ZAHTJEV
    List<Igracka> findByDonator_IdAndStatus(Long donatorId, StatusIgracke status);
}
