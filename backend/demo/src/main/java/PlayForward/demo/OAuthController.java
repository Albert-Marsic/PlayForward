package PlayForward.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.DonatorRepository;
import PlayForward.demo.user.Korisnik;
import PlayForward.demo.user.KorisnikRepository;
import PlayForward.demo.user.Primatelj;
import PlayForward.demo.user.PrimateljRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class OAuthController {

    private final String oauthLoginPath = "/oauth2/authorization/google";
    private final KorisnikRepository korisnikRepository;
    private final DonatorRepository donatorRepository;
    private final PrimateljRepository primateljRepository;

    public OAuthController(KorisnikRepository korisnikRepository,
                           DonatorRepository donatorRepository,
                           PrimateljRepository primateljRepository) {
        this.korisnikRepository = korisnikRepository;
        this.donatorRepository = donatorRepository;
        this.primateljRepository = primateljRepository;
    }

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

    @GetMapping("/role")
    public ResponseEntity<?> currentRole(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User is not authenticated"));
        }

        String email = principal.getAttribute("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email is missing from OAuth profile"));
        }

        Korisnik korisnik = korisnikRepository.findByEmail(email).orElse(null);
        if (korisnik == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User profile not found"));
        }

        String role = resolveRole(korisnik.getId());
        if ("CONFLICT".equals(role)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "User has multiple roles assigned"));
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("role", role);
        payload.put("registered", role != null);
        return ResponseEntity.ok(payload);
    }

    @PostMapping("/role")
    public ResponseEntity<?> chooseRole(@AuthenticationPrincipal OAuth2User principal,
                                        @RequestBody RoleRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User is not authenticated"));
        }

        String email = principal.getAttribute("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email is missing from OAuth profile"));
        }

        String desiredRole = normalizeRole(request != null ? request.getRole() : null);
        if (desiredRole == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Role must be DONATOR or RECIPIENT"));
        }

        String name = principal.getAttribute("name");
        final String displayName = (name == null || name.isBlank()) ? email : name;

        Korisnik korisnik = korisnikRepository.findByEmail(email).orElseGet(() -> {
            Korisnik newUser = new Korisnik();
            newUser.setEmail(email);
            newUser.setImeKorisnik(displayName);
            return korisnikRepository.save(newUser);
        });

        String existingRole = resolveRole(korisnik.getId());
        if ("CONFLICT".equals(existingRole)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "User has multiple roles assigned"));
        }
        if (existingRole != null && !existingRole.equals(desiredRole)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "User already registered as " + existingRole));
        }
        if (existingRole == null) {
            if ("DONATOR".equals(desiredRole)) {
                Donator donator = new Donator();
                donator.setKorisnik(korisnik);
                donatorRepository.save(donator);
            } else {
                Primatelj primatelj = new Primatelj();
                primatelj.setKorisnik(korisnik);
                primateljRepository.save(primatelj);
            }
        }

        return ResponseEntity.ok(Map.of("role", desiredRole));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) {
        // For JWT-based authentication, logout is handled on the frontend
        // by removing the token from localStorage
        // We still clear the SecurityContext for good measure
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }

    private String resolveRole(Long korisnikId) {
        if (korisnikId == null) {
            return null;
        }
        boolean isDonator = donatorRepository.existsById(korisnikId);
        boolean isPrimatelj = primateljRepository.existsById(korisnikId);
        if (isDonator && isPrimatelj) {
            return "CONFLICT";
        }
        if (isDonator) {
            return "DONATOR";
        }
        if (isPrimatelj) {
            return "RECIPIENT";
        }
        return null;
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return null;
        }
        String normalized = role.trim().toUpperCase();
        if ("DONATOR".equals(normalized)) {
            return "DONATOR";
        }
        if ("RECIPIENT".equals(normalized) || "PRIMATELJ".equals(normalized)) {
            return "RECIPIENT";
        }
        return null;
    }

    private static class RoleRequest {
        private String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
