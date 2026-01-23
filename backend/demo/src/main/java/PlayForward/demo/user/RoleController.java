package PlayForward.demo.user;

import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.dto.ChooseRoleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final KorisnikRepository korisnikRepository;
    private final DonatorRepository donatorRepository;
    private final PrimateljRepository primateljRepository;

    public RoleController(KorisnikRepository korisnikRepository,
                          DonatorRepository donatorRepository,
                          PrimateljRepository primateljRepository) {
        this.korisnikRepository = korisnikRepository;
        this.donatorRepository = donatorRepository;
        this.primateljRepository = primateljRepository;
    }

    @PostMapping
    public ResponseEntity<?> chooseRole(@RequestBody ChooseRoleRequest req) {
        String email = SecurityUtil.currentEmailOrThrow();
        Korisnik korisnik = korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji u bazi."));

        String role = (req.role == null) ? "" : req.role.trim().toUpperCase();

        if (role.equals("DONATOR")) {
            if (!donatorRepository.existsById(korisnik.getId())) {
                Donator d = new Donator();
                d.setKorisnik(korisnik);
                donatorRepository.save(d);
            }
            return ResponseEntity.ok(java.util.Map.of("role", "DONATOR"));
        }

        if (role.equals("PRIMATELJ")) {
            if (!primateljRepository.existsById(korisnik.getId())) {
                Primatelj p = new Primatelj();
                p.setKorisnik(korisnik);
                primateljRepository.save(p);
            }
            return ResponseEntity.ok(java.util.Map.of("role", "PRIMATELJ"));
        }

        return ResponseEntity.badRequest().body(java.util.Map.of("error", "role mora biti DONATOR ili PRIMATELJ"));
    }
}
