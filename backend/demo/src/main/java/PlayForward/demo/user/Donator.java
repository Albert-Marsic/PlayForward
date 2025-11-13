package PlayForward.demo.user;

import jakarta.persistence.*;

@Entity
@Table(name = "donator")
public class Donator {

    @Id
    @Column(name = "iddonator")
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "iddonator")
    private Korisnik korisnik;

    public Donator() {}

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
