package PlayForward.demo;

import PlayForward.demo.user.AdminService;
import PlayForward.demo.user.Korisnik;
import PlayForward.demo.user.KorisnikRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final KorisnikRepository korisnikRepository;
    private final AdminService adminService;
    private static final int DISPLAY_NAME_MAX = 20;

    public CustomOAuth2UserService(KorisnikRepository korisnikRepository,
                                   AdminService adminService) {
        this.korisnikRepository = korisnikRepository;
        this.adminService = adminService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        if (email != null) {
            String fullName = normalizeDisplayName(oAuth2User.getAttribute("name"), email);

            Korisnik korisnik = korisnikRepository.findByEmail(email)
                    .orElseGet(Korisnik::new);
            korisnik.setEmail(email);
            korisnik.setImeKorisnik(fullName);
            Korisnik saved = korisnikRepository.save(korisnik);
            adminService.ensureAdminFor(saved);
        }

        return oAuth2User;
    }

    private String normalizeDisplayName(String name, String email) {
        String candidate = (name == null || name.isBlank()) ? email : name;
        String trimmed = candidate == null ? "" : candidate.trim();
        if (trimmed.isEmpty()) {
            trimmed = "Korisnik";
        }
        if (trimmed.length() > DISPLAY_NAME_MAX) {
            return trimmed.substring(0, DISPLAY_NAME_MAX);
        }
        return trimmed;
    }
}
