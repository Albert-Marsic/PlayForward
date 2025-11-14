package PlayForward.demo.campaign;

import jakarta.persistence.*;

@Entity
@Table(name = "popis_igracaka")
public class PopisIgracaka {

    @EmbeddedId
    private PopisIgracakaId id;

    @MapsId("idKampanja")
    @ManyToOne(optional = false)
    @JoinColumn(name = "idkampanja", nullable = false)
    private Kampanja kampanja;

    @Column(name = "kolicina", nullable = false)
    private Integer kolicina;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private StatusPopisa status;

    public PopisIgracaka() {}

    public PopisIgracakaId getId() {
        return id;
    }
    public Kampanja getKampanja() {
        return kampanja;
    }
    public Integer getKolicina() {
        return kolicina;
    }
    public StatusPopisa getStatus() {
        return status;
    }
    public void setId(PopisIgracakaId id) {
        this.id = id;
    }
    public void setKampanja(Kampanja kampanja) {
        this.kampanja = kampanja;
    }
    public void setKolicina(Integer kolicina) {
        this.kolicina = kolicina;
    }
    public void setStatus(StatusPopisa status) {
        this.status = status;
    }
}
