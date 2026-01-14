package PlayForward.demo.user;

import jakarta.persistence.*;

@Entity
@Table(name = "korisnik", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Korisnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idkorisnik")
    private Long id;

    @Column(name = "imekorisnik", length = 100, nullable = false)
    private String imeKorisnik;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    public Korisnik() {}

    public Long getId() {
        return id;
    }

    public String getImeKorisnik() {
        return imeKorisnik;
    }

    public String getEmail() {
        return email;
    }

    public void setImeKorisnik(String imeKorisnik) {
        this.imeKorisnik = imeKorisnik;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
