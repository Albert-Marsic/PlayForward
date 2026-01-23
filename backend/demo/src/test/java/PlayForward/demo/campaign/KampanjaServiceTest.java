package PlayForward.demo.campaign;

import PlayForward.demo.campaign.dto.CreateKampanjaRequest;
import PlayForward.demo.campaign.dto.PopisIgracakaRequest;
import PlayForward.demo.campaign.dto.PopisIgracakaView;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.DonatorRepository;
import PlayForward.demo.user.Korisnik;
import PlayForward.demo.user.KorisnikRepository;
import PlayForward.demo.user.Primatelj;
import PlayForward.demo.user.PrimateljRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KampanjaServiceTest {

    private KampanjaRepository kampanjaRepo;
    private PopisIgracakaRepository popisRepo;
    private KorisnikRepository korisnikRepo;
    private PrimateljRepository primateljRepo;
    private DonatorRepository donatorRepo;

    private KampanjaService service;

    @BeforeEach
    void setup() {
        kampanjaRepo = mock(KampanjaRepository.class);
        popisRepo = mock(PopisIgracakaRepository.class);
        korisnikRepo = mock(KorisnikRepository.class);
        primateljRepo = mock(PrimateljRepository.class);
        donatorRepo = mock(DonatorRepository.class);

        service = new KampanjaService(kampanjaRepo, popisRepo, korisnikRepo, primateljRepo, donatorRepo);
    }

    @Test
    void testListAll_empty() {
        when(kampanjaRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(Collections.emptyList());
        List<?> kampanje = service.listAll();
        assertNotNull(kampanje);
        assertEquals(0, kampanje.size());
    }

    @Test
    void testGetById_notFound() {
        when(kampanjaRepo.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.getById(1L));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testGetById_invalidId() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.getById(0L));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testCreate_nullRequest() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(null));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testCreate_missingNaziv() {
        CreateKampanjaRequest req = new CreateKampanjaRequest();
        req.naziv = "  ";
        req.opis = "Opis";
        req.rokTrajanja = LocalDate.now().plusDays(1);

        mockCurrentPrimatelj();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(req));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testCreate_missingOpis() {
        CreateKampanjaRequest req = new CreateKampanjaRequest();
        req.naziv = "Naziv";
        req.opis = " ";
        req.rokTrajanja = LocalDate.now().plusDays(1);

        mockCurrentPrimatelj();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(req));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testCreate_pastRok() {
        CreateKampanjaRequest req = new CreateKampanjaRequest();
        req.naziv = "Naziv";
        req.opis = "Opis";
        req.rokTrajanja = LocalDate.now().minusDays(1);

        mockCurrentPrimatelj();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(req));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testListPopis_empty() {
        Kampanja kampanja = mock(Kampanja.class);
        when(kampanja.getId()).thenReturn(1L);
        when(kampanjaRepo.findById(1L)).thenReturn(Optional.of(kampanja));
        when(popisRepo.findByKampanja_Id(1L)).thenReturn(Collections.emptyList());

        List<PopisIgracakaView> popis = service.listPopis(1L);
        assertNotNull(popis);
        assertEquals(0, popis.size());
    }

    @Test
    void testListPopis_campaignNotFound() {
        when(kampanjaRepo.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.listPopis(1L));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testSavePopis_emptyList() {
        Kampanja kampanja = mock(Kampanja.class);
        Primatelj primatelj = mockCurrentPrimatelj();
        when(kampanja.getPrimatelj()).thenReturn(primatelj);
        when(kampanja.getId()).thenReturn(1L);
        when(kampanjaRepo.findById(1L)).thenReturn(Optional.of(kampanja));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.savePopis(1L, Collections.emptyList()));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testSavePopis_unauthorizedPrimatelj() {
        Kampanja kampanja = mock(Kampanja.class);
        Primatelj primatelj = mock(Primatelj.class);
        when(primatelj.getId()).thenReturn(2L); // different than current
        when(kampanja.getPrimatelj()).thenReturn(primatelj);
        when(kampanja.getId()).thenReturn(1L);
        when(kampanjaRepo.findById(1L)).thenReturn(Optional.of(kampanja));
        mockCurrentPrimatelj(1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.savePopis(1L, List.of(new PopisIgracakaRequest())));
        assertEquals(403, ex.getStatusCode().value());
    }

    @Test
    void testMarkDonated_campaignInactive() {
        Kampanja kampanja = mock(Kampanja.class);
        when(kampanja.getId()).thenReturn(1L);
        when(kampanja.getRokTrajanja()).thenReturn(LocalDate.now().minusDays(1));
        when(kampanjaRepo.findById(1L)).thenReturn(Optional.of(kampanja));
        mockCurrentDonator();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.markDonated(1L, "Igracka", 1));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testMarkDonated_invalidKolicina() {
        Kampanja kampanja = mock(Kampanja.class);
        when(kampanja.getId()).thenReturn(1L);
        when(kampanja.getRokTrajanja()).thenReturn(LocalDate.now().plusDays(1));
        when(kampanjaRepo.findById(1L)).thenReturn(Optional.of(kampanja));
        mockCurrentDonator();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.markDonated(1L, "Igracka", 0));
        assertEquals(400, ex.getStatusCode().value());
    }

    private Primatelj mockCurrentPrimatelj() {
        return mockCurrentPrimatelj(1L);
    }

    private Primatelj mockCurrentPrimatelj(Long id) {
        Primatelj primatelj = mock(Primatelj.class);
        Korisnik korisnik = mock(Korisnik.class);
        when(primatelj.getId()).thenReturn(id);
        when(korisnik.getId()).thenReturn(id);
        when(korisnik.getEmail()).thenReturn("test@example.com");
        when(primatelj.getKorisnik()).thenReturn(korisnik);
        when(korisnikRepo.findByEmail(anyString())).thenReturn(Optional.of(korisnik));
        when(primateljRepo.findById(id)).thenReturn(Optional.of(primatelj));
        return primatelj;
    }

    private void mockCurrentDonator() {
        Donator donator = mock(Donator.class);
        Korisnik korisnik = mock(Korisnik.class);
        when(korisnik.getId()).thenReturn(1L);
        when(korisnik.getEmail()).thenReturn("donator@example.com");
        when(korisnikRepo.findByEmail(anyString())).thenReturn(Optional.of(korisnik));
        when(donator.getId()).thenReturn(1L);
        when(donatorRepo.findById(1L)).thenReturn(Optional.of(donator));
    }
}
