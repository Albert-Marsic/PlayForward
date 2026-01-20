package PlayForward.demo.user;

import PlayForward.demo.campaign.Kampanja;
import PlayForward.demo.campaign.KampanjaRepository;
import PlayForward.demo.listing.Igracka;
import PlayForward.demo.listing.IgrackaRepository;
import PlayForward.demo.listing.StatusIgracke;
import PlayForward.demo.request.Zahtjev;
import PlayForward.demo.request.ZahtjevRepository;
import PlayForward.demo.review.RecenzijaRepository;
import PlayForward.demo.user.dto.AdminCampaignView;
import PlayForward.demo.user.dto.AdminDonationView;
import PlayForward.demo.user.dto.AdminEmailView;
import PlayForward.demo.user.dto.AdminIgrackaView;
import PlayForward.demo.user.dto.AdminUserView;
import PlayForward.demo.campaign.PopisIgracakaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AdminUserService {

    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 200;

    private final KorisnikRepository korisnikRepository;
    private final AdminRepository adminRepository;
    private final DonatorRepository donatorRepository;
    private final PrimateljRepository primateljRepository;
    private final IgrackaRepository igrackaRepository;
    private final KampanjaRepository kampanjaRepository;
    private final PopisIgracakaRepository popisIgracakaRepository;
    private final ZahtjevRepository zahtjevRepository;
    private final RecenzijaRepository recenzijaRepository;
    private final AdminService adminService;

    public AdminUserService(KorisnikRepository korisnikRepository,
                            AdminRepository adminRepository,
                            DonatorRepository donatorRepository,
                            PrimateljRepository primateljRepository,
                            IgrackaRepository igrackaRepository,
                            KampanjaRepository kampanjaRepository,
                            PopisIgracakaRepository popisIgracakaRepository,
                            ZahtjevRepository zahtjevRepository,
                            RecenzijaRepository recenzijaRepository,
                            AdminService adminService) {
        this.korisnikRepository = korisnikRepository;
        this.adminRepository = adminRepository;
        this.donatorRepository = donatorRepository;
        this.primateljRepository = primateljRepository;
        this.igrackaRepository = igrackaRepository;
        this.kampanjaRepository = kampanjaRepository;
        this.popisIgracakaRepository = popisIgracakaRepository;
        this.zahtjevRepository = zahtjevRepository;
        this.recenzijaRepository = recenzijaRepository;
        this.adminService = adminService;
    }

    @Transactional(readOnly = true)
    public List<AdminUserView> listUsers(int limit, int offset) {
        int safeLimit = normalizeLimit(limit);
        int safeOffset = normalizeOffset(offset);

        List<Korisnik> korisnici = korisnikRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<Korisnik> slice = slice(korisnici, safeLimit, safeOffset);

        Set<Long> donatorIds = new HashSet<>();
        for (Donator donator : donatorRepository.findAll()) {
            donatorIds.add(donator.getId());
        }
        Set<Long> primateljIds = new HashSet<>();
        for (Primatelj primatelj : primateljRepository.findAll()) {
            primateljIds.add(primatelj.getId());
        }

        List<AdminUserView> results = new ArrayList<>(slice.size());
        for (Korisnik korisnik : slice) {
            AdminUserView view = new AdminUserView();
            view.id = korisnik.getId();
            view.email = korisnik.getEmail();
            view.ime = korisnik.getImeKorisnik();
            view.uloga = resolveUloga(korisnik, donatorIds, primateljIds);
            view.datumRegistracije = null;
            results.add(view);
        }
        return results;
    }

    @Transactional(readOnly = true)
    public List<AdminDonationView> listDonations(int limit, int offset) {
        int safeLimit = normalizeLimit(limit);
        int safeOffset = normalizeOffset(offset);

        List<Igracka> igracke = igrackaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<Igracka> slice = slice(igracke, safeLimit, safeOffset);

        List<AdminDonationView> results = new ArrayList<>(slice.size());
        for (Igracka igracka : slice) {
            AdminDonationView view = new AdminDonationView();
            view.id = igracka.getId();
            view.status = igracka.getStatus() == null
                    ? null
                    : igracka.getStatus().name().toLowerCase(Locale.ROOT);
            view.datumKreiranja = null;

            AdminIgrackaView igrackaView = new AdminIgrackaView();
            igrackaView.naziv = igracka.getNaziv();
            view.igracka = igrackaView;

            AdminEmailView donatorView = new AdminEmailView();
            if (igracka.getDonator() != null && igracka.getDonator().getKorisnik() != null) {
                donatorView.email = igracka.getDonator().getKorisnik().getEmail();
            }
            view.donator = donatorView;

            results.add(view);
        }
        return results;
    }

    @Transactional(readOnly = true)
    public List<AdminCampaignView> listCampaigns(int limit, int offset) {
        int safeLimit = normalizeLimit(limit);
        int safeOffset = normalizeOffset(offset);

        List<Kampanja> kampanje = kampanjaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<Kampanja> slice = slice(kampanje, safeLimit, safeOffset);

        List<AdminCampaignView> results = new ArrayList<>(slice.size());
        for (Kampanja kampanja : slice) {
            AdminCampaignView view = new AdminCampaignView();
            view.id = kampanja.getId();
            view.napredak = kampanja.getNapredak();
            view.rokTrajanja = kampanja.getRokTrajanja();

            AdminEmailView primateljView = new AdminEmailView();
            if (kampanja.getPrimatelj() != null && kampanja.getPrimatelj().getKorisnik() != null) {
                primateljView.email = kampanja.getPrimatelj().getKorisnik().getEmail();
            }
            view.primatelj = primateljView;

            results.add(view);
        }
        return results;
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getStats() {
        long ukupnoKorisnika = korisnikRepository.count();
        long ukupnoZahtjeva = zahtjevRepository.count();

        long aktivnihDonacija = 0;
        List<Igracka> igracke = igrackaRepository.findAll();
        for (Igracka igracka : igracke) {
            if (igracka.getStatus() == StatusIgracke.DOSTUPNO) {
                aktivnihDonacija++;
            }
        }

        long aktivnihKampanja = 0;
        List<Kampanja> kampanje = kampanjaRepository.findAll();
        LocalDate today = LocalDate.now();
        for (Kampanja kampanja : kampanje) {
            if (kampanja.getRokTrajanja() != null && !kampanja.getRokTrajanja().isBefore(today)) {
                aktivnihKampanja++;
            }
        }

        return java.util.Map.of(
                "ukupnoKorisnika", ukupnoKorisnika,
                "aktivnihDonacija", aktivnihDonacija,
                "aktivnihKampanja", aktivnihKampanja,
                "ukupnoZahtjeva", ukupnoZahtjeva
        );
    }

    @Transactional
    public void deleteUserById(Long korisnikId) {
        if (korisnikId == null || korisnikId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Neispravan ID korisnika.");
        }
        if (!korisnikRepository.existsById(korisnikId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Korisnik ne postoji.");
        }

        // Prvo ukloni ovisne zapise (recenzije, zahtjeve).
        recenzijaRepository.deleteByPrimatelj_Id(korisnikId);
        recenzijaRepository.deleteByDonator_Id(korisnikId);
        zahtjevRepository.deleteByPrimatelj_Id(korisnikId);
        zahtjevRepository.deleteByDonator_Id(korisnikId);

        boolean isPrimatelj = primateljRepository.existsById(korisnikId);
        boolean isDonator = donatorRepository.existsById(korisnikId);

        if (isPrimatelj) {
            // Oslobodi rezervacije korisnika i vrati oglase u dostupno stanje.
            List<Igracka> rezervirane = igrackaRepository.findByPrimatelj_Id(korisnikId);
            if (!rezervirane.isEmpty()) {
                for (Igracka igracka : rezervirane) {
                    igracka.setPrimatelj(null);
                    igracka.setStatus(StatusIgracke.DOSTUPNO);
                }
                igrackaRepository.saveAll(rezervirane);
            }

            // Ukloni kampanje (s pripadajucim popisima igracaka).
            List<Kampanja> kampanje = kampanjaRepository.findByPrimatelj_Id(korisnikId);
            if (!kampanje.isEmpty()) {
                for (Kampanja kampanja : kampanje) {
                    if (kampanja.getId() != null) {
                        popisIgracakaRepository.deleteByKampanja_Id(kampanja.getId());
                    }
                }
                kampanjaRepository.deleteAll(kampanje);
            }

            primateljRepository.deleteById(korisnikId);
        }

        if (isDonator) {
            // Ukloni sve oglase donatora (ukljucujuci aktivne).
            igrackaRepository.deleteByDonator_Id(korisnikId);
            donatorRepository.deleteById(korisnikId);
        }

        if (adminRepository.existsById(korisnikId)) {
            adminRepository.deleteById(korisnikId);
        }

        korisnikRepository.deleteById(korisnikId);
    }

    @Transactional
    public void deleteDonationById(Long igrackaId) {
        if (igrackaId == null || igrackaId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Neispravan ID donacije.");
        }
        Igracka igracka = igrackaRepository.findById(igrackaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donacija ne postoji."));

        List<Zahtjev> zahtjevi = zahtjevRepository.findByIgracka_Id(igrackaId);
        if (!zahtjevi.isEmpty()) {
            List<Long> zahtjevIds = new ArrayList<>(zahtjevi.size());
            for (Zahtjev zahtjev : zahtjevi) {
                if (zahtjev.getId() != null) {
                    zahtjevIds.add(zahtjev.getId());
                }
            }
            if (!zahtjevIds.isEmpty()) {
                recenzijaRepository.deleteByZahtjev_IdIn(zahtjevIds);
            }
            zahtjevRepository.deleteAll(zahtjevi);
        }

        igrackaRepository.delete(igracka);
    }

    @Transactional
    public void deleteCampaignById(Long kampanjaId) {
        if (kampanjaId == null || kampanjaId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Neispravan ID kampanje.");
        }
        Kampanja kampanja = kampanjaRepository.findById(kampanjaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kampanja ne postoji."));
        popisIgracakaRepository.deleteByKampanja_Id(kampanjaId);
        kampanjaRepository.delete(kampanja);
    }

    private String resolveUloga(Korisnik korisnik, Set<Long> donatorIds, Set<Long> primateljIds) {
        if (adminService.isAdminEmail(korisnik.getEmail())) {
            return "ADMIN";
        }
        Long id = korisnik.getId();
        if (id != null && donatorIds.contains(id)) {
            return "DONATOR";
        }
        if (id != null && primateljIds.contains(id)) {
            return "PRIMATELJ";
        }
        return "KORISNIK";
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private int normalizeOffset(int offset) {
        return Math.max(offset, 0);
    }

    private <T> List<T> slice(List<T> items, int limit, int offset) {
        if (items == null || items.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        int start = Math.min(offset, items.size());
        int end = Math.min(start + limit, items.size());
        return items.subList(start, end);
    }
}
