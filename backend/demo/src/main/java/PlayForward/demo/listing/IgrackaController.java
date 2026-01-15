//pod pretpostavkom da se salje JSON

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

    // Donator: objavi igračku
    @PostMapping
    public ResponseEntity<Igracka> create(@RequestBody CreateIgrackaRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    // Donator: promijeni uvjete (frontend šalje JSON: { "uvjeti": "..." })
    @PutMapping("/{id}/uvjeti")
    public ResponseEntity<Igracka> updateUvjeti(@PathVariable Long id,
                                                @RequestBody UpdateUvjetiRequest req) {
        return ResponseEntity.ok(service.updateUvjeti(id, req.uvjeti));
    }

    // Primatelj: filtriranje (samo dostupne)
    @GetMapping
    public ResponseEntity<List<Igracka>> filter(
            @RequestParam(required = false) String kategorija,
            @RequestParam(required = false) StanjeIgracke stanje
    ) {
        return ResponseEntity.ok(service.filter(kategorija, stanje));
    }

    // Primatelj: pošalji zahtjev
    @PostMapping("/{id}/zahtjev")
    public ResponseEntity<Igracka> posaljiZahtjev(@PathVariable Long id) {
        return ResponseEntity.ok(service.posaljiZahtjev(id));
    }

    // Donator: lista zahtjeva (obavijesti)
    @GetMapping("/moji/zahtjevi")
    public ResponseEntity<List<Igracka>> mojiZahtjevi() {
        return ResponseEntity.ok(service.mojiZahtjevi());
    }

    // Donator: prihvati zahtjev
    @PostMapping("/{id}/zahtjev/prihvati")
    public ResponseEntity<Igracka> prihvati(@PathVariable Long id) {
        return ResponseEntity.ok(service.prihvatiZahtjev(id));
    }

    // Donator: odbij zahtjev
    @PostMapping("/{id}/zahtjev/odbij")
    public ResponseEntity<Igracka> odbij(@PathVariable Long id) {
        return ResponseEntity.ok(service.odbijZahtjev(id));
    }
}
