package PlayForward.demo.campaign;

import PlayForward.demo.user.Primatelj;   // ⟵ ispravan import
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kampanja")
public class Kampanja {

    public Kampanja() {}                    // ⟵ no-arg ctor za JPA

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idkampanja")
    private Long id;

    // u SQL-u kolona je napisana roktrajanja (ne-citirano => sve malim slovima)
    @Column(name = "roktrajanja", nullable = false)
    private LocalDate rokTrajanja;

    @Column(name = "napredak", length = 100, nullable = false) // ⟵ u bazi NOT NULL
    private String napredak;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idprimatelj", nullable = false)
    private Primatelj primatelj;

    @OneToMany(mappedBy = "kampanja", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PopisIgracaka> popisi = new ArrayList<>();

    // get/set
    public Long getId() { return id; }
    public LocalDate getRokTrajanja() { return rokTrajanja; }
    public String getNapredak() { return napredak; }
    public Primatelj getPrimatelj() { return primatelj; }
    public List<PopisIgracaka> getPopisi() { return popisi; }
    public void setRokTrajanja(LocalDate rokTrajanja) { this.rokTrajanja = rokTrajanja; }
    public void setNapredak(String napredak) { this.napredak = napredak; }
    public void setPrimatelj(Primatelj primatelj) { this.primatelj = primatelj; }

    // helper metode (nije obavezno, ali korisno)
    public void addStavka(PopisIgracaka s) { popisi.add(s); s.setKampanja(this); }
    public void removeStavka(PopisIgracaka s) { popisi.remove(s); s.setKampanja(null); }
}
