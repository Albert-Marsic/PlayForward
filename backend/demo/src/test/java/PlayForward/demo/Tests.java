package PlayForward.demo;

import PlayForward.demo.listing.Igracka;
import PlayForward.demo.listing.IgrackaRepository;
import PlayForward.demo.listing.StatusIgracke;
import PlayForward.demo.mail.EmailService;
import PlayForward.demo.request.*;
import PlayForward.demo.review.RecenzijaService;
import PlayForward.demo.review.RecenzijaRepository;
import PlayForward.demo.review.dto.CreateRecenzijaRequest;
import PlayForward.demo.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Tests {

    private ZahtjevRepository zahtjevRepo;
    private KorisnikRepository korisnikRepo;
    private DonatorRepository donatorRepo;
    private PrimateljRepository primateljRepo;
    private IgrackaRepository igrackaRepo;
    private RecenzijaRepository recenzijaRepo;
    private AdminRepository adminRepo;
    private EmailService emailService;

    private ZahtjevService zahtjevService;
    private RecenzijaService recenzijaService;
    private AdminService adminService;

    @BeforeEach
    void setup() {
        // Create typed mocks
        zahtjevRepo = mock(ZahtjevRepository.class);
        korisnikRepo = mock(KorisnikRepository.class);
        donatorRepo = mock(DonatorRepository.class);
        primateljRepo = mock(PrimateljRepository.class);
        igrackaRepo = mock(IgrackaRepository.class);
        recenzijaRepo = mock(RecenzijaRepository.class);
        adminRepo = mock(AdminRepository.class);
        emailService = mock(EmailService.class);

        // Services
        adminService = new AdminService("admin@example.com", adminRepo);

        zahtjevService = new ZahtjevService(
                zahtjevRepo,
                igrackaRepo,
                korisnikRepo,
                donatorRepo,
                primateljRepo,
                emailService
        );

        recenzijaService = new RecenzijaService(
                recenzijaRepo,
                zahtjevRepo,
                korisnikRepo,
                donatorRepo,
                primateljRepo,
                emailService
        );
    }

    @Test
    void testAdminEmailAndEnsureAdmin() {
        // Admin email check
        assertTrue(adminService.isAdminEmail("admin@example.com"));
        assertFalse(adminService.isAdminEmail("user@example.com"));

        Korisnik korisnik = new Korisnik();
        korisnik.setId(1L);
        korisnik.setEmail("admin@example.com");

        when(adminRepo.existsById(1L)).thenReturn(false);

        boolean result = adminService.ensureAdminFor(korisnik);
        assertTrue(result);
        verify(adminRepo).save(any(Admin.class));
    }

    @Test
    void testWithdrawZahtjevChangesStatusAndResetsIgracka() {
        // Setup Primatelj and Zahtjev
        Primatelj primatelj = new Primatelj();
        primatelj.setId(1L);

        Igracka igracka = new Igracka();
        igracka.setStatus(StatusIgracke.REZERVIRANO);
        igracka.setPrimatelj(primatelj);

        Zahtjev zahtjev = new Zahtjev();
        zahtjev.setId(100L);
        zahtjev.setStatus(StatusZahtjeva.APPROVED);
        zahtjev.setPrimatelj(primatelj);
        zahtjev.setIgracka(igracka);

        when(zahtjevRepo.findById(100L)).thenReturn(Optional.of(zahtjev));
        when(zahtjevRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Zahtjev withdrawn = zahtjevService.withdraw(100L);

        assertEquals(StatusZahtjeva.WITHDRAWN, withdrawn.getStatus());
        assertNull(igracka.getPrimatelj());
        assertEquals(StatusIgracke.DOSTUPNO, igracka.getStatus());
    }

    @Test
    void testCreateRecenzijaThrowsExceptionForShortText() {
        CreateRecenzijaRequest req = new CreateRecenzijaRequest();
        req.zahtjevId = 1L;
        req.ocjena = 4;
        req.tekst = "Too short";

        Exception ex = assertThrows(RuntimeException.class, () -> recenzijaService.create(req));
        assertTrue(ex.getMessage().contains("najmanje 10 znakova"));
    }

    @Test
    void testRecenzijaCreateSuccessfulFlow() {
        // Setup Primatelj, Donator, Zahtjev
        Primatelj primatelj = new Primatelj();
        primatelj.setId(1L);

        Donator donator = new Donator();
        donator.setId(2L);
        donator.setKorisnik(new Korisnik());
        donator.getKorisnik().setEmail("donor@example.com");

        Zahtjev zahtjev = new Zahtjev();
        zahtjev.setId(10L);
        zahtjev.setStatus(StatusZahtjeva.PICKED_UP);
        zahtjev.setPrimatelj(primatelj);
        zahtjev.setDonator(donator);

        when(zahtjevRepo.findById(10L)).thenReturn(Optional.of(zahtjev));
        when(recenzijaRepo.existsByZahtjev_Id(10L)).thenReturn(false);
        when(recenzijaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateRecenzijaRequest req = new CreateRecenzijaRequest();
        req.zahtjevId = 10L;
        req.ocjena = 5;
        req.tekst = "Odlična igračka, jako sam zadovoljan.";

        var response = recenzijaService.create(req);

        assertEquals(5, response.ocjena);
        assertEquals("Odlična igračka, jako sam zadovoljan.", response.tekst);
    }

    @Test
    void testZahtjevGetForCurrentPrimateljThrowsOnInvalidId() {
        assertThrows(RuntimeException.class, () -> zahtjevService.getForCurrentPrimatelj(-5L));
    }

    @Test
    void testAdminEnsureAdminReturnsFalseForNonAdminEmail() {
        Korisnik user = new Korisnik();
        user.setId(99L);
        user.setEmail("user@example.com");
        assertFalse(adminService.ensureAdminFor(user));
        verify(adminRepo, never()).save(any());
    }
}
