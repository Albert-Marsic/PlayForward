package PlayForward.demo.review.dto;

import PlayForward.demo.listing.Igracka;
import PlayForward.demo.request.Zahtjev;
import PlayForward.demo.review.Recenzija;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.Primatelj;

public class RecenzijaResponse {
    public Long id;
    public Integer ocjena;
    public String tekst;
    public Long zahtjevId;
    public String igrackaNaziv;
    public String donatorEmail;
    public String primateljEmail;

    public static RecenzijaResponse fromEntity(Recenzija recenzija) {
        RecenzijaResponse response = new RecenzijaResponse();
        if (recenzija == null) {
            return response;
        }

        response.id = recenzija.getId();
        response.ocjena = recenzija.getOcjena();
        response.tekst = recenzija.getTekst();

        Zahtjev zahtjev = recenzija.getZahtjev();
        if (zahtjev != null) {
            response.zahtjevId = zahtjev.getId();
            Igracka igracka = zahtjev.getIgracka();
            if (igracka != null) {
                response.igrackaNaziv = igracka.getNaziv();
            }
        }

        Donator donator = recenzija.getDonator();
        if (donator != null && donator.getKorisnik() != null) {
            response.donatorEmail = donator.getKorisnik().getEmail();
        }

        Primatelj primatelj = recenzija.getPrimatelj();
        if (primatelj != null && primatelj.getKorisnik() != null) {
            response.primateljEmail = primatelj.getKorisnik().getEmail();
        }

        return response;
    }
}
