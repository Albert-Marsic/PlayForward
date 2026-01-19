package PlayForward.demo.review;

import PlayForward.demo.user.Donator;
import PlayForward.demo.user.Primatelj;
import PlayForward.demo.request.Zahtjev;
import jakarta.persistence.*;

@Entity
@Table(name = "recenzija")
public class Recenzija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idrecenzija")
    private Long id;

    @Column(name = "ocjena", nullable = false)
    private Integer ocjena;

    @Column(name = "tekst", length = 500)
    private String tekst;

    @OneToOne(optional = false)
    @JoinColumn(name = "idzahtjev", nullable = false, unique = true)
    private Zahtjev zahtjev;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idprimatelj", nullable = false)
    private Primatelj primatelj;

    @ManyToOne(optional = false)
    @JoinColumn(name = "iddonator", nullable = false)
    private Donator donator;

    public Recenzija() {}

    public Long getId() {
        return id;
    }

    public Integer getOcjena() {
        return ocjena;
    }

    public String getTekst() {
        return tekst;
    }

    public Zahtjev getZahtjev() {
        return zahtjev;
    }

    public Primatelj getPrimatelj() {
        return primatelj;
    }

    public Donator getDonator() {
        return donator;
    }

    public void setOcjena(Integer ocjena) {
        this.ocjena = ocjena;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public void setZahtjev(Zahtjev zahtjev) {
        this.zahtjev = zahtjev;
    }

    public void setPrimatelj(Primatelj primatelj) {
        this.primatelj = primatelj;
    }

    public void setDonator(Donator donator) {
        this.donator = donator;
    }
}
