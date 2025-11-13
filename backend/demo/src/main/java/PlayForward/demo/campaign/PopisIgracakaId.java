package PlayForward.demo.campaign;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PopisIgracakaId implements Serializable {
    private String nazivIgracke;
    private Long idKampanja;

    public PopisIgracakaId() {}

    public PopisIgracakaId(String nazivIgracke, Long idKampanja) {
        this.nazivIgracke = nazivIgracke;
        this.idKampanja = idKampanja;
    }

    public String getNazivIgracke() {
        return nazivIgracke;
    }

    public Long getIdKampanja() {
        return idKampanja;
    }

    public void setNazivIgracke(String nazivIgracke) {
        this.nazivIgracke = nazivIgracke;
    }

    public void setIdKampanja(Long idKampanja) {
        this.idKampanja = idKampanja;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PopisIgracakaId that)) return false;
        return Objects.equals(nazivIgracke, that.nazivIgracke)
                && Objects.equals(idKampanja, that.idKampanja);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nazivIgracke, idKampanja);
    }
}
