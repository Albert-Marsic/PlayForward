package PlayForward.demo.listing;

import PlayForward.demo.listing.dto.CreateIgrackaRequest;
import PlayForward.demo.listing.dto.UpdateUvjetiRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/igracke")
public class IgrackaController {

    private final IgrackaService service;

    public IgrackaController(IgrackaService service) {
        this.service = service;
    }

    // 1) Donator objavi igračku
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateIgrackaRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    // 2) Donator postavlja uvjete
    @PutMapping("/{id}/uvjeti")
    public ResponseEntity<?> updateUvjeti(@PathVariable Long id, @RequestBody UpdateUvjetiRequest req) {
        return ResponseEntity.ok(service.updateUvjeti(id, req == null ? null : req.uvjeti));
    }

    // 3) Primatelj filtrira dostupne
    @GetMapping
    public ResponseEntity<List<Igracka>> filter(
            @RequestParam(required = false) String kategorija,
            @RequestParam(required = false) StanjeIgracke stanje
    ) {
        return ResponseEntity.ok(service.filter(kategorija, stanje));
    }

    // 3.25) Donator pregled svojih igračaka
    @GetMapping("/moje")
    public ResponseEntity<List<Igracka>> listForCurrentDonator() {
        return ResponseEntity.ok(service.listForCurrentDonator());
    }

    // 3.5) Dohvati pojedinačnu igračku
    @GetMapping("/{id}")
    public ResponseEntity<Igracka> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // BONUS: primatelj "zatraži" -> rezerviraj
    @PostMapping("/{id}/rezerviraj")
    public ResponseEntity<?> rezerviraj(@PathVariable Long id) {
        return ResponseEntity.ok(service.rezerviraj(id));
    }

    // F-010: Donator povlači oglas
    @DeleteMapping("/{id}/povuci-oglas")
    public ResponseEntity<?> povuciOglas(@PathVariable Long id) {
        service.povuciOglas(id);
        return ResponseEntity.ok(java.util.Map.of("deleted", true));
    }

    // F-011: Primatelj odustaje od preuzimanja
    @PostMapping("/{id}/odustani-preuzimanje")
    public ResponseEntity<Igracka> odustaniOdPreuzimanja(@PathVariable Long id) {
        return ResponseEntity.ok(service.odustaniOdPreuzimanja(id));
    }
}
