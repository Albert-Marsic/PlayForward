package PlayForward.demo.user;

import PlayForward.demo.campaign.Kampanja;
import PlayForward.demo.campaign.KampanjaRepository;
import PlayForward.demo.listing.Igracka;
import PlayForward.demo.listing.IgrackaRepository;
import PlayForward.demo.listing.StatusIgracke;
import PlayForward.demo.request.ZahtjevRepository;
import PlayForward.demo.review.RecenzijaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminUserService {

    private final KorisnikRepository korisnikRepository;
    private final AdminRepository adminRepository;
    private final DonatorRepository donatorRepository;
    private final PrimateljRepository primateljRepository;
    private final IgrackaRepository igrackaRepository;
    private final KampanjaRepository kampanjaRepository;
    private final ZahtjevRepository zahtjevRepository;
    private final RecenzijaRepository recenzijaRepository;

    public AdminUserService(KorisnikRepository korisnikRepository,
                            AdminRepository adminRepository,
                            DonatorRepository donatorRepository,
                            PrimateljRepository primateljRepository,
                            IgrackaRepository igrackaRepository,
                            KampanjaRepository kampanjaRepository,
                            ZahtjevRepository zahtjevRepository,
                            RecenzijaRepository recenzijaRepository) {
        this.korisnikRepository = korisnikRepository;
        this.adminRepository = adminRepository;
        this.donatorRepository = donatorRepository;
        this.primateljRepository = primateljRepository;
        this.igrackaRepository = igrackaRepository;
        this.kampanjaRepository = kampanjaRepository;
        this.zahtjevRepository = zahtjevRepository;
        this.recenzijaRepository = recenzijaRepository;
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
}
