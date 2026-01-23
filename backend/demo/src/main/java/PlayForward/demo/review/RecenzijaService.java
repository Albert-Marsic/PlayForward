package PlayForward.demo.review;

import PlayForward.demo.mail.EmailService;
import PlayForward.demo.request.StatusZahtjeva;
import PlayForward.demo.request.Zahtjev;
import PlayForward.demo.request.ZahtjevRepository;
import PlayForward.demo.review.dto.CreateRecenzijaRequest;
import PlayForward.demo.review.dto.RecenzijaResponse;
import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.DonatorRepository;
import PlayForward.demo.user.KorisnikRepository;
import PlayForward.demo.user.Primatelj;
import PlayForward.demo.user.PrimateljRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RecenzijaService {

    private static final Logger log = LoggerFactory.getLogger(RecenzijaService.class);

    private final RecenzijaRepository recenzijaRepo;
    private final ZahtjevRepository zahtjevRepo;
    private final KorisnikRepository korisnikRepo;
    private final DonatorRepository donatorRepo;
    private final PrimateljRepository primateljRepo;
    private final EmailService emailService;

    public RecenzijaService(RecenzijaRepository recenzijaRepo,
                            ZahtjevRepository zahtjevRepo,
                            KorisnikRepository korisnikRepo,
                            DonatorRepository donatorRepo,
                            PrimateljRepository primateljRepo,
                            EmailService emailService) {
        this.recenzijaRepo = recenzijaRepo;
        this.zahtjevRepo = zahtjevRepo;
        this.korisnikRepo = korisnikRepo;
        this.donatorRepo = donatorRepo;
        this.primateljRepo = primateljRepo;
        this.emailService = emailService;
    }

    private Long currentKorisnikId() {
        String email = SecurityUtil.currentEmailOrThrow();
        return korisnikRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Korisnik ne postoji u bazi."))
                .getId();
    }

    private Primatelj currentPrimateljOrThrow() {
        Long id = currentKorisnikId();
        return primateljRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Nemate PRIMATELJ ulogu."));
    }

    private Donator currentDonatorOrThrow() {
        Long id = currentKorisnikId();
        return donatorRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Nemate DONATOR ulogu."));
    }

    @Transactional
    public RecenzijaResponse create(CreateRecenzijaRequest req) {
        if (req == null || req.zahtjevId == null || req.zahtjevId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID zahtjeva je obavezan.");
        }
        if (req.ocjena == null || req.ocjena < 1 || req.ocjena > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ocjena mora biti između 1 i 5.");
        }
        if (req.tekst == null || req.tekst.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tekst recenzije je obavezan.");
        }

        String trimmed = req.tekst.trim();
        if (trimmed.length() < 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recenzija mora imati najmanje 10 znakova.");
        }
        if (trimmed.length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recenzija može imati najviše 500 znakova.");
        }

        Primatelj primatelj = currentPrimateljOrThrow();

        Zahtjev zahtjev = zahtjevRepo.findById(req.zahtjevId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Zahtjev ne postoji."));

        if (!zahtjev.getPrimatelj().getId().equals(primatelj.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nemate pravo ocjenjivati ovaj zahtjev.");
        }

        if (zahtjev.getStatus() != StatusZahtjeva.PICKED_UP
                && zahtjev.getStatus() != StatusZahtjeva.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recenzija je moguća tek nakon preuzimanja.");
        }

        if (recenzijaRepo.existsByZahtjev_Id(zahtjev.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Recenzija za ovaj zahtjev već postoji.");
        }

        Recenzija recenzija = new Recenzija();
        recenzija.setOcjena(req.ocjena);
        recenzija.setTekst(trimmed);
        recenzija.setZahtjev(zahtjev);
        recenzija.setPrimatelj(primatelj);
        recenzija.setDonator(zahtjev.getDonator());

        Recenzija saved = recenzijaRepo.save(recenzija);

        String donorEmail = zahtjev.getDonator() != null
                && zahtjev.getDonator().getKorisnik() != null
                ? zahtjev.getDonator().getKorisnik().getEmail()
                : null;
        if (donorEmail == null || donorEmail.isBlank()) {
            log.warn("Email donatora nije dostupan za zahtjev {}.", zahtjev.getId());
            return RecenzijaResponse.fromEntity(saved);
        }

        emailService.sendReviewNotificationAsync(donorEmail, saved);

        return RecenzijaResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<RecenzijaResponse> listForCurrentDonator() {
        Donator donator = currentDonatorOrThrow();
        return toResponseList(recenzijaRepo.findByDonator_IdOrderByIdDesc(donator.getId()));
    }

    @Transactional(readOnly = true)
    public List<RecenzijaResponse> listForDonator(Long donatorId) {
        if (donatorId == null || donatorId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID donatora je obavezan.");
        }

        Donator donator = currentDonatorOrThrow();
        if (!donator.getId().equals(donatorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nemate pravo pregledavati tuđe recenzije.");
        }

        return toResponseList(recenzijaRepo.findByDonator_IdOrderByIdDesc(donatorId));
    }

    @Transactional(readOnly = true)
    public List<RecenzijaResponse> listForCurrentPrimatelj() {
        Primatelj primatelj = currentPrimateljOrThrow();
        return toResponseList(recenzijaRepo.findByPrimatelj_IdOrderByIdDesc(primatelj.getId()));
    }

    private List<RecenzijaResponse> toResponseList(List<Recenzija> recenzije) {
        return recenzije.stream()
                .map(RecenzijaResponse::fromEntity)
                .toList();
    }
}
