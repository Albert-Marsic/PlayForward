package PlayForward.demo.payment;

import PlayForward.demo.payment.dto.CapturePayPalOrderRequest;
import PlayForward.demo.payment.dto.CreatePayPalOrderRequest;
import PlayForward.demo.payment.dto.PayPalOrderResponse;
import PlayForward.demo.request.Zahtjev;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/placanja/paypal")
public class PayPalController {

    private final PostagePaymentService postagePaymentService;

    public PayPalController(PostagePaymentService postagePaymentService) {
        this.postagePaymentService = postagePaymentService;
    }

    @PostMapping("/kreiraj")
    public ResponseEntity<PayPalOrderResponse> createOrder(@RequestBody CreatePayPalOrderRequest request) {
        Long zahtjevId = request != null ? request.zahtjevId : null;
        return ResponseEntity.ok(postagePaymentService.createOrder(zahtjevId));
    }

    @PostMapping("/potvrdi")
    public ResponseEntity<Zahtjev> captureOrder(@RequestBody CapturePayPalOrderRequest request) {
        Long zahtjevId = request != null ? request.zahtjevId : null;
        String orderId = request != null ? request.orderId : null;
        return ResponseEntity.ok(postagePaymentService.captureOrder(zahtjevId, orderId));
    }
}
