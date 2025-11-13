package PlayForward.demo.listing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IgrackaRepository extends JpaRepository<Igracka, Long> {
}
