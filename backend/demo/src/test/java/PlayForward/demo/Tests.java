package PlayForward.demo;

import PlayForward.demo.campaign.*;
import PlayForward.demo.listing.IgrackaRepository;
import PlayForward.demo.mail.EmailService;
import PlayForward.demo.request.*;
import PlayForward.demo.review.RecenzijaService;
import PlayForward.demo.review.RecenzijaRepository;
import PlayForward.demo.review.dto.CreateRecenzijaRequest;
import PlayForward.demo.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    private KampanjaRepository kampanjaRepository;
    private PopisIgracakaRepository popisRepository;
    private KampanjaService kampanjaService;

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
        kampanjaRepository = mock(KampanjaRepository.class);
        popisRepository = mock(PopisIgracakaRepository.class);
        kampanjaService = mock(KampanjaService.class);

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
    void testZahtjevGetForCurrentPrimateljThrowsForInvalidId() {
        ZahtjevService service = new ZahtjevService(
                zahtjevRepo,
                igrackaRepo,
                korisnikRepo,
                donatorRepo,
                primateljRepo,
                emailService
        );

        ResponseStatusException ex1 = assertThrows(
                ResponseStatusException.class,
                () -> service.getForCurrentPrimatelj(0L)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatusCode());

        ResponseStatusException ex2 = assertThrows(
                ResponseStatusException.class,
                () -> service.getForCurrentPrimatelj(-1L)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatusCode());

        ResponseStatusException ex3 = assertThrows(
                ResponseStatusException.class,
                () -> service.getForCurrentPrimatelj(-100L)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex3.getStatusCode());
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
    void testRecenzijaCreateThrowsForInvalidRequest() {
        ResponseStatusException ex1 = assertThrows(
                ResponseStatusException.class,
                () -> recenzijaService.create(null)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatusCode());

        CreateRecenzijaRequest req2 = new CreateRecenzijaRequest();
        req2.zahtjevId = null;
        req2.ocjena = 3;
        req2.tekst = "Valid text for testing";

        ResponseStatusException ex2 = assertThrows(
                ResponseStatusException.class,
                () -> recenzijaService.create(req2)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatusCode());

        CreateRecenzijaRequest req3 = new CreateRecenzijaRequest();
        req3.zahtjevId = 1L;
        req3.ocjena = 0;
        req3.tekst = "Valid text for testing";

        ResponseStatusException ex3 = assertThrows(
                ResponseStatusException.class,
                () -> recenzijaService.create(req3)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex3.getStatusCode());

        CreateRecenzijaRequest req4 = new CreateRecenzijaRequest();
        req4.zahtjevId = 1L;
        req4.ocjena = 3;
        req4.tekst = "short";

        ResponseStatusException ex4 = assertThrows(
                ResponseStatusException.class,
                () -> recenzijaService.create(req4)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex4.getStatusCode());
    }

    @Test
    void testZahtjevGetForCurrentPrimateljThrowsOnInvalidId() {
        assertThrows(RuntimeException.class, () -> zahtjevService.getForCurrentPrimatelj(-5L));
    }

    @Test
    void testKampanjaGetByIdThrowsForInvalidId() {
        KampanjaService service = new KampanjaService(
                kampanjaRepository,
                popisRepository,
                korisnikRepo,
                primateljRepo,
                donatorRepo
        );

        ResponseStatusException ex1 = assertThrows(
                ResponseStatusException.class,
                () -> service.getById(0L)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatusCode());

        ResponseStatusException ex2 = assertThrows(
                ResponseStatusException.class,
                () -> service.getById(-10L)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatusCode());
    }

}
