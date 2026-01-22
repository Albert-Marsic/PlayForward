package PlayForward.demo.campaign;

import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.KorisnikRepository;
import PlayForward.demo.user.Primatelj;
import PlayForward.demo.user.PrimateljRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KampanjaService {

    private final KampanjaRepository kampanjaRepository;
    private final PrimateljRepository primateljRepository;
    private final KorisnikRepository korisnikRepository;

    public KampanjaService(KampanjaRepository kampanjaRepository,
            PrimateljRepository primateljRepository,
            KorisnikRepository korisnikRepository) {
        this.kampanjaRepository = kampanjaRepository;
        this.primateljRepository = primateljRepository;
        this.korisnikRepository = korisnikRepository;
    }

    @Transactional(readOnly = true)
    public List<Kampanja> getAllCampaigns() {
        return kampanjaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Kampanja getCampaignById(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID kampanje nije validan.");
        }
        return kampanjaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kampanja ne postoji."));
    }

    @Transactional
    public Kampanja createCampaign(Kampanja kampanja) {
        if (kampanja == null) {
            throw new RuntimeException("Kampanja ne može biti null.");
        }

        // Provjera korisnika - mora biti primatelj
        String email = SecurityUtil.currentEmailOrThrow();
        Long korisnikId = korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji."))
                .getId();

        Primatelj primatelj = primateljRepository.findById(korisnikId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije primatelj."));

        kampanja.setPrimatelj(primatelj);
        return kampanjaRepository.save(kampanja);
    }

    @Transactional
    public Kampanja updateCampaign(Long id, Kampanja updates) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID kampanje nije validan.");
        }
        if (updates == null) {
            throw new RuntimeException("Ažuriranja ne mogu biti null.");
        }

        Kampanja existing = getCampaignById(id);

        // Provjera autorizacije - samo kreirator može ažurirati
        String email = SecurityUtil.currentEmailOrThrow();
        Long korisnikId = korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji."))
                .getId();

        Primatelj primatelj = primateljRepository.findById(korisnikId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije primatelj."));

        if (!existing.getPrimatelj().getId().equals(primatelj.getId())) {
            throw new RuntimeException("Nemate pravo ažurirati ovu kampanju.");
        }

        if (updates.getNapredak() != null) {
            existing.setNapredak(updates.getNapredak());
        }
        if (updates.getRokTrajanja() != null) {
            existing.setRokTrajanja(updates.getRokTrajanja());
        }

        return kampanjaRepository.save(existing);
    }

    @Transactional
    public void deleteCampaign(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID kampanje nije validan.");
        }

        Kampanja kampanja = getCampaignById(id);

        // Provjera autorizacije - samo kreirator može obrisati
        String email = SecurityUtil.currentEmailOrThrow();
        Long korisnikId = korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji."))
                .getId();

        Primatelj primatelj = primateljRepository.findById(korisnikId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije primatelj."));

        if (!kampanja.getPrimatelj().getId().equals(primatelj.getId())) {
            throw new RuntimeException("Nemate pravo obrisati ovu kampanju.");
        }

        kampanjaRepository.delete(kampanja);
    }

    @Transactional(readOnly = true)
    public List<Kampanja> getCampaignsByCurrentUser() {
        String email = SecurityUtil.currentEmailOrThrow();
        Long korisnikId = korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji."))
                .getId();

        Primatelj primatelj = primateljRepository.findById(korisnikId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije primatelj."));

        return kampanjaRepository.findByPrimatelj_Id(primatelj.getId());
    }
}
