package PlayForward.demo.review;

import PlayForward.demo.mail.EmailService;
import PlayForward.demo.request.StatusZahtjeva;
import PlayForward.demo.request.Zahtjev;
import PlayForward.demo.request.ZahtjevRepository;
import PlayForward.demo.review.dto.CreateRecenzijaRequest;
import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RecenzijaServiceTest {

    private RecenzijaRepository recenzijaRepo;
    private ZahtjevRepository zahtjevRepo;
    private EmailService emailService;
    private RecenzijaService service;
    private KorisnikRepository korisnikRepo;
    private Donator donatorRepo;
    private PrimateljRepository primateljRepo;

    @BeforeEach
    void setup() {
        recenzijaRepo = mock(RecenzijaRepository.class);
        zahtjevRepo = mock(ZahtjevRepository.class);
        emailService = mock(EmailService.class);
        korisnikRepo = mock(KorisnikRepository.class);
        donatorRepo = mock(DonatorRepository.class);
        primateljRepo = mock(PrimateljRepository.class);

        service = new RecenzijaService(
                recenzijaRepo,
                zahtjevRepo,
                korisnikRepo,
                donatorRepo,
                primateljRepo,
                emailService
        );
    }

    @Test
    void testCreateReviewSuccessfully() {
        try (MockedStatic<SecurityUtil> security = Mockito.mockStatic(SecurityUtil.class)) {
            security.when(SecurityUtil::currentEmailOrThrow).thenReturn("user@example.com");

            Primatelj primatelj = new Primatelj();
            primatelj.setId(1L);

            Donator donator = new Donator();
            donator.setId(2L);

            Zahtjev zahtjev = new Zahtjev();
            zahtjev.setId(100L);
            zahtjev.setPrimatelj(primatelj);
            zahtjev.setDonator(donator);
            zahtjev.setStatus(StatusZahtjeva.COMPLETED);

            when(korisnikRepo.findByEmail("user@example.com")).thenReturn(Optional.of(primatelj.getKorisnik()));
            when(primateljRepo.findById(1L)).thenReturn(Optional.of(primatelj));
            when(zahtjevRepo.findById(100L)).thenReturn(Optional.of(zahtjev));
            when(recenzijaRepo.existsByZahtjev_Id(100L)).thenReturn(false);
            when(recenzijaRepo.save(any(Recenzija.class))).thenAnswer(inv -> inv.getArgument(0));

            CreateRecenzijaRequest req = new CreateRecenzijaRequest();
            req.zahtjevId = 100L;
            req.ocjena = 5;
            req.tekst = "Ovo je dobra igračka!";

            Recenzija result = service.create(req);

            assertEquals(5, result.getOcjena());
            assertEquals("Ovo je dobra igračka!", result.getTekst());
            verify(recenzijaRepo).save(any(Recenzija.class));
            verify(emailService).sendReviewNotificationAsync(anyString(), any(Recenzija.class));
        }
    }

    @Test
    void testCreateReviewThrowsOnInvalidRating() {
        CreateRecenzijaRequest req = new CreateRecenzijaRequest();
        req.zahtjevId = 1L;
        req.ocjena = 0;
        req.tekst = "Valid text here";

        Exception ex = assertThrows(RuntimeException.class, () -> service.create(req));
        assertTrue(ex.getMessage().contains("Ocjena mora biti između 1 i 5"));
    }

    @Test
    void testListForCurrentDonator() {
        try (MockedStatic<SecurityUtil> security = Mockito.mockStatic(SecurityUtil.class)) {
            security.when(SecurityUtil::currentEmailOrThrow).thenReturn("donor@example.com");
            Donator donor = new Donator();
            donor.setId(1L);

            when(korisnikRepo.findByEmail("donor@example.com")).thenReturn(Optional.of(donor.getKorisnik()));
            when(donatorRepo.findById(1L)).thenReturn(Optional.of(donor));
            when(recenzijaRepo.findByDonator_IdOrderByIdDesc(1L)).thenReturn(List.of());

            List<Recenzija> list = service.listForCurrentDonator();
            assertNotNull(list);
            verify(recenzijaRepo).findByDonator_IdOrderByIdDesc(1L);
        }
    }

    @Test
    void testListForDonatorUnauthorized() {
        try (MockedStatic<SecurityUtil> security = Mockito.mockStatic(SecurityUtil.class)) {
            security.when(SecurityUtil::currentEmailOrThrow).thenReturn("donor@example.com");
            Donator donor = new Donator();
            donor.setId(1L);

            when(korisnikRepo.findByEmail("donor@example.com")).thenReturn(Optional.of(donor.getKorisnik()));
            when(donatorRepo.findById(1L)).thenReturn(Optional.of(donor));

            Exception ex = assertThrows(RuntimeException.class, () -> service.listForDonator(2L));
            assertTrue(ex.getMessage().contains("Nemate pravo pregledavati tuđe recenzije"));
        }
    }

    @Test
    void testCreateReviewThrowsOnShortText() {
        CreateRecenzijaRequest req = new CreateRecenzijaRequest();
        req.zahtjevId = 1L;
        req.ocjena = 5;
        req.tekst = "Too short";

        Exception ex = assertThrows(RuntimeException.class, () -> service.create(req));
        assertTrue(ex.getMessage().contains("Recenzija mora imati najmanje 10 znakova"));
    }

    @Test
    void testCreateReviewThrowsWhenZahtjevNotPickedUpOrCompleted() {
        try (MockedStatic<SecurityUtil> security = Mockito.mockStatic(SecurityUtil.class)) {
            security.when(SecurityUtil::currentEmailOrThrow).thenReturn("user@example.com");

            Primatelj prim = new Primatelj();
            prim.setId(1L);

            Zahtjev zahtjev = new Zahtjev();
            zahtjev.setId(100L);
            zahtjev.setPrimatelj(prim);
            zahtjev.setStatus(StatusZahtjeva.PENDING); // not PICKED_UP or COMPLETED

            when(korisnikRepo.findByEmail("user@example.com")).thenReturn(Optional.of(prim.getKorisnik()));
            when(primateljRepo.findById(1L)).thenReturn(Optional.of(prim));
            when(zahtjevRepo.findById(100L)).thenReturn(Optional.of(zahtjev));

            CreateRecenzijaRequest req = new CreateRecenzijaRequest();
            req.zahtjevId = 100L;
            req.ocjena = 5;
            req.tekst = "Valid review text";

            Exception ex = assertThrows(RuntimeException.class, () -> service.create(req));
            assertTrue(ex.getMessage().contains("Recenzija je moguća tek nakon preuzimanja"));
        }
    }

    @Test
    void testCreateReviewThrowsIfRecenzijaAlreadyExists() {
        try (MockedStatic<SecurityUtil> security = Mockito.mockStatic(SecurityUtil.class)) {
            security.when(SecurityUtil::currentEmailOrThrow).thenReturn("user@example.com");

            Primatelj prim = new Primatelj();
            prim.setId(1L);

            Zahtjev zahtjev = new Zahtjev();
            zahtjev.setId(100L);
            zahtjev.setPrimatelj(prim);
            zahtjev.setStatus(StatusZahtjeva.COMPLETED);

            when(korisnikRepo.findByEmail("user@example.com")).thenReturn(Optional.of(prim.getKorisnik()));
            when(primateljRepo.findById(1L)).thenReturn(Optional.of(prim));
            when(zahtjevRepo.findById(100L)).thenReturn(Optional.of(zahtjev));
            when(recenzijaRepo.existsByZahtjev_Id(100L)).thenReturn(true);

            CreateRecenzijaRequest req = new CreateRecenzijaRequest();
            req.zahtjevId = 100L;
            req.ocjena = 5;
            req.tekst = "Valid review text";

            Exception ex = assertThrows(RuntimeException.class, () -> service.create(req));
            assertTrue(ex.getMessage().contains("Recenzija za ovaj zahtjev već postoji"));
        }
    }

    @Test
    void testListForCurrentPrimateljReturnsEmptyList() {
        try (MockedStatic<SecurityUtil> security = Mockito.mockStatic(SecurityUtil.class)) {
            security.when(SecurityUtil::currentEmailOrThrow).thenReturn("user@example.com");

            Primatelj prim = new Primatelj();
            prim.setId(1L);

            when(korisnikRepo.findByEmail("user@example.com")).thenReturn(Optional.of(prim.getKorisnik()));
            when(primateljRepo.findById(1L)).thenReturn(Optional.of(prim));
            when(recenzijaRepo.findByPrimatelj_IdOrderByIdDesc(1L)).thenReturn(List.of());

            List<Recenzija> list = service.listForCurrentPrimatelj();
            assertNotNull(list);
            assertTrue(list.isEmpty());
        }
    }

}
