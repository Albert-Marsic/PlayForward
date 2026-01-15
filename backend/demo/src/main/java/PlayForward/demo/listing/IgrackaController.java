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

    // BONUS: primatelj odustane
    @PostMapping("/{id}/odustani")
    public ResponseEntity<?> odustani(@PathVariable Long id) {
        return ResponseEntity.ok(service.odustani(id));
    }

    // BONUS: donator povuče oglas dok je dostupno
    @DeleteMapping("/{id}")
    public ResponseEntity<?> povuci(@PathVariable Long id) {
        service.povuci(id);
        return ResponseEntity.ok(java.util.Map.of("deleted", true));
    }
}
