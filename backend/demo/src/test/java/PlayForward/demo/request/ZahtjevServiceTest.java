package PlayForward.demo.request;

import PlayForward.demo.listing.Igracka;
import PlayForward.demo.listing.IgrackaRepository;
import PlayForward.demo.listing.StatusIgracke;
import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.*;
import PlayForward.demo.request.dto.CreateZahtjevRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ZahtjevServiceTest {

    private ZahtjevRepository zahtjevRepo;
    private IgrackaRepository igrackaRepo;
    private KorisnikRepository korisnikRepo;
    private DonatorRepository donatorRepo;
    private PrimateljRepository primateljRepo;

    private ZahtjevService service;

    @BeforeEach
    void setUp() {
        zahtjevRepo = mock(ZahtjevRepository.class);
        igrackaRepo = mock(IgrackaRepository.class);
        korisnikRepo = mock(KorisnikRepository.class);
        donatorRepo = mock(DonatorRepository.class);
        primateljRepo = mock(PrimateljRepository.class);

        service = new ZahtjevService(
                zahtjevRepo,
                igrackaRepo,
                korisnikRepo,
                donatorRepo,
                primateljRepo
        );
    }

    @Test
    void create_validRequest_createsZahtjev() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("primatelj@example.com");

            Korisnik korisnik = new Korisnik(); korisnik.setId(1L);
            Primatelj primatelj = new Primatelj(); primatelj.setId(1L);

            Donator donator = new Donator(); donator.setId(2L);

            Igracka igracka = new Igracka();
            igracka.setId(10L);
            igracka.setStatus(StatusIgracke.DOSTUPNO);
            igracka.setDonator(donator);

            CreateZahtjevRequest req = new CreateZahtjevRequest();
            req.igrackaId = 10L;
            req.napomena = "Please send carefully";

            when(korisnikRepo.findByEmail("primatelj@example.com")).thenReturn(Optional.of(korisnik));
            when(primateljRepo.findById(1L)).thenReturn(Optional.of(primatelj));
            when(igrackaRepo.findById(10L)).thenReturn(Optional.of(igracka));
            when(zahtjevRepo.existsByIgracka_IdAndStatusIn(anyLong(), anySet())).thenReturn(false);
            when(zahtjevRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Zahtjev result = service.create(req);

            assertNotNull(result);
            assertEquals(StatusZahtjeva.PENDING, result.getStatus());
            assertEquals(igracka, result.getIgracka());
            assertEquals(primatelj, result.getPrimatelj());
            assertEquals(donator, result.getDonator());
            assertEquals("Please send carefully", result.getNapomena());
        }
    }

    @Test
    void create_nullRequest_throws() {
        Exception ex = assertThrows(RuntimeException.class, () -> service.create(null));
        assertEquals("ID igračke je obavezan.", ex.getMessage());
    }

    @Test
    void listForCurrentPrimatelj_returnsList() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("primatelj@example.com");

            Korisnik k = new Korisnik(); k.setId(1L);
            Primatelj p = new Primatelj(); p.setId(1L);

            when(korisnikRepo.findByEmail("primatelj@example.com")).thenReturn(Optional.of(k));
            when(primateljRepo.findById(1L)).thenReturn(Optional.of(p));

            Zahtjev z1 = new Zahtjev();
            Zahtjev z2 = new Zahtjev();

            when(zahtjevRepo.findByPrimatelj_IdOrderByDatumZahtjevaDesc(1L))
                    .thenReturn(List.of(z1, z2));

            List<Zahtjev> results = service.listForCurrentPrimatelj();
            assertEquals(2, results.size());
        }
    }

    @Test
    void withdraw_valid_setsWithdrawn() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("primatelj@example.com");

            Korisnik k = new Korisnik(); k.setId(1L);
            Primatelj p = new Primatelj(); p.setId(1L);

            when(korisnikRepo.findByEmail("primatelj@example.com")).thenReturn(Optional.of(k));
            when(primateljRepo.findById(1L)).thenReturn(Optional.of(p));

            Zahtjev z = new Zahtjev();
            z.setId(100L);
            z.setPrimatelj(p);
            z.setStatus(StatusZahtjeva.PENDING);

            Igracka igracka = new Igracka();
            igracka.setStatus(StatusIgracke.REZERVIRANO);
            igracka.setPrimatelj(p);
            z.setIgracka(igracka);

            when(zahtjevRepo.findById(100L)).thenReturn(Optional.of(z));
            when(zahtjevRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(igrackaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Zahtjev result = service.withdraw(100L);

            assertEquals(StatusZahtjeva.WITHDRAWN, result.getStatus());
            assertEquals(StatusIgracke.DOSTUPNO, result.getIgracka().getStatus());
            assertNull(result.getIgracka().getPrimatelj());
        }
    }

    @Test
    void markCompleted_valid_setsCompleted() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("primatelj@example.com");

            Korisnik k = new Korisnik(); k.setId(1L);
            Primatelj p = new Primatelj(); p.setId(1L);

            when(korisnikRepo.findByEmail("primatelj@example.com")).thenReturn(Optional.of(k));
            when(primateljRepo.findById(1L)).thenReturn(Optional.of(p));

            Zahtjev z = new Zahtjev();
            z.setPrimatelj(p);
            z.setStatus(StatusZahtjeva.APPROVED);

            when(zahtjevRepo.findById(200L)).thenReturn(Optional.of(z));
            when(zahtjevRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Zahtjev result = service.markCompleted(200L);
            assertEquals(StatusZahtjeva.COMPLETED, result.getStatus());
        }
    }

    @Test
    void approveForCurrentDonator_valid_setsApproved() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::currentEmailOrThrow)
                    .thenReturn("donator@example.com");

            Korisnik k = new Korisnik(); k.setId(2L);
            Donator d = new Donator(); d.setId(2L);
            Primatelj p = new Primatelj(); p.setId(1L);

            when(korisnikRepo.findByEmail("donator@example.com")).thenReturn(Optional.of(k));
            when(donatorRepo.findById(2L)).thenReturn(Optional.of(d));

            Igracka igracka = new Igracka();
            igracka.setId(10L);
            igracka.setStatus(StatusIgracke.DOSTUPNO);
            igracka.setDonator(d);

            Zahtjev z = new Zahtjev();
            z.setId(300L);
            z.setDonator(d);
            z.setPrimatelj(p);
            z.setIgracka(igracka);
            z.setStatus(StatusZahtjeva.PENDING);

            when(zahtjevRepo.findById(300L)).thenReturn(Optional.of(z));
            when(zahtjevRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(igrackaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Zahtjev result = service.approveForCurrentDonator(300L);

            assertEquals(StatusZahtjeva.APPROVED, result.getStatus());
            assertEquals(StatusIgracke.REZERVIRANO, result.getIgracka().getStatus());
            assertEquals(p, result.getIgracka().getPrimatelj());
        }
    }
}
