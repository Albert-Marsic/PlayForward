package PlayForward.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class OAuthController {

    private final String oauthLoginPath = "/oauth2/authorization/google";

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("authenticated", true);
        body.put("name", principal.getAttribute("name"));
        body.put("email", principal.getAttribute("email"));
        body.put("picture", principal.getAttribute("picture"));

        return ResponseEntity.ok(body);
    }

    @GetMapping("/config")
    public Map<String, String> oauthConfig() {
        return Map.of("loginUrl", oauthLoginPath);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
