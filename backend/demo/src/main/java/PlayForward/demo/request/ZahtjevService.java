package PlayForward.demo.request;

import PlayForward.demo.listing.Igracka;
import PlayForward.demo.listing.IgrackaRepository;
import PlayForward.demo.listing.StatusIgracke;
import PlayForward.demo.request.dto.CreateZahtjevRequest;
import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.DonatorRepository;
import PlayForward.demo.user.KorisnikRepository;
import PlayForward.demo.user.Primatelj;
import PlayForward.demo.user.PrimateljRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class ZahtjevService {

    private static final Set<StatusZahtjeva> ACTIVE_STATUSES =
            EnumSet.of(StatusZahtjeva.PENDING, StatusZahtjeva.APPROVED, StatusZahtjeva.COMPLETED);

    private final ZahtjevRepository zahtjevRepo;
    private final IgrackaRepository igrackaRepo;
    private final KorisnikRepository korisnikRepo;
    private final DonatorRepository donatorRepo;
    private final PrimateljRepository primateljRepo;

    public ZahtjevService(ZahtjevRepository zahtjevRepo,
                          IgrackaRepository igrackaRepo,
                          KorisnikRepository korisnikRepo,
                          DonatorRepository donatorRepo,
                          PrimateljRepository primateljRepo) {
        this.zahtjevRepo = zahtjevRepo;
        this.igrackaRepo = igrackaRepo;
        this.korisnikRepo = korisnikRepo;
        this.donatorRepo = donatorRepo;
        this.primateljRepo = primateljRepo;
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
    public Zahtjev create(CreateZahtjevRequest req) {
        if (req == null || req.igrackaId == null || req.igrackaId <= 0) {
            throw new RuntimeException("ID igračke je obavezan.");
        }

        Primatelj primatelj = currentPrimateljOrThrow();
        Igracka igracka = igrackaRepo.findById(req.igrackaId)
                .orElseThrow(() -> new RuntimeException("Igračka ne postoji."));

        if (igracka.getStatus() != StatusIgracke.DOSTUPNO) {
            throw new RuntimeException("Igračka više nije dostupna.");
        }

        if (igracka.getDonator() != null && igracka.getDonator().getId().equals(primatelj.getId())) {
            throw new RuntimeException("Ne možete zatražiti vlastitu igračku.");
        }

        if (zahtjevRepo.existsByIgracka_IdAndStatusIn(igracka.getId(), ACTIVE_STATUSES)) {
            throw new RuntimeException("Za ovu igračku već postoji aktivan zahtjev.");
        }

        igracka.setStatus(StatusIgracke.REZERVIRANO);
        igracka.setPrimatelj(primatelj);
        igrackaRepo.save(igracka);

        Zahtjev zahtjev = new Zahtjev();
        zahtjev.setStatus(StatusZahtjeva.APPROVED);
        zahtjev.setDatumZahtjeva(LocalDateTime.now());
        zahtjev.setNapomena(req.napomena == null ? null : req.napomena.trim());
        zahtjev.setIgracka(igracka);
        zahtjev.setPrimatelj(primatelj);
        zahtjev.setDonator(igracka.getDonator());

        return zahtjevRepo.save(zahtjev);
    }

    @Transactional(readOnly = true)
    public List<Zahtjev> listForCurrentPrimatelj() {
        Primatelj primatelj = currentPrimateljOrThrow();
        return zahtjevRepo.findByPrimatelj_IdOrderByDatumZahtjevaDesc(primatelj.getId());
    }

    @Transactional(readOnly = true)
    public Zahtjev getForCurrentPrimatelj(Long zahtjevId) {
        if (zahtjevId == null || zahtjevId <= 0) {
            throw new RuntimeException("ID zahtjeva nije validan.");
        }

        Primatelj primatelj = currentPrimateljOrThrow();
        Zahtjev zahtjev = zahtjevRepo.findById(zahtjevId)
                .orElseThrow(() -> new RuntimeException("Zahtjev ne postoji."));

        if (!zahtjev.getPrimatelj().getId().equals(primatelj.getId())) {
            throw new RuntimeException("Nemate pravo pristupa ovom zahtjevu.");
        }

        return zahtjev;
    }

    @Transactional
    public Zahtjev withdraw(Long zahtjevId) {
        Primatelj primatelj = currentPrimateljOrThrow();
        Zahtjev zahtjev = zahtjevRepo.findById(zahtjevId)
                .orElseThrow(() -> new RuntimeException("Zahtjev ne postoji."));

        if (!zahtjev.getPrimatelj().getId().equals(primatelj.getId())) {
            throw new RuntimeException("Nemate pravo odustati od ovog zahtjeva.");
        }

        if (zahtjev.getStatus() != StatusZahtjeva.PENDING
                && zahtjev.getStatus() != StatusZahtjeva.APPROVED) {
            throw new RuntimeException("Ne možete odustati od ovog zahtjeva.");
        }

        zahtjev.setStatus(StatusZahtjeva.WITHDRAWN);

        Igracka igracka = zahtjev.getIgracka();
        if (igracka != null && igracka.getPrimatelj() != null
                && igracka.getPrimatelj().getId().equals(primatelj.getId())) {
            igracka.setStatus(StatusIgracke.DOSTUPNO);
            igracka.setPrimatelj(null);
            igrackaRepo.save(igracka);
        }

        return zahtjevRepo.save(zahtjev);
    }

    @Transactional
    public Zahtjev markCompleted(Long zahtjevId) {
        Primatelj primatelj = currentPrimateljOrThrow();
        Zahtjev zahtjev = zahtjevRepo.findById(zahtjevId)
                .orElseThrow(() -> new RuntimeException("Zahtjev ne postoji."));

        if (!zahtjev.getPrimatelj().getId().equals(primatelj.getId())) {
            throw new RuntimeException("Nemate pravo mijenjati ovaj zahtjev.");
        }

        if (zahtjev.getStatus() != StatusZahtjeva.APPROVED) {
            throw new RuntimeException("Zahtjev mora biti odobren prije potvrde preuzimanja.");
        }

        zahtjev.setStatus(StatusZahtjeva.COMPLETED);
        return zahtjevRepo.save(zahtjev);
    }

    @Transactional(readOnly = true)
    public List<Zahtjev> listForCurrentDonator() {
        Donator donator = currentDonatorOrThrow();
        return zahtjevRepo.findByDonator_IdOrderByDatumZahtjevaDesc(donator.getId());
    }
}
