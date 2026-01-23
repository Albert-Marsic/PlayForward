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
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Iznos poštarine mora biti pozitivan.");
        }

        if (zahtjev.getPaypalOrderId() != null && !zahtjev.getPaypalOrderId().isBlank()) {
            zahtjevRepo.save(zahtjev);
            return new PayPalOrderResponse(zahtjev.getPaypalOrderId(), "EXISTING", amount);
        }

        String description = "Postage for request #" + zahtjev.getId();
        PayPalService.PayPalOrder order = createOrder(amount, description);

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

        if (zahtjev.getStatus() == StatusZahtjeva.POSTAGE_PAID) {
            return zahtjev;
        }
        if (zahtjev.getStatus() == StatusZahtjeva.PICKED_UP
                || zahtjev.getStatus() == StatusZahtjeva.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Zahtjev je već završen.");
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

        PayPalService.PayPalCapture capture = captureOrder(orderId);
        if (capture.status == null || !"COMPLETED".equalsIgnoreCase(capture.status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PayPal transakcija nije dovršena.");
        }
        if (capture.amount == null || capture.currency == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PayPal nije vratio iznos ili valutu.");
        }
        if (!"EUR".equalsIgnoreCase(capture.currency)) {
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
            BigDecimal parsed = new BigDecimal(amount);
            if (parsed.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Postage amount must be positive");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid postage amount", ex);
        }
    }

    private PayPalService.PayPalOrder createOrder(BigDecimal amount, String description) {
        try {
            return payPalService.createOrder(amount, description);
        } catch (PayPalApiException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "PayPal greška: " + ex.getMessage(), ex);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Neuspješno kreiranje PayPal narudžbe.", ex);
        }
    }

    private PayPalService.PayPalCapture captureOrder(String orderId) {
        try {
            return payPalService.captureOrder(orderId);
        } catch (PayPalApiException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "PayPal greška: " + ex.getMessage(), ex);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Neuspješno potvrđivanje PayPal narudžbe.", ex);
        }
    }
}
