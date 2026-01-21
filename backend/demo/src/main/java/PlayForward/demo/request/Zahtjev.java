package PlayForward.demo.request;

import PlayForward.demo.listing.Igracka;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.Primatelj;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "zahtjev")
public class Zahtjev {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idzahtjev")
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "status_zahtjeva", nullable = false)
    private StatusZahtjeva status;

    @Column(name = "datumzahtjeva", nullable = false)
    private LocalDateTime datumZahtjeva;

    @Column(name = "napomena", length = 200)
    private String napomena;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idigracka", nullable = false)
    private Igracka igracka;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idprimatelj", nullable = false)
    private Primatelj primatelj;

    @ManyToOne(optional = false)
    @JoinColumn(name = "iddonator", nullable = false)
    private Donator donator;

    public Zahtjev() {}

    public Long getId() {
        return id;
    }

    public StatusZahtjeva getStatus() {
        return status;
    }

    public LocalDateTime getDatumZahtjeva() {
        return datumZahtjeva;
    }

    public String getNapomena() {
        return napomena;
    }

    public Igracka getIgracka() {
        return igracka;
    }

    public Primatelj getPrimatelj() {
        return primatelj;
    }

    public Donator getDonator() {
        return donator;
    }

    public void setStatus(StatusZahtjeva status) {
        this.status = status;
    }

    public void setDatumZahtjeva(LocalDateTime datumZahtjeva) {
        this.datumZahtjeva = datumZahtjeva;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }

    public void setIgracka(Igracka igracka) {
        this.igracka = igracka;
    }

    public void setPrimatelj(Primatelj primatelj) {
        this.primatelj = primatelj;
    }

    public void setDonator(Donator donator) {
        this.donator = donator;
    }
}
