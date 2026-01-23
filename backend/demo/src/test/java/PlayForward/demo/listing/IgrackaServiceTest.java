package PlayForward.demo.listing;

import PlayForward.demo.listing.dto.CreateIgrackaRequest;
import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IgrackaServiceTest {

    private IgrackaRepository igrackaRepo;
    private KorisnikRepository korisnikRepo;
    private DonatorRepository donatorRepo;
    private PrimateljRepository primateljRepo;

    private IgrackaService service;

    @BeforeEach
    void setUp() {
        igrackaRepo = mock(IgrackaRepository.class);
        korisnikRepo = mock(KorisnikRepository.class);
        donatorRepo = mock(DonatorRepository.class);
        primateljRepo = mock(PrimateljRepository.class);

        service = new IgrackaService(igrackaRepo, korisnikRepo, donatorRepo, primateljRepo);
    }

    @Test
    void create_valid_createsIgracka() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("donator@example.com");

            Korisnik k = new Korisnik(); k.setId(1L);
            Donator d = new Donator(); d.setId(1L);

            when(korisnikRepo.findByEmail("donator@example.com")).thenReturn(Optional.of(k));
            when(donatorRepo.findById(1L)).thenReturn(Optional.of(d));

            CreateIgrackaRequest req = new CreateIgrackaRequest();
            req.naziv = "Teddy Bear";
            req.kategorija = "Stuffed";
            req.stanje = StanjeIgracke.NOVO;
            req.fotografija = "teddy.jpg";

            when(igrackaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Igracka result = service.create(req);

            assertEquals("Teddy Bear", result.getNaziv());
            assertEquals("Stuffed", result.getKategorija());
            assertEquals(StanjeIgracke.NOVO, result.getStanje());
            assertEquals(StatusIgracke.DOSTUPNO, result.getStatus());
            assertEquals(d, result.getDonator());
            assertNull(result.getPrimatelj());
        }
    }

    @Test
    void updateUvjeti_valid_updatesUvjeti() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("donator@example.com");

            Korisnik k = new Korisnik(); k.setId(1L);
            Donator d = new Donator(); d.setId(1L);
            Igracka igracka = new Igracka();
            igracka.setId(10L);
            igracka.setDonator(d);
            igracka.setStatus(StatusIgracke.DOSTUPNO);

            when(korisnikRepo.findByEmail("donator@example.com")).thenReturn(Optional.of(k));
            when(donatorRepo.findById(1L)).thenReturn(Optional.of(d));
            when(igrackaRepo.findById(10L)).thenReturn(Optional.of(igracka));
            when(igrackaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Igracka updated = service.updateUvjeti(10L, "New conditions");
            assertEquals("New conditions", updated.getUvjeti());
        }
    }

    @Test
    void filter_returnsFiltered() {
        Igracka i1 = new Igracka();
        Igracka i2 = new Igracka();

        when(igrackaRepo.findByStatus(StatusIgracke.DOSTUPNO)).thenReturn(List.of(i1, i2));

        List<Igracka> results = service.filter(null, null);

        assertEquals(2, results.size());
    }

    @Test
    void rezerviraj_valid_reserves() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("primatelj@example.com");

            Korisnik k = new Korisnik(); k.setId(2L);
            Primatelj p = new Primatelj(); p.setId(2L);
            Donator d = new Donator(); d.setId(1L);

            Igracka i = new Igracka();
            i.setId(10L);
            i.setStatus(StatusIgracke.DOSTUPNO);
            i.setDonator(d);

            when(korisnikRepo.findByEmail("primatelj@example.com")).thenReturn(Optional.of(k));
            when(primateljRepo.findById(2L)).thenReturn(Optional.of(p));
            when(igrackaRepo.findById(10L)).thenReturn(Optional.of(i));
            when(igrackaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Igracka result = service.rezerviraj(10L);

            assertEquals(StatusIgracke.REZERVIRANO, result.getStatus());
            assertEquals(p, result.getPrimatelj());
        }
    }

    @Test
    void odustani_valid_setsAvailable() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("primatelj@example.com");

            Korisnik k = new Korisnik(); k.setId(2L);
            Primatelj p = new Primatelj(); p.setId(2L);
            Igracka i = new Igracka();
            i.setStatus(StatusIgracke.REZERVIRANO);
            i.setPrimatelj(p);

            when(korisnikRepo.findByEmail("primatelj@example.com")).thenReturn(Optional.of(k));
            when(primateljRepo.findById(2L)).thenReturn(Optional.of(p));
            when(igrackaRepo.findById(10L)).thenReturn(Optional.of(i));
            when(igrackaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Igracka result = service.odustani(10L);

            assertEquals(StatusIgracke.DOSTUPNO, result.getStatus());
            assertNull(result.getPrimatelj());
        }
    }

    @Test
    void povuci_valid_deletesIgracka() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("donator@example.com");

            Korisnik k = new Korisnik(); k.setId(1L);
            Donator d = new Donator(); d.setId(1L);
            Igracka i = new Igracka();
            i.setId(10L);
            i.setDonator(d);
            i.setStatus(StatusIgracke.DOSTUPNO);

            when(korisnikRepo.findByEmail("donator@example.com")).thenReturn(Optional.of(k));
            when(donatorRepo.findById(1L)).thenReturn(Optional.of(d));
            when(igrackaRepo.findById(10L)).thenReturn(Optional.of(i));

            service.povuci(10L);

            verify(igrackaRepo).delete(i);
        }
    }

    @Test
    void create_nullRequest_throwsException() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("donator@example.com");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.create(null));

            assertTrue(ex.getMessage().contains("Podaci"));
        }
    }

    @Test
    void create_userNotDonator_throws() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("user@example.com");

            Korisnik k = new Korisnik(); k.setId(5L);

            when(korisnikRepo.findByEmail("user@example.com"))
                    .thenReturn(Optional.of(k));
            when(donatorRepo.findById(5L))
                    .thenReturn(Optional.empty());

            CreateIgrackaRequest req = new CreateIgrackaRequest();
            req.naziv = "Toy";
            req.kategorija = "Kids";
            req.stanje = StanjeIgracke.NOVO;
            req.fotografija = "img.jpg";

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.create(req));

            assertTrue(ex.getMessage().toLowerCase().contains("donator"));
        }
    }

    @Test
    void updateUvjeti_wrongDonator_throws() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("donator@example.com");

            Korisnik k = new Korisnik(); k.setId(1L);

            Donator realDonator = new Donator(); realDonator.setId(99L);

            Igracka igracka = new Igracka();
            igracka.setId(10L);
            igracka.setDonator(realDonator);
            igracka.setStatus(StatusIgracke.DOSTUPNO);

            when(korisnikRepo.findByEmail(anyString()))
                    .thenReturn(Optional.of(k));
            when(igrackaRepo.findById(10L))
                    .thenReturn(Optional.of(igracka));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.updateUvjeti(10L, "hack"));

            assertTrue(ex.getMessage().toLowerCase().contains("nemate"));
        }
    }

    @Test
    void rezerviraj_alreadyReserved_throws() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("primatelj@example.com");

            Korisnik k = new Korisnik(); k.setId(2L);
            Primatelj p = new Primatelj(); p.setId(2L);

            Igracka i = new Igracka();
            i.setStatus(StatusIgracke.REZERVIRANO);

            when(korisnikRepo.findByEmail(anyString()))
                    .thenReturn(Optional.of(k));
            when(primateljRepo.findById(2L))
                    .thenReturn(Optional.of(p));
            when(igrackaRepo.findById(10L))
                    .thenReturn(Optional.of(i));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.rezerviraj(10L));

            assertTrue(ex.getMessage().toLowerCase().contains("nije dostupna"));
        }
    }

    @Test
    void odustani_wrongPrimatelj_throws() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("other@example.com");

            Korisnik k = new Korisnik(); k.setId(3L);
            Primatelj realPrimatelj = new Primatelj(); realPrimatelj.setId(2L);

            Igracka i = new Igracka();
            i.setStatus(StatusIgracke.REZERVIRANO);
            i.setPrimatelj(realPrimatelj);

            when(korisnikRepo.findByEmail(anyString()))
                    .thenReturn(Optional.of(k));
            when(igrackaRepo.findById(10L))
                    .thenReturn(Optional.of(i));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.odustani(10L));

            assertTrue(ex.getMessage().toLowerCase().contains("samo primatelj"));
        }
    }

    @Test
    void povuciOglas_delegatesToPovuci() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("donator@example.com");

            Korisnik k = new Korisnik(); k.setId(1L);
            Donator d = new Donator(); d.setId(1L);

            Igracka i = new Igracka();
            i.setId(10L);
            i.setDonator(d);
            i.setStatus(StatusIgracke.DOSTUPNO);

            when(korisnikRepo.findByEmail(anyString()))
                    .thenReturn(Optional.of(k));
            when(donatorRepo.findById(1L))
                    .thenReturn(Optional.of(d));
            when(igrackaRepo.findById(10L))
                    .thenReturn(Optional.of(i));

            service.povuciOglas(10L);

            verify(igrackaRepo).delete(i);
        }
    }

}
