package PlayForward.demo.campaign;

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
    public ResponseEntity<List<Kampanja>> getAllCampaigns() {
        return ResponseEntity.ok(kampanjaService.getAllCampaigns());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kampanja> getCampaignById(@PathVariable Long id) {
        return ResponseEntity.ok(kampanjaService.getCampaignById(id));
    }

    @PostMapping
    public ResponseEntity<Kampanja> createCampaign(@RequestBody Kampanja kampanja) {
        return ResponseEntity.ok(kampanjaService.createCampaign(kampanja));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Kampanja> updateCampaign(@PathVariable Long id, @RequestBody Kampanja updates) {
        return ResponseEntity.ok(kampanjaService.updateCampaign(id, updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        kampanjaService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/moje")
    public ResponseEntity<List<Kampanja>> getMyCampaigns() {
        return ResponseEntity.ok(kampanjaService.getCampaignsByCurrentUser());
    }
}
