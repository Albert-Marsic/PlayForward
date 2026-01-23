package PlayForward.demo.review;

import PlayForward.demo.review.dto.CreateRecenzijaRequest;
import PlayForward.demo.review.dto.RecenzijaResponse;
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
    public ResponseEntity<RecenzijaResponse> create(@RequestBody CreateRecenzijaRequest req) {
        return ResponseEntity.ok(recenzijaService.create(req));
    }

    @GetMapping("/donator/{donatorId}")
    public ResponseEntity<List<RecenzijaResponse>> listForDonator(@PathVariable Long donatorId) {
        return ResponseEntity.ok(recenzijaService.listForDonator(donatorId));
    }

    @GetMapping("/moje")
    public ResponseEntity<List<RecenzijaResponse>> listForCurrentDonator() {
        return ResponseEntity.ok(recenzijaService.listForCurrentDonator());
    }

    @GetMapping("/primatelj/moje")
    public ResponseEntity<List<RecenzijaResponse>> listForCurrentPrimatelj() {
        return ResponseEntity.ok(recenzijaService.listForCurrentPrimatelj());
    }
}
