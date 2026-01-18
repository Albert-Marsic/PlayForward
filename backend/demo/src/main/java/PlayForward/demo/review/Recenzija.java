package PlayForward.demo.review;

import PlayForward.demo.user.Donator;
import PlayForward.demo.user.Primatelj;
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

    @Column(name = "tekst", length = 100, nullable = false)
    private String tekst;

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

    public void setPrimatelj(Primatelj primatelj) {
        this.primatelj = primatelj;
    }

    public void setDonator(Donator donator) {
        this.donator = donator;
    }
}
