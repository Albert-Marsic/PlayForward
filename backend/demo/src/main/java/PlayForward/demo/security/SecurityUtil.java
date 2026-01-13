//dohvat emaila iz JWT-a napravljne uz pomoc CHAT GPTA :/

package PlayForward.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {}

    public static String currentEmailOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Niste autentificirani.");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomOAuth2User u) {
            String email = u.getEmail();
            if (email == null || email.isBlank()) throw new RuntimeException("Email nije dostupan u tokenu.");
            return email;
        }
        throw new RuntimeException("Nepodržan principal tip: " + principal.getClass().getName());
    }
}
