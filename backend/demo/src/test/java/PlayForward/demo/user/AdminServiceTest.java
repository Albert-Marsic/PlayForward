package PlayForward.demo.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private AdminRepository adminRepository;
    private AdminService service;

    @BeforeEach
    void setup() {
        adminRepository = mock(AdminRepository.class);
        // set up with two admin emails
        service = new AdminService("admin1@example.com, admin2@example.com", adminRepository);
    }

    @Test
    void testIsAdminEmail() {
        assertTrue(service.isAdminEmail("admin1@example.com"));
        assertTrue(service.isAdminEmail("ADMIN2@EXAMPLE.COM")); // case insensitive
        assertFalse(service.isAdminEmail("notadmin@example.com"));
        assertFalse(service.isAdminEmail(null));
        assertFalse(service.isAdminEmail(""));
        assertFalse(service.isAdminEmail("   "));
    }

    @Test
    void testEnsureAdminForAlreadyExists() {
        Korisnik korisnik = new Korisnik();
        korisnik.setId(1L);
        korisnik.setEmail("admin1@example.com");

        when(adminRepository.existsById(1L)).thenReturn(true);

        boolean result = service.ensureAdminFor(korisnik);

        assertTrue(result);
        verify(adminRepository, never()).save(any());
    }

    @Test
    void testEnsureAdminForCreatesAdmin() {
        Korisnik korisnik = new Korisnik();
        korisnik.setId(2L);
        korisnik.setEmail("admin2@example.com");

        when(adminRepository.existsById(2L)).thenReturn(false);

        boolean result = service.ensureAdminFor(korisnik);

        assertTrue(result);
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void testEnsureAdminForNonAdminEmail() {
        Korisnik korisnik = new Korisnik();
        korisnik.setId(3L);
        korisnik.setEmail("user@example.com");

        boolean result = service.ensureAdminFor(korisnik);

        assertFalse(result);
        verify(adminRepository, never()).save(any());
    }

    @Test
    void testEnsureAdminForNullKorisnik() {
        boolean result = service.ensureAdminFor(null);
        assertFalse(result);
        verify(adminRepository, never()).save(any());
    }

    @Test
    void testEnsureAdminForKorisnikWithoutId() {
        Korisnik korisnik = new Korisnik();
        korisnik.setEmail("admin1@example.com");

        boolean result = service.ensureAdminFor(korisnik);

        assertFalse(result);
        verify(adminRepository, never()).save(any());
    }
}
