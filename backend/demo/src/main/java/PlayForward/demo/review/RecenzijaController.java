package PlayForward.demo.review;

import PlayForward.demo.review.dto.CreateRecenzijaRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recenzije")
public class RecenzijaController {

    private final RecenzijaService recenzijaService;

    public RecenzijaController(RecenzijaService recenzijaService) {
        this.recenzijaService = recenzijaService;
    }

    @PostMapping
    public ResponseEntity<Recenzija> create(@RequestBody CreateRecenzijaRequest req) {
        return ResponseEntity.ok(recenzijaService.create(req));
    }

    @GetMapping("/donator/{donatorId}")
    public ResponseEntity<List<Recenzija>> listForDonator(@PathVariable Long donatorId) {
        return ResponseEntity.ok(recenzijaService.listForDonator(donatorId));
    }

    @GetMapping("/moje")
    public ResponseEntity<List<Recenzija>> listForCurrentDonator() {
        return ResponseEntity.ok(recenzijaService.listForCurrentDonator());
    }
}
