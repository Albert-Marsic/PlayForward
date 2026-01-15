package PlayForward.demo.listing.dto;

import PlayForward.demo.listing.StanjeIgracke;
import PlayForward.demo.listing.dto.CreateIgrackaRequest;


public class CreateIgrackaRequest {
    public String naziv;
    public String kategorija;
    public StanjeIgracke stanje;
    public String fotografija; // obavezno
    public String uvjeti;      // opcionalno
}
