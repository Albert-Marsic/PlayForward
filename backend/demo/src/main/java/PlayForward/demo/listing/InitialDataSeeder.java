package PlayForward.demo.listing;

//Klasa za popunjavanje baze podataka sa 25 inicijalnih igracaka pri pokretanju aplikacije

import PlayForward.demo.listing.Igracka;
import PlayForward.demo.listing.IgrackaRepository;
import PlayForward.demo.listing.StanjeIgracke;
import PlayForward.demo.listing.StatusIgracke;
import PlayForward.demo.user.Korisnik;
import PlayForward.demo.user.KorisnikRepository;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.DonatorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitialDataSeeder {

    @Bean
    CommandLineRunner seedInitialToys(
            KorisnikRepository korisnikRepository,
            DonatorRepository donatorRepository,
            IgrackaRepository igrackaRepository
    ) {
        return args -> {

            if (igrackaRepository.count() > 0) {
                return;
            }


            Korisnik korisnik = new Korisnik();
            korisnik.setImeKorisnik("Demo Donator");
            korisnik.setEmail("demo.donator@example.com");
            korisnik = korisnikRepository.save(korisnik);

            Donator donator = new Donator();
            donator.setKorisnik(korisnik);
            donator = donatorRepository.save(donator);


            dodajIgracku(igrackaRepository, donator,
                    "crveni autić", "auta",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy01.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "minion figurica", "figure",
                    StanjeIgracke.KORISTENO,
                    "/images/toys/toy02.jpg",
                    "Dostava na trošak primatelja");

            dodajIgracku(igrackaRepository, donator,
                    "batman LEGO set", "LEGO",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy03.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "groot figurica", "figure",
                    StanjeIgracke.KORISTENO,
                    "/images/toys/toy04.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "gumena patkica", "figure",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy05.jpg",
                    "Dostava ili osobno");

            dodajIgracku(igrackaRepository, donator,
                    "robot igračka", "figure",
                    StanjeIgracke.KORISTENO,
                    "/images/toys/toy06.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "Dinosaur figurice", "Figurice",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy07.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "robot figura", "figure",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy08.jpg",
                    "Dostava");

            dodajIgracku(igrackaRepository, donator,
                    "Buzz Svjetlosni", "figure",
                    StanjeIgracke.KORISTENO,
                    "/images/toys/toy09.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "Barbie", "Lutke",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy10.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "rubrikova kocka", "kocke",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy11.jpg",
                    "Dostava na trošak primatelja");

            dodajIgracku(igrackaRepository, donator,
                    "figurica motocikl", "figure",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy12.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "autić", "auta",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy13.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "zbrajalica", "igre",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy14.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "igračka biljka", "plišanci",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy15.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "LEGO kocke", "LEGO",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy16.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "autić", "auta",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy17.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "plišani slon", "plišanci",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy18.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "LEGO figura", "LEGO",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy19.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "LEGO Yoda figurica", "LEGO",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy20.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "kamion i bager", "Igračke",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy21.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "LEGO kockice", "LEGO",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy22.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "plišani medo", "Igračke",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy23.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "Plišani slon", "plišasnci",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy24.jpg",
                    "Preuzimanje osobno");

            dodajIgracku(igrackaRepository, donator,
                    "Gumena patkica žuta", "Igračke",
                    StanjeIgracke.NOVO,
                    "/images/toys/toy25.jpg",
                    "Preuzimanje osobno");


        };
    }

    private void dodajIgracku(
            IgrackaRepository igrackaRepository,
            Donator donator,
            String naziv,
            String kategorija,
            StanjeIgracke stanje,
            String slikaPath,
            String uvjeti
    ) {
        Igracka igracka = new Igracka();
        igracka.setNaziv(naziv);
        igracka.setKategorija(kategorija);
        igracka.setStanje(stanje);
        igracka.setFotografija(slikaPath);
        igracka.setStatus(StatusIgracke.DOSTUPNO);
        igracka.setUvjeti(uvjeti);
        igracka.setDonator(donator);

        igrackaRepository.save(igracka);
    }
}
