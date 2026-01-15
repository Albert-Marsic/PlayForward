package PlayForward.demo.review;

import PlayForward.demo.review.dto.RecenzijaCreateRequest;
import PlayForward.demo.review.dto.RecenzijaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RecenzijaController {

    private final RecenzijaService recenzijaService;

    public RecenzijaController(RecenzijaService recenzijaService) {
        this.recenzijaService = recenzijaService;
    }

    @PostMapping("/recenzije")
    public ResponseEntity<RecenzijaResponse> create(@Valid @RequestBody RecenzijaCreateRequest request) {
        return ResponseEntity.ok(recenzijaService.create(request));
    }

    @GetMapping("/donatori/{idDonator}/recenzije")
    public ResponseEntity<List<RecenzijaResponse>> listByDonator(@PathVariable Long idDonator) {
        return ResponseEntity.ok(recenzijaService.listByDonator(idDonator));
    }
}
