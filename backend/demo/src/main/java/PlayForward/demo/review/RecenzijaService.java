package PlayForward.demo.review;

import PlayForward.demo.review.dto.RecenzijaCreateRequest;
import PlayForward.demo.review.dto.RecenzijaResponse;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.DonatorRepository;
import PlayForward.demo.user.Primatelj;
import PlayForward.demo.user.PrimateljRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecenzijaService {

    private final RecenzijaRepository recenzijaRepository;
    private final DonatorRepository donatorRepository;
    private final PrimateljRepository primateljRepository;

    public RecenzijaService(RecenzijaRepository recenzijaRepository,
                            DonatorRepository donatorRepository,
                            PrimateljRepository primateljRepository) {
        this.recenzijaRepository = recenzijaRepository;
        this.donatorRepository = donatorRepository;
        this.primateljRepository = primateljRepository;
    }

    @Transactional
    public RecenzijaResponse create(RecenzijaCreateRequest request) {
        if (request == null) {
            throw new RuntimeException("Podaci recenzije nedostaju.");
        }

        Donator donator = donatorRepository.findById(request.idDonator)
                .orElseThrow(() -> new RuntimeException("Donator ne postoji."));
        Primatelj primatelj = primateljRepository.findById(request.idPrimatelj)
                .orElseThrow(() -> new RuntimeException("Primatelj ne postoji."));

        Recenzija recenzija = new Recenzija();
        recenzija.setOcjena(request.ocjena);
        recenzija.setTekst(request.komentar == null ? null : request.komentar.trim());
        recenzija.setDonator(donator);
        recenzija.setPrimatelj(primatelj);

        Recenzija saved = recenzijaRepository.save(recenzija);
        return RecenzijaResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<RecenzijaResponse> listByDonator(Long donatorId) {
        return recenzijaRepository.findByDonatorId(donatorId).stream()
                .map(RecenzijaResponse::fromEntity)
                .toList();
    }
}
