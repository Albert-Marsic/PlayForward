package PlayForward.demo.request;

import PlayForward.demo.request.dto.CreateZahtjevRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zahtjevi")
public class ZahtjevController {

    private final ZahtjevService zahtjevService;

    public ZahtjevController(ZahtjevService zahtjevService) {
        this.zahtjevService = zahtjevService;
    }

    @PostMapping
    public ResponseEntity<Zahtjev> create(@RequestBody CreateZahtjevRequest req) {
        return ResponseEntity.ok(zahtjevService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<Zahtjev>> listForCurrentPrimatelj() {
        return ResponseEntity.ok(zahtjevService.listForCurrentPrimatelj());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Zahtjev> getById(@PathVariable Long id) {
        return ResponseEntity.ok(zahtjevService.getForCurrentPrimatelj(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Zahtjev> withdraw(@PathVariable Long id) {
        return ResponseEntity.ok(zahtjevService.withdraw(id));
    }

    @PostMapping("/{id}/preuzeto")
    public ResponseEntity<Zahtjev> markCompleted(@PathVariable Long id) {
        return ResponseEntity.ok(zahtjevService.markCompleted(id));
    }
}
