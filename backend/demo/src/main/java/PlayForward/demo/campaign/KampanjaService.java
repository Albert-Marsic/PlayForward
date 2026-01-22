package PlayForward.demo.campaign;

import PlayForward.demo.campaign.dto.CreateKampanjaRequest;
import PlayForward.demo.campaign.dto.KampanjaPrimateljView;
import PlayForward.demo.campaign.dto.KampanjaView;
import PlayForward.demo.campaign.dto.PopisIgracakaRequest;
import PlayForward.demo.campaign.dto.PopisIgracakaView;
import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.DonatorRepository;
import PlayForward.demo.user.KorisnikRepository;
import PlayForward.demo.user.Primatelj;
import PlayForward.demo.user.PrimateljRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class KampanjaService {

    private final KampanjaRepository kampanjaRepository;
    private final PopisIgracakaRepository popisRepository;
    private final KorisnikRepository korisnikRepository;
    private final PrimateljRepository primateljRepository;
    private final DonatorRepository donatorRepository;

    public KampanjaService(KampanjaRepository kampanjaRepository,
                           PopisIgracakaRepository popisRepository,
                           KorisnikRepository korisnikRepository,
                           PrimateljRepository primateljRepository,
                           DonatorRepository donatorRepository) {
        this.kampanjaRepository = kampanjaRepository;
        this.popisRepository = popisRepository;
        this.korisnikRepository = korisnikRepository;
        this.primateljRepository = primateljRepository;
        this.donatorRepository = donatorRepository;
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
                .orElseThrow(() -> new RuntimeException("Nemate PRIMATELJ ulogu."));
    }

    private Donator currentDonatorOrThrow() {
        Long id = currentKorisnikId();
        return donatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nemate DONATOR ulogu."));
    }

    private Kampanja requireCampaign(Long kampanjaId) {
        if (kampanjaId == null || kampanjaId <= 0) {
            throw new RuntimeException("ID kampanje nije validan.");
        }
        return kampanjaRepository.findById(kampanjaId)
                .orElseThrow(() -> new RuntimeException("Kampanja ne postoji."));
    }

    private boolean isActive(Kampanja kampanja) {
        LocalDate rok = kampanja.getRokTrajanja();
        if (rok == null) {
            return false;
        }
        return !rok.isBefore(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<KampanjaView> listAll() {
        List<Kampanja> kampanje = kampanjaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<KampanjaView> results = new ArrayList<>(kampanje.size());
        for (Kampanja kampanja : kampanje) {
            results.add(toView(kampanja));
        }
        return results;
    }

    @Transactional(readOnly = true)
    public KampanjaView getById(Long kampanjaId) {
        Kampanja kampanja = requireCampaign(kampanjaId);
        return toView(kampanja);
    }

    @Transactional
    public KampanjaView create(CreateKampanjaRequest req) {
        if (req == null) {
            throw new RuntimeException("Podaci nedostaju.");
        }

        String naziv = req.naziv == null ? "" : req.naziv.trim();
        String opis = req.opis == null ? "" : req.opis.trim();

        if (naziv.isBlank()) {
            throw new RuntimeException("Naziv je obavezan.");
        }
        if (opis.isBlank()) {
            throw new RuntimeException("Opis je obavezan.");
        }
        if (req.rokTrajanja == null) {
            throw new RuntimeException("Rok trajanja je obavezan.");
        }
        if (req.rokTrajanja.isBefore(LocalDate.now())) {
            throw new RuntimeException("Rok trajanja ne može biti u prošlosti.");
        }

        Primatelj primatelj = currentPrimateljOrThrow();

        Kampanja kampanja = new Kampanja();
        kampanja.setNaziv(naziv);
        kampanja.setOpis(opis);
        kampanja.setRokTrajanja(req.rokTrajanja);
        kampanja.setNapredak(naziv);
        kampanja.setPrimatelj(primatelj);

        Kampanja saved = kampanjaRepository.save(kampanja);
        return toView(saved, List.of());
    }

    @Transactional(readOnly = true)
    public List<PopisIgracakaView> listPopis(Long kampanjaId) {
        Kampanja kampanja = requireCampaign(kampanjaId);
        List<PopisIgracaka> popisi = popisRepository.findByKampanja_Id(kampanja.getId());
        return toPopisViews(popisi);
    }

    @Transactional
    public List<PopisIgracakaView> savePopis(Long kampanjaId, List<PopisIgracakaRequest> items) {
        Kampanja kampanja = requireCampaign(kampanjaId);
        Primatelj primatelj = currentPrimateljOrThrow();

        if (kampanja.getPrimatelj() == null || kampanja.getPrimatelj().getId() == null
                || !kampanja.getPrimatelj().getId().equals(primatelj.getId())) {
            throw new RuntimeException("Nemate pravo uređivati ovu kampanju.");
        }

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Popis igračaka je obavezan.");
        }

        List<PopisIgracaka> toSave = new ArrayList<>(items.size());
        Set<String> seenNames = new HashSet<>();

        for (PopisIgracakaRequest item : items) {
            if (item == null) {
                continue;
            }
            String naziv = item.nazivIgracke == null ? "" : item.nazivIgracke.trim();
            if (naziv.isBlank()) {
                throw new RuntimeException("Naziv igračke je obavezan.");
            }
            if (item.kolicina == null || item.kolicina <= 0) {
                throw new RuntimeException("Količina mora biti veća od 0.");
            }
            String key = naziv.toLowerCase();
            if (!seenNames.add(key)) {
                throw new RuntimeException("Duplicirana igračka: " + naziv);
            }

            PopisIgracaka popis = new PopisIgracaka();
            PopisIgracakaId id = new PopisIgracakaId(naziv, kampanja.getId());
            popis.setId(id);
            popis.setKampanja(kampanja);
            popis.setKolicina(item.kolicina);
            popis.setStatus(StatusPopisa.POTREBNO);
            toSave.add(popis);
        }

        if (toSave.isEmpty()) {
            throw new RuntimeException("Popis igračaka je obavezan.");
        }

        popisRepository.deleteByKampanja_Id(kampanjaId);
        popisRepository.saveAll(toSave);

        return toPopisViews(toSave);
    }

    @Transactional
    public PopisIgracakaView markDonated(Long kampanjaId, String nazivIgracke) {
        currentDonatorOrThrow();
        Kampanja kampanja = requireCampaign(kampanjaId);
        if (!isActive(kampanja)) {
            throw new RuntimeException("Kampanja je završena.");
        }

        String naziv = nazivIgracke == null ? "" : nazivIgracke.trim();
        if (naziv.isBlank()) {
            throw new RuntimeException("Naziv igračke je obavezan.");
        }

        PopisIgracakaId id = new PopisIgracakaId(naziv, kampanja.getId());
        PopisIgracaka popis = popisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Igračka nije pronađena u kampanji."));

        if (popis.getStatus() != StatusPopisa.DONIRANO) {
            popis.setStatus(StatusPopisa.DONIRANO);
            popis = popisRepository.save(popis);
        }

        return toPopisView(popis);
    }

    private KampanjaView toView(Kampanja kampanja) {
        List<PopisIgracaka> popisi = popisRepository.findByKampanja_Id(kampanja.getId());
        return toView(kampanja, popisi);
    }

    private KampanjaView toView(Kampanja kampanja, List<PopisIgracaka> popisi) {
        KampanjaView view = new KampanjaView();
        view.id = kampanja.getId();
        view.naziv = kampanja.getNaziv();
        view.opis = kampanja.getOpis();
        view.napredak = kampanja.getNapredak();
        view.rokTrajanja = kampanja.getRokTrajanja();
        view.status = isActive(kampanja) ? "AKTIVNA" : "ZAVRSENA";
        view.postotak = calculateCompletionPercentage(popisi);
        view.popisi = toPopisViews(popisi);

        if (kampanja.getPrimatelj() != null && kampanja.getPrimatelj().getKorisnik() != null) {
            KampanjaPrimateljView primateljView = new KampanjaPrimateljView();
            primateljView.email = kampanja.getPrimatelj().getKorisnik().getEmail();
            view.primatelj = primateljView;
        }

        return view;
    }

    private List<PopisIgracakaView> toPopisViews(List<PopisIgracaka> popisi) {
        if (popisi == null || popisi.isEmpty()) {
            return List.of();
        }
        List<PopisIgracakaView> results = new ArrayList<>(popisi.size());
        for (PopisIgracaka popis : popisi) {
            results.add(toPopisView(popis));
        }
        return results;
    }

    private PopisIgracakaView toPopisView(PopisIgracaka popis) {
        PopisIgracakaView view = new PopisIgracakaView();
        if (popis.getId() != null) {
            view.nazivIgracke = popis.getId().getNazivIgracke();
        }
        view.kolicina = popis.getKolicina();
        view.status = popis.getStatus();
        return view;
    }

    private Integer calculateCompletionPercentage(List<PopisIgracaka> popisi) {
        if (popisi == null || popisi.isEmpty()) {
            return 0;
        }
        int total = 0;
        int donated = 0;
        for (PopisIgracaka popis : popisi) {
            if (popis.getKolicina() != null) {
                total += popis.getKolicina();
                if (popis.getStatus() == StatusPopisa.DONIRANO) {
                    donated += popis.getKolicina();
                }
            }
        }
        if (total <= 0) {
            return 0;
        }
        return Math.round((donated * 100f) / total);
    }
}
