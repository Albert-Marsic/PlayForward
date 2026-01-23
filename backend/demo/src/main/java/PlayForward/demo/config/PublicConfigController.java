package PlayForward.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class PublicConfigController {

    private final String paypalClientId;

    public PublicConfigController(@Value("${app.paypal.client-id:}") String paypalClientId) {
        this.paypalClientId = paypalClientId;
    }

    @GetMapping("/paypal-client-id")
    public ResponseEntity<Map<String, String>> getPaypalClientId() {
        return ResponseEntity.ok(Map.of("clientId", paypalClientId == null ? "" : paypalClientId));
    }
}
