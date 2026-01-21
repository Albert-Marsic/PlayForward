package PlayForward.demo.listing;

import PlayForward.demo.listing.dto.CreateIgrackaRequest;
import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IgrackaService {

    // Repozitoriji = objekti koji čitaju/pišu podatke u bazu
    private final IgrackaRepository igrackaRepo;
    private final KorisnikRepository korisnikRepo;
    private final DonatorRepository donatorRepo;
    private final PrimateljRepository primateljRepo;

    // Spring će sam ubaciti (injectati) ove repozitorije
    public IgrackaService(IgrackaRepository igrackaRepo,
                          KorisnikRepository korisnikRepo,
                          DonatorRepository donatorRepo,
                          PrimateljRepository primateljRepo) {
        this.igrackaRepo = igrackaRepo;
        this.korisnikRepo = korisnikRepo;
        this.donatorRepo = donatorRepo;
        this.primateljRepo = primateljRepo;
    }

    // ---------------------------------------------------------
    // POMOĆNE METODE
    // ---------------------------------------------------------

    // Dohvaća ID trenutno ulogiranog korisnika iz JWT tokena
    private Long currentKorisnikId() {
        // Iz JWT-a uzmemo email
        String email = SecurityUtil.currentEmailOrThrow();

        // Po emailu pronađemo korisnika u bazi i vratimo njegov ID
        return korisnikRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji u bazi."))
                .getId();
    }

    // Provjerava da je korisnik DONATOR i vraća njegov zapis
    private Donator currentDonatorOrThrow() {
        Long id = currentKorisnikId();

        return donatorRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Nemate DONATOR ulogu (niste upisani u tablicu donator).")
                );
    }

    // Provjerava da je korisnik PRIMATELJ i vraća njegov zapis
    private Primatelj currentPrimateljOrThrow() {
        Long id = currentKorisnikId();

        return primateljRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Nemate PRIMATELJ ulogu (niste upisani u tablicu primatelj).")
                );
    }

    // ---------------------------------------------------------
    // 1) DONATOR OBJAVLJUJE IGRAČKU
    // ---------------------------------------------------------
    @Transactional
    public Igracka create(CreateIgrackaRequest req) {

        // Provjere da korisnik nije poslao prazne podatke
        if (req == null) throw new RuntimeException("Podaci nedostaju.");
        if (req.naziv == null || req.naziv.isBlank()) throw new RuntimeException("Naziv je obavezan.");
        if (req.kategorija == null || req.kategorija.isBlank()) throw new RuntimeException("Kategorija je obavezna.");
        if (req.stanje == null) throw new RuntimeException("Stanje je obavezno.");
        if (req.fotografija == null || req.fotografija.isBlank())
            throw new RuntimeException("Fotografija je obavezna.");

        // Provjerimo da je korisnik donator
        Donator donator = currentDonatorOrThrow();

        // Napravimo novi objekt Igracka
        Igracka igracka = new Igracka();
        igracka.setNaziv(req.naziv.trim());
        igracka.setKategorija(req.kategorija.trim());
        igracka.setStanje(req.stanje);
        igracka.setFotografija(req.fotografija.trim());
        igracka.setUvjeti(req.uvjeti == null ? null : req.uvjeti.trim());

        // Nova igračka je uvijek dostupna
        igracka.setStatus(StatusIgracke.DOSTUPNO);

        // Postavimo tko je donator
        igracka.setDonator(donator);

        // Još nema primatelja
        igracka.setPrimatelj(null);

        // Spremimo u bazu
        return igrackaRepo.save(igracka);
    }

    // ---------------------------------------------------------
    // 2) DONATOR MIJENJA UVJETE
    // ---------------------------------------------------------
    @Transactional
    public Igracka updateUvjeti(Long igrackaId, String uvjeti) {

        // Pronađemo igračku u bazi
        Igracka igracka = igrackaRepo.findById(igrackaId)
                .orElseThrow(() -> new RuntimeException("Igračka ne postoji."));

        // Provjera: samo onaj donator koji ju je objavio smije je mijenjati
        Long currentId = currentKorisnikId();
        if (!igracka.getDonator().getId().equals(currentId)) {
            throw new RuntimeException("Nemate pravo mijenjati ovu igračku.");
        }

        // Ako je rezervirana, ne smije se više mijenjati
        if (igracka.getStatus() == StatusIgracke.REZERVIRANO) {
            throw new RuntimeException("Ne možete mijenjati uvjete jer je igračka već rezervirana.");
        }

        // Postavimo nove uvjete
        igracka.setUvjeti(uvjeti == null ? null : uvjeti.trim());

        return igrackaRepo.save(igracka);
    }

    // ---------------------------------------------------------
    // 3) PRIMATELJ FILTRIRA DOSTUPNE IGRAČKE
    // ---------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Igracka> filter(String kategorija, StanjeIgracke stanje) {

        // Ako ništa nije zadano – vrati sve dostupne
        if ((kategorija == null || kategorija.isBlank()) && stanje == null) {
            return igrackaRepo.findByStatus(StatusIgracke.DOSTUPNO);
        }

        // Samo kategorija
        if (kategorija != null && !kategorija.isBlank() && stanje == null) {
            return igrackaRepo.findByStatusAndKategorijaIgnoreCase(
                    StatusIgracke.DOSTUPNO,
                    kategorija.trim()
            );
        }

        // Samo stanje
        if ((kategorija == null || kategorija.isBlank()) && stanje != null) {
            return igrackaRepo.findByStatusAndStanje(
                    StatusIgracke.DOSTUPNO,
                    stanje
            );
        }

        // Kategorija + stanje
        return igrackaRepo.findByStatusAndKategorijaIgnoreCaseAndStanje(
                StatusIgracke.DOSTUPNO,
                kategorija.trim(),
                stanje
        );
    }

    // ---------------------------------------------------------
    // 3.5) DONATOR - MOJE IGRAČKE
    // ---------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Igracka> listForCurrentDonator() {
        Donator donator = currentDonatorOrThrow();
        return igrackaRepo.findByDonator_Id(donator.getId());
    }

    // ---------------------------------------------------------
    //  PRIMATELJ "ZATRAŽI" = REZERVIRA IGRAČKU
    // ---------------------------------------------------------
    @Transactional
    public Igracka rezerviraj(Long igrackaId) {

        // Provjera da je korisnik primatelj
        Primatelj primatelj = currentPrimateljOrThrow();

        Igracka igracka = igrackaRepo.findById(igrackaId)
                .orElseThrow(() -> new RuntimeException("Igračka ne postoji."));

        if (igracka.getStatus() != StatusIgracke.DOSTUPNO) {
            throw new RuntimeException("Igračka više nije dostupna.");
        }

        // Postaje rezervirana i vežemo primatelja
        igracka.setStatus(StatusIgracke.REZERVIRANO);
        igracka.setPrimatelj(primatelj);

        return igrackaRepo.save(igracka);
    }

    // ---------------------------------------------------------
    // BONUS: PRIMATELJ ODUSTANE -> OPET DOSTUPNO
    // ---------------------------------------------------------
    @Transactional
    public Igracka odustani(Long igrackaId) {

        Long currentId = currentKorisnikId();

        Igracka igracka = igrackaRepo.findById(igrackaId)
                .orElseThrow(() -> new RuntimeException("Igračka ne postoji."));

        // Mora biti rezervirana
        if (igracka.getStatus() != StatusIgracke.REZERVIRANO || igracka.getPrimatelj() == null) {
            throw new RuntimeException("Igračka nije rezervirana.");
        }

        // Samo onaj primatelj koji ju je rezervirao smije odustati
        if (!igracka.getPrimatelj().getId().equals(currentId)) {
            throw new RuntimeException("Samo primatelj koji je rezervirao smije odustati.");
        }

        // Vraćamo igračku u stanje dostupno
        igracka.setStatus(StatusIgracke.DOSTUPNO);
        igracka.setPrimatelj(null);

        return igrackaRepo.save(igracka);
    }

    // ---------------------------------------------------------
    // BONUS: DONATOR POVUČE OGLAS (DOK NIJE REZERVIRAN)
    // ---------------------------------------------------------
    @Transactional
    public void povuci(Long igrackaId) {

        Igracka igracka = igrackaRepo.findById(igrackaId)
                .orElseThrow(() -> new RuntimeException("Igračka ne postoji."));

        Long currentId = currentKorisnikId();

        if (!igracka.getDonator().getId().equals(currentId)) {
            throw new RuntimeException("Nemate pravo povući ovu igračku.");
        }

        if (igracka.getStatus() == StatusIgracke.REZERVIRANO) {
            throw new RuntimeException("Ne možete povući oglas jer je igračka već rezervirana.");
        }

        igrackaRepo.delete(igracka);
    }
    // ---------------------------------------------------------
// DODANO radi IgrackaController-a (da se projekt kompilira)
// ---------------------------------------------------------

    // Frontend / controller može tražiti detalje igračke po ID-u
    @Transactional(readOnly = true)
    public Igracka getById(Long igrackaId) {
        if (igrackaId == null || igrackaId <= 0) {
            throw new RuntimeException("ID igračke nije validan.");
        }
        return igrackaRepo.findById(igrackaId)
                .orElseThrow(() -> new RuntimeException("Igračka ne postoji."));
    }

    // Controller zove povuciOglas(), a prava logika je u povuci()
    @Transactional
    public void povuciOglas(Long igrackaId) {
        povuci(igrackaId);
    }

    // Controller zove odustaniOdPreuzimanja(), a prava logika je u odustani()
    @Transactional
    public Igracka odustaniOdPreuzimanja(Long igrackaId) {
        return odustani(igrackaId);
    }

}
