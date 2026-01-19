package PlayForward.demo.review;

import PlayForward.demo.mail.EmailService;
import PlayForward.demo.request.StatusZahtjeva;
import PlayForward.demo.request.Zahtjev;
import PlayForward.demo.request.ZahtjevRepository;
import PlayForward.demo.review.dto.CreateRecenzijaRequest;
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
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji u bazi."))
                .getId();
    }

    private Primatelj currentPrimateljOrThrow() {
        Long id = currentKorisnikId();
        return primateljRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Nemate PRIMATELJ ulogu."));
    }

    private Donator currentDonatorOrThrow() {
        Long id = currentKorisnikId();
        return donatorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Nemate DONATOR ulogu."));
    }

    @Transactional
    public Recenzija create(CreateRecenzijaRequest req) {
        if (req == null || req.zahtjevId == null || req.zahtjevId <= 0) {
            throw new RuntimeException("ID zahtjeva je obavezan.");
        }
        if (req.ocjena == null || req.ocjena < 1 || req.ocjena > 5) {
            throw new RuntimeException("Ocjena mora biti između 1 i 5.");
        }
        if (req.tekst == null || req.tekst.trim().isEmpty()) {
            throw new RuntimeException("Tekst recenzije je obavezan.");
        }

        String trimmed = req.tekst.trim();
        if (trimmed.length() < 10) {
            throw new RuntimeException("Recenzija mora imati najmanje 10 znakova.");
        }
        if (trimmed.length() > 500) {
            throw new RuntimeException("Recenzija može imati najviše 500 znakova.");
        }

        Primatelj primatelj = currentPrimateljOrThrow();

        Zahtjev zahtjev = zahtjevRepo.findById(req.zahtjevId)
                .orElseThrow(() -> new RuntimeException("Zahtjev ne postoji."));

        if (!zahtjev.getPrimatelj().getId().equals(primatelj.getId())) {
            throw new RuntimeException("Nemate pravo ocjenjivati ovaj zahtjev.");
        }

        if (zahtjev.getStatus() != StatusZahtjeva.COMPLETED) {
            throw new RuntimeException("Recenzija je moguća tek nakon preuzimanja.");
        }

        if (recenzijaRepo.existsByZahtjev_Id(zahtjev.getId())) {
            throw new RuntimeException("Recenzija za ovaj zahtjev već postoji.");
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
            throw new RuntimeException("Email donatora nije dostupan.");
        }

        try {
            emailService.sendReviewNotification(donorEmail, saved);
        } catch (Exception ex) {
            throw new RuntimeException("Greška pri slanju emaila donatoru.", ex);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Recenzija> listForCurrentDonator() {
        Donator donator = currentDonatorOrThrow();
        return recenzijaRepo.findByDonator_IdOrderByIdDesc(donator.getId());
    }

    @Transactional(readOnly = true)
    public List<Recenzija> listForDonator(Long donatorId) {
        if (donatorId == null || donatorId <= 0) {
            throw new RuntimeException("ID donatora je obavezan.");
        }

        Donator donator = currentDonatorOrThrow();
        if (!donator.getId().equals(donatorId)) {
            throw new RuntimeException("Nemate pravo pregledavati tuđe recenzije.");
        }

        return recenzijaRepo.findByDonator_IdOrderByIdDesc(donatorId);
    }
}
