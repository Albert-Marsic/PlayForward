




//test za ispitivanje povezanosti s bazom sa gpt-a









package PlayForward.demo;

import PlayForward.demo.user.Korisnik;
import PlayForward.demo.user.KorisnikRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbTestConfig {
    /*
    @Bean
    public CommandLineRunner dbTestRunner(KorisnikRepository korisnikRepository) {
        return args -> {
            System.out.println("=== DB TEST POCETAK ===");

            // 1) Ispiši koliko ima korisnika u bazi
            long count = korisnikRepository.count();
            System.out.println("Broj korisnika u bazi: " + count);

            // 2) Ispiši sve korisnike
            korisnikRepository.findAll().forEach(k -> {
                System.out.println(" - " + k.getId() + " | " + k.getImeKorisnik() + " | " + k.getEmail());
            });

            // 3) Ubaci jednog novog korisnika preko JPA
            Korisnik novi = new Korisnik();
            novi.setImeKorisnik("KorisnikIzJave");
            novi.setEmail("java.test@example.com");

            korisnikRepository.save(novi);

            System.out.println("Spremio novog korisnika preko JPA.");

            long newCount = korisnikRepository.count();
            System.out.println("Novi broj korisnika u bazi: " + newCount);

            System.out.println("=== DB TEST KRAJ ===");
        };
    }*/
}
