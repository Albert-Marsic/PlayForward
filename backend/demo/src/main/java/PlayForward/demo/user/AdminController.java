package PlayForward.demo.user;

import PlayForward.demo.security.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    @DeleteMapping("/korisnici/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        String email = SecurityUtil.currentEmailOrThrow();
        if (!adminService.isAdminEmail(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin pristup je obavezan.");
        }
        adminUserService.deleteUserById(id);
        return ResponseEntity.ok(Map.of("deleted", true, "userId", id));
    }
}
