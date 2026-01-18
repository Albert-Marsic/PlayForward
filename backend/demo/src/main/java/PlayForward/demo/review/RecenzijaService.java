package PlayForward.demo.review;

import PlayForward.demo.review.dto.RecenzijaCreateRequest;
import PlayForward.demo.review.dto.RecenzijaResponse;
import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.DonatorRepository;
import PlayForward.demo.user.KorisnikRepository;
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
    private final KorisnikRepository korisnikRepository;
    private final EmailService emailService;

    public RecenzijaService(RecenzijaRepository recenzijaRepository,
                            DonatorRepository donatorRepository,
                            PrimateljRepository primateljRepository,
                            KorisnikRepository korisnikRepository,
                            EmailService emailService) {
        this.recenzijaRepository = recenzijaRepository;
        this.donatorRepository = donatorRepository;
        this.primateljRepository = primateljRepository;
        this.korisnikRepository = korisnikRepository;
        this.emailService = emailService;
    }

    @Transactional
    public RecenzijaResponse create(RecenzijaCreateRequest request) {
        if (request == null) {
            throw new RuntimeException("Podaci nedostaju.");
        }
        if (request.idDonator == null || request.idDonator <= 0) {
            throw new RuntimeException("Donator nije valjan.");
        }
        if (request.ocjena == null || request.ocjena < 1 || request.ocjena > 5) {
            throw new RuntimeException("Ocjena mora biti između 1 i 5.");
        }
        if (request.tekst == null || request.tekst.isBlank()) {
            throw new RuntimeException("Komentar je obavezan.");
        }

        Primatelj primatelj = currentPrimateljOrThrow();
        Donator donator = donatorRepository.findById(request.idDonator)
                .orElseThrow(() -> new RuntimeException("Donator ne postoji."));

        Recenzija recenzija = new Recenzija();
        recenzija.setOcjena(request.ocjena);
        recenzija.setTekst(request.tekst.trim());
        recenzija.setPrimatelj(primatelj);
        recenzija.setDonator(donator);

        Recenzija saved = recenzijaRepository.save(recenzija);

        String donatorEmail = donator.getKorisnik() == null ? null : donator.getKorisnik().getEmail();
        emailService.sendReviewNotification(donatorEmail, saved.getOcjena(), saved.getTekst());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RecenzijaResponse> listByDonator(Long idDonator) {
        if (idDonator == null || idDonator <= 0) {
            throw new RuntimeException("Donator nije valjan.");
        }
        return recenzijaRepository.findByDonatorIdOrderByIdDesc(idDonator).stream()
                .map(this::toResponse)
                .toList();
    }

    private RecenzijaResponse toResponse(Recenzija recenzija) {
        RecenzijaResponse response = new RecenzijaResponse();
        response.id = recenzija.getId();
        response.ocjena = recenzija.getOcjena();
        response.tekst = recenzija.getTekst();
        response.idPrimatelj = recenzija.getPrimatelj() == null ? null : recenzija.getPrimatelj().getId();
        response.idDonator = recenzija.getDonator() == null ? null : recenzija.getDonator().getId();
        return response;
    }

    private Long currentKorisnikId() {
        String email = SecurityUtil.currentEmailOrThrow();

        return korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji u bazi."))
                .getId();
    }

    private Primatelj currentPrimateljOrThrow() {
        Long id = currentKorisnikId();

        return primateljRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Nemate PRIMATELJ ulogu (niste upisani u tablicu primatelj).")
                );
    }
}
