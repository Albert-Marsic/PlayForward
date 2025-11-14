package PlayForward.demo;

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

    public CustomOAuth2UserService(KorisnikRepository korisnikRepository) {
        this.korisnikRepository = korisnikRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        if (email != null) {
            String fullName = oAuth2User.getAttribute("name");
            if (fullName == null || fullName.isBlank()) {
                fullName = email;
            }

            Korisnik korisnik = korisnikRepository.findByEmail(email)
                    .orElseGet(Korisnik::new);
            korisnik.setEmail(email);
            korisnik.setImeKorisnik(fullName);
            korisnikRepository.save(korisnik);
        }

        return oAuth2User;
    }
}
