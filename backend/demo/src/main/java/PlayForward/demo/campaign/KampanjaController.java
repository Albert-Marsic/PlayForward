package PlayForward.demo.campaign;

import PlayForward.demo.campaign.dto.CreateKampanjaRequest;
import PlayForward.demo.campaign.dto.KampanjaView;
import PlayForward.demo.campaign.dto.PopisIgracakaRequest;
import PlayForward.demo.campaign.dto.PopisIgracakaView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kampanje")
public class KampanjaController {

    private final KampanjaService kampanjaService;

    public KampanjaController(KampanjaService kampanjaService) {
        this.kampanjaService = kampanjaService;
    }

    @GetMapping
    public ResponseEntity<List<KampanjaView>> listAll() {
        return ResponseEntity.ok(kampanjaService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KampanjaView> getById(@PathVariable Long id) {
        return ResponseEntity.ok(kampanjaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<KampanjaView> create(@RequestBody CreateKampanjaRequest req) {
        return ResponseEntity.ok(kampanjaService.create(req));
    }

    @GetMapping("/{id}/popis")
    public ResponseEntity<List<PopisIgracakaView>> listPopis(@PathVariable Long id) {
        return ResponseEntity.ok(kampanjaService.listPopis(id));
    }

    @PostMapping("/{id}/popis")
    public ResponseEntity<List<PopisIgracakaView>> savePopis(@PathVariable Long id,
                                                             @RequestBody List<PopisIgracakaRequest> items) {
        return ResponseEntity.ok(kampanjaService.savePopis(id, items));
    }

    @PostMapping("/{id}/igracke/{nazivIgracke}/zahtjev")
    public ResponseEntity<PopisIgracakaView> markDonated(@PathVariable Long id,
                                                         @PathVariable String nazivIgracke) {
        return ResponseEntity.ok(kampanjaService.markDonated(id, nazivIgracke));
    }
}
