package PlayForward.demo.user;

import jakarta.persistence.*;

@Entity
@Table(name = "primatelj")
public class Primatelj {

    @Id
    @Column(name = "idprimatelj")
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "idprimatelj")
    private Korisnik korisnik;

    public Primatelj() {}

    public Long getId() {
        return id;
    }

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }
}
