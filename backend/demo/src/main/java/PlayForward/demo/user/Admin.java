package PlayForward.demo.user;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @Column(name = "idadmin")
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "idadmin")
    private Korisnik korisnik;

    public Admin() {}

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
