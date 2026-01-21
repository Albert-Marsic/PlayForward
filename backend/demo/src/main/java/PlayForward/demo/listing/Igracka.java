package PlayForward.demo.listing;

import PlayForward.demo.user.Donator;
import PlayForward.demo.user.Primatelj;
import jakarta.persistence.*;

@Entity
@Table(name = "igracka")
public class Igracka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idigracka")
    private Long id;

    @Column(name = "naziv", length = 20, nullable = false)
    private String naziv;

    @Column(name = "kategorija", length = 20, nullable = false)
    private String kategorija;

    @Enumerated(EnumType.STRING)
    @Column(name = "stanje", length = 12, nullable = false)
    private StanjeIgracke stanje;

    @Lob
    @Column(name = "fotografija", nullable = false)
    private String fotografija;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 12, nullable = false)
    private StatusIgracke status = StatusIgracke.DOSTUPNO;

    @Column(name = "uvjeti", length = 100)
    private String uvjeti;

    @ManyToOne(optional = false)
    @JoinColumn(name = "iddonator", nullable = false)
    private Donator donator;

    @ManyToOne
    @JoinColumn(name = "idprimatelj")
    private Primatelj primatelj;

    public Igracka() {}

    public Long getId() {
        return id;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getKategorija() {
        return kategorija;
    }

    public StanjeIgracke getStanje() {
        return stanje;
    }

    public String getFotografija() {
        return fotografija;
    }

    public StatusIgracke getStatus() {
        return status;
    }

    public String getUvjeti() {
        return uvjeti;
    }

    public Donator getDonator() {
        return donator;
    }

    public Primatelj getPrimatelj() {
        return primatelj;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public void setKategorija(String kategorija) {
        this.kategorija = kategorija;
    }

    public void setStanje(StanjeIgracke stanje) {
        this.stanje = stanje;
    }

    public void setFotografija(String fotografija) {
        this.fotografija = fotografija;
    }

    public void setStatus(StatusIgracke status) {
        this.status = status;
    }

    public void setUvjeti(String uvjeti) {
        this.uvjeti = uvjeti;
    }

    public void setDonator(Donator donator) {
        this.donator = donator;
    }

    public void setPrimatelj(Primatelj primatelj) {
        this.primatelj = primatelj;
    }
}
