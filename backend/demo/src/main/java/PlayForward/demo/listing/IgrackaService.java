package PlayForward.demo.listing;

import PlayForward.demo.listing.dto.CreateIgrackaRequest;
import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IgrackaService {

    private final IgrackaRepository igrackaRepo;
    private final KorisnikRepository korisnikRepo;
    private final DonatorRepository donatorRepo;
    private final PrimateljRepository primateljRepo;

    public IgrackaService(IgrackaRepository igrackaRepo,
                          KorisnikRepository korisnikRepo,
                          DonatorRepository donatorRepo,
                          PrimateljRepository primateljRepo) {
        this.igrackaRepo = igrackaRepo;
        this.korisnikRepo = korisnikRepo;
        this.donatorRepo = donatorRepo;
        this.primateljRepo = primateljRepo;
    }

    // -------------------------
    // POMOĆNE METODE (tko je prijavljen)
    // -------------------------

    // Iz JWT-a uzmemo email, pa iz baze nađemo našeg korisnika (korisnik tablica).
    private Long currentKorisnikId() {
        String email = SecurityUtil.currentEmailOrThrow();

        Korisnik k = korisnikRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji u bazi."));

        return k.getId();
    }

    // Ako korisnik nije donator (nema reda u tablici DONATOR), bacimo grešku.
    private Donator currentDonatorOrThrow() {
        Long id = currentKorisnikId();
        return donatorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Nemate DONATOR ulogu (ne postoji zapis u tablici donator)."));
    }

    // Ako korisnik nije primatelj (nema reda u tablici PRIMATELJ), bacimo grešku.
    private Primatelj currentPrimateljOrThrow() {
        Long id = currentKorisnikId();
        return primateljRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Nemate PRIMATELJ ulogu (ne postoji zapis u tablici primatelj)."));
    }

    // -------------------------
    // 1) DONATOR: objavi igračku
    // -------------------------
    @Transactional
    public Igracka create(CreateIgrackaRequest req) {

        // Jednostavne provjere (da ne upišemo prazne podatke)
        if (req == null) throw new RuntimeException("Tijelo zahtjeva nedostaje.");
        if (req.naziv == null || req.naziv.isBlank()) throw new RuntimeException("Naziv je obavezan.");
        if (req.kategorija == null || req.kategorija.isBlank()) throw new RuntimeException("Kategorija je obavezna.");
        if (req.stanje == null) throw new RuntimeException("Stanje je obavezno.");
        if (req.fotografija == null || req.fotografija.isBlank()) throw new RuntimeException("Fotografija je obavezna.");

        // Ovdje pazimo da stvarno imamo donatora
        Donator donator = currentDonatorOrThrow();

        // Napravimo novi objekt i spremimo u bazu
        Igracka i = new Igracka();
        i.setNaziv(req.naziv.trim());
        i.setKategorija(req.kategorija.trim());
        i.setStanje(req.stanje);
        i.setFotografija(req.fotografija.trim());
        i.setUvjeti(req.uvjeti == null ? null : req.uvjeti.trim());

        // Na početku je igračka dostupna i nema primatelja
        i.setStatus(StatusIgracke.dostupno);
        i.setDonator(donator);
        i.setPrimatelj(null);

        return igrackaRepo.save(i);
    }

    // -------------------------
    // 2) DONATOR: promijeni uvjete (samo vlasnik i samo ako nije rezervirano)
    // -------------------------
    @Transactional
    public Igracka updateUvjeti(Long igrackaId, String uvjeti) {

        Igracka i = igrackaRepo.findById(igrackaId)
                .orElseThrow(() -> new RuntimeException("Igračka ne postoji."));

        // Samo donator koji je objavio igračku smije mijenjati uvjete
        Long currentId = currentKorisnikId();
        if (!i.getDonator().getId().equals(currentId)) {
            throw new RuntimeException("Nemate pravo mijenjati ovu igračku.");
        }

        // Ako je već prihvaćena (rezervirana), nema promjena uvjeta
        if (i.getStatus() == StatusIgracke.rezervirano
        ) {
            throw new RuntimeException("Ne možete mijenjati uvjete jer je igračka rezervirana.");
        }

        i.setUvjeti(uvjeti == null ? null : uvjeti.trim());
        return igrackaRepo.save(i);
    }

    // -------------------------
    // 3) PRIMATELJ: filtriraj dostupne igračke
    // -------------------------
    @Transactional(readOnly = true)
    public List<Igracka> filter(String kategorija, StanjeIgracke stanje) {

        // Uvijek vraćamo samo one koje su DOSTUPNE
        if ((kategorija == null || kategorija.isBlank()) && stanje == null) {
            return igrackaRepo.findByStatus(StatusIgracke.dostupno);
        }

        if (kategorija != null && !kategorija.isBlank() && stanje == null) {
            return igrackaRepo.findByStatusAndKategorijaIgnoreCase(
                    StatusIgracke.dostupno, kategorija.trim()
            );
        }

        if ((kategorija == null || kategorija.isBlank()) && stanje != null) {
            return igrackaRepo.findByStatusAndStanje(StatusIgracke.dostupno, stanje);
        }

        return igrackaRepo.findByStatusAndKategorijaIgnoreCaseAndStanje(
                StatusIgracke.dostupno,
                kategorija.trim(),
                stanje
        );
    }

    // -------------------------
    // 4) PRIMATELJ: pošalji zahtjev za donaciju
    // -------------------------
    @Transactional
    public Igracka posaljiZahtjev(Long igrackaId) {

        // Mora biti primatelj
        Primatelj primatelj = currentPrimateljOrThrow();

        Igracka i = igrackaRepo.findById(igrackaId)
                .orElseThrow(() -> new RuntimeException("Igračka ne postoji."));

        // Zahtjev se može poslati samo ako je igračka dostupna
        if (i.getStatus() != StatusIgracke.dostupno) {
            throw new RuntimeException("Igračka nije dostupna za slanje zahtjeva.");
        }

        // Zapišemo tko je poslao zahtjev i promijenimo status u ZAHTJEV
        i.setPrimatelj(primatelj);
        i.setStatus(StatusIgracke.zahtjev);

        return igrackaRepo.save(i);
    }

    // -------------------------
    // 5) DONATOR: “obavijesti” = vidi sve moje zahtjeve (status ZAHTJEV)
    // -------------------------
    @Transactional(readOnly = true)
    public List<Igracka> mojiZahtjevi() {
        Donator donator = currentDonatorOrThrow();
        return igrackaRepo.findByDonator_IdAndStatus(donator.getId(), StatusIgracke.zahtjev);
    }

    // -------------------------
    // 6) DONATOR: prihvati zahtjev => REZERVIRANO
    // -------------------------
    @Transactional
    public Igracka prihvatiZahtjev(Long igrackaId) {

        Long currentId = currentKorisnikId();

        Igracka i = igrackaRepo.findById(igrackaId)
                .orElseThrow(() -> new RuntimeException("Igračka ne postoji."));

        // Samo vlasnik (donator) može prihvatiti
        if (!i.getDonator().getId().equals(currentId)) {
            throw new RuntimeException("Nemate pravo prihvatiti zahtjev za ovu igračku.");
        }

        // Mora postojati zahtjev
        if (i.getStatus() != StatusIgracke.zahtjev || i.getPrimatelj() == null) {
            throw new RuntimeException("Za ovu igračku nema aktivnog zahtjeva.");
        }

        i.setStatus(StatusIgracke.rezervirano);
        return igrackaRepo.save(i);
    }

    // (preporučeno) Donator odbije zahtjev => opet DOSTUPNO
    @Transactional
    public Igracka odbijZahtjev(Long igrackaId) {

        Long currentId = currentKorisnikId();

        Igracka i = igrackaRepo.findById(igrackaId)
                .orElseThrow(() -> new RuntimeException("Igračka ne postoji."));

        if (!i.getDonator().getId().equals(currentId)) {
            throw new RuntimeException("Nemate pravo odbiti zahtjev za ovu igračku.");
        }

        if (i.getStatus() != StatusIgracke.zahtjev) {
            throw new RuntimeException("Igračka nema zahtjev koji se može odbiti.");
        }

        i.setStatus(StatusIgracke.dostupno);
        i.setPrimatelj(null);
        return igrackaRepo.save(i);
    }
}
