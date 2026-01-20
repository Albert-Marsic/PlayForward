package PlayForward.demo.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final Set<String> adminEmails;
    private final AdminRepository adminRepository;

    public AdminService(@Value("${app.admin.emails:}") String adminEmails,
                        AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
        this.adminEmails = Arrays.stream(adminEmails.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isAdminEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return adminEmails.contains(email.trim().toLowerCase(Locale.ROOT));
    }

    @Transactional
    public boolean ensureAdminFor(Korisnik korisnik) {
        if (korisnik == null) {
            return false;
        }
        if (!isAdminEmail(korisnik.getEmail())) {
            return false;
        }
        Long id = korisnik.getId();
        if (id == null) {
            return false;
        }
        if (!adminRepository.existsById(id)) {
            Admin admin = new Admin();
            admin.setKorisnik(korisnik);
            adminRepository.save(admin);
        }
        return true;
    }
}
