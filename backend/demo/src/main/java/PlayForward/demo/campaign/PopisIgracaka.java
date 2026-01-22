package PlayForward.demo.campaign;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "popis_igracaka")
public class PopisIgracaka {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "nazivIgracke", column = @Column(name = "nazivigracke")),
            @AttributeOverride(name = "idKampanja", column = @Column(name = "idkampanja"))
    })
    private PopisIgracakaId id;

    @MapsId("idKampanja")
    @ManyToOne(optional = false)
    @JoinColumn(name = "idkampanja", nullable = false)
    private Kampanja kampanja;

    @Column(name = "kolicina", nullable = false)
    private Integer kolicina;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "status_popisa_igracaka", nullable = false)
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
