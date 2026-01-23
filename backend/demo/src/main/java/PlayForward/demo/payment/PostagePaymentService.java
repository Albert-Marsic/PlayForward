package PlayForward.demo.payment;

import PlayForward.demo.request.StatusZahtjeva;
import PlayForward.demo.request.Zahtjev;
import PlayForward.demo.request.ZahtjevRepository;
import PlayForward.demo.user.KorisnikRepository;
import PlayForward.demo.user.Primatelj;
import PlayForward.demo.user.PrimateljRepository;
import PlayForward.demo.security.SecurityUtil;
import PlayForward.demo.payment.dto.PayPalOrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class PostagePaymentService {

    private final PayPalService payPalService;
    private final ZahtjevRepository zahtjevRepo;
    private final KorisnikRepository korisnikRepo;
    private final PrimateljRepository primateljRepo;
    private final BigDecimal defaultPostageAmount;

    public PostagePaymentService(PayPalService payPalService,
                                 ZahtjevRepository zahtjevRepo,
                                 KorisnikRepository korisnikRepo,
                                 PrimateljRepository primateljRepo,
                                 @Value("${app.postage.amount:5.00}") String postageAmount) {
        this.payPalService = payPalService;
        this.zahtjevRepo = zahtjevRepo;
        this.korisnikRepo = korisnikRepo;
        this.primateljRepo = primateljRepo;
        this.defaultPostageAmount = parseAmount(postageAmount);
    }

    @Transactional
    public PayPalOrderResponse createOrder(Long zahtjevId) {
        if (zahtjevId == null || zahtjevId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID zahtjeva je obavezan.");
        }

        Primatelj primatelj = currentPrimateljOrThrow();
        Zahtjev zahtjev = zahtjevRepo.findById(zahtjevId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Zahtjev ne postoji."));

        if (!zahtjev.getPrimatelj().getId().equals(primatelj.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nemate pravo platiti ovaj zahtjev.");
        }

        if (zahtjev.getStatus() == StatusZahtjeva.APPROVED) {
            zahtjev.setStatus(StatusZahtjeva.POSTAGE_PENDING);
        }
        if (zahtjev.getStatus() != StatusZahtjeva.POSTAGE_PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poštarina nije dostupna za plaćanje.");
        }

        BigDecimal amount = zahtjev.getPostageAmount();
        if (amount == null) {
            amount = defaultPostageAmount;
            zahtjev.setPostageAmount(amount);
        }

        String description = "Postage for request #" + zahtjev.getId();
        PayPalService.PayPalOrder order = payPalService.createOrder(amount, description);

        zahtjev.setPaypalOrderId(order.id);
        zahtjevRepo.save(zahtjev);

        return new PayPalOrderResponse(order.id, order.status, amount);
    }

    @Transactional
    public Zahtjev captureOrder(Long zahtjevId, String orderId) {
        if (zahtjevId == null || zahtjevId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID zahtjeva je obavezan.");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PayPal order ID je obavezan.");
        }

        Primatelj primatelj = currentPrimateljOrThrow();
        Zahtjev zahtjev = zahtjevRepo.findById(zahtjevId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Zahtjev ne postoji."));

        if (!zahtjev.getPrimatelj().getId().equals(primatelj.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nemate pravo potvrditi ovaj zahtjev.");
        }

        if (zahtjev.getStatus() == StatusZahtjeva.APPROVED) {
            zahtjev.setStatus(StatusZahtjeva.POSTAGE_PENDING);
        }
        if (zahtjev.getStatus() != StatusZahtjeva.POSTAGE_PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poštarina nije dostupna za potvrdu.");
        }

        if (zahtjev.getPaypalOrderId() == null || !zahtjev.getPaypalOrderId().equals(orderId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PayPal order ID se ne podudara.");
        }

        PayPalService.PayPalCapture capture = payPalService.captureOrder(orderId);
        if (capture.status == null || !"COMPLETED".equalsIgnoreCase(capture.status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PayPal transakcija nije dovršena.");
        }
        if (capture.currency != null && !"EUR".equalsIgnoreCase(capture.currency)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Valuta PayPal transakcije nije ispravna.");
        }

        BigDecimal expectedAmount = zahtjev.getPostageAmount();
        if (expectedAmount != null && capture.amount != null
                && capture.amount.compareTo(expectedAmount) != 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "PayPal iznos se ne podudara.");
        }

        zahtjev.setStatus(StatusZahtjeva.POSTAGE_PAID);
        return zahtjevRepo.save(zahtjev);
    }

    private Primatelj currentPrimateljOrThrow() {
        String email = SecurityUtil.currentEmailOrThrow();
        Long korisnikId = korisnikRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Korisnik ne postoji u bazi."))
                .getId();
        return primateljRepo.findById(korisnikId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Nemate PRIMATELJ ulogu."));
    }

    private BigDecimal parseAmount(String amount) {
        if (amount == null || amount.isBlank()) {
            return new BigDecimal("5.00");
        }
        try {
            return new BigDecimal(amount);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid postage amount", ex);
        }
    }
}
