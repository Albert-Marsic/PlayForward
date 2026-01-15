package PlayForward.demo.review.dto;

import PlayForward.demo.review.Recenzija;

public class RecenzijaResponse {
    public Long id;
    public Integer ocjena;
    public String komentar;
    public Long idDonator;
    public Long idPrimatelj;

    public static RecenzijaResponse fromEntity(Recenzija recenzija) {
        RecenzijaResponse response = new RecenzijaResponse();
        response.id = recenzija.getId();
        response.ocjena = recenzija.getOcjena();
        response.komentar = recenzija.getTekst();
        response.idDonator = recenzija.getDonator().getId();
        response.idPrimatelj = recenzija.getPrimatelj().getId();
        return response;
    }
}
