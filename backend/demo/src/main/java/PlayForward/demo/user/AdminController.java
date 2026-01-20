package PlayForward.demo.user;

import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.user.dto.AdminCampaignView;
import PlayForward.demo.user.dto.AdminDonationView;
import PlayForward.demo.user.dto.AdminUserView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminUserService adminUserService;

    public AdminController(AdminService adminService, AdminUserService adminUserService) {
        this.adminService = adminService;
        this.adminUserService = adminUserService;
    }

    @GetMapping("/korisnici")
    public List<AdminUserView> listUsers(@RequestParam(defaultValue = "50") int limit,
                                         @RequestParam(defaultValue = "0") int offset) {
        requireAdmin();
        return adminUserService.listUsers(limit, offset);
    }

    @GetMapping("/donacije")
    public List<AdminDonationView> listDonations(@RequestParam(defaultValue = "50") int limit,
                                                 @RequestParam(defaultValue = "0") int offset) {
        requireAdmin();
        return adminUserService.listDonations(limit, offset);
    }

    @GetMapping("/kampanje")
    public List<AdminCampaignView> listCampaigns(@RequestParam(defaultValue = "50") int limit,
                                                 @RequestParam(defaultValue = "0") int offset) {
        requireAdmin();
        return adminUserService.listCampaigns(limit, offset);
    }

    @GetMapping("/statistika")
    public Map<String, Object> stats() {
        requireAdmin();
        return adminUserService.getStats();
    }

    @DeleteMapping("/korisnici/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        requireAdmin();
        try {
            adminUserService.deleteUserById(id);
            return ResponseEntity.ok(Map.of("deleted", true, "userId", id));
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Brisanje korisnika nije uspjelo: " + rootCauseMessage(ex),
                    ex
            );
        }
    }

    @DeleteMapping("/donacije/{id}")
    public ResponseEntity<?> deleteDonation(@PathVariable Long id) {
        requireAdmin();
        adminUserService.deleteDonationById(id);
        return ResponseEntity.ok(Map.of("deleted", true, "donationId", id));
    }

    @DeleteMapping("/kampanje/{id}")
    public ResponseEntity<?> deleteCampaign(@PathVariable Long id) {
        requireAdmin();
        adminUserService.deleteCampaignById(id);
        return ResponseEntity.ok(Map.of("deleted", true, "campaignId", id));
    }

    private void requireAdmin() {
        String email = SecurityUtil.currentEmailOrThrow();
        if (!adminService.isAdminEmail(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin pristup je obavezan.");
        }
    }

    private String rootCauseMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return (message == null || message.isBlank()) ? current.getClass().getSimpleName() : message;
    }
}
