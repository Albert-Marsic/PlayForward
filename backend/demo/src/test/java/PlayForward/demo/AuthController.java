package PlayForward.demo;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired private UserRepository userRepo;
    @Autowired private TokenRepository tokenRepo;
    //@Autowired private EmailService emailService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userRepo.save(user);
        String token = UUID.randomUUID().toString();

        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(user);
        tokenRepo.save(vt);

        //String verifyLink = "http://localhost:8080/api/verify?token=" + token;
        //emailService.send(user.getEmail(), "Verify your email", verifyLink);

        return "Verification email sent!";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        VerificationToken vt = tokenRepo.findByToken(token);
        if (vt == null) return "Invalid token";

        User user = vt.getUser();
        user.setVerified(true);
        userRepo.save(user);
        return "Email verified successfully!";
    }
}
