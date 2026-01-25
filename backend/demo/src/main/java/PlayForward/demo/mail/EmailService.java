package PlayForward.demo.mail;

import PlayForward.demo.review.Recenzija;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender,
            @Value("${app.mail.from:}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = (fromAddress == null || fromAddress.isBlank())
                ? null
                : fromAddress.trim();
    }

    @Async
    public void sendReviewNotificationAsync(String to, Recenzija recenzija) {
        if (to == null || to.isBlank()) {
            log.warn("Email primatelja nije definiran, preskacem slanje recenzije.");
            return;
        }
        if (recenzija == null) {
            log.warn("Recenzija nije dostupna, preskacem slanje emaila.");
            return;
        }

        String igrackaNaziv = recenzija.getZahtjev() != null
                && recenzija.getZahtjev().getIgracka() != null
                        ? recenzija.getZahtjev().getIgracka().getNaziv()
                        : "Nepoznato";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            if (fromAddress != null) {
                message.setFrom(fromAddress);
            }
            message.setSubject("Nova recenzija na PlayForward");
            message.setText(
                    "Dobili ste novu recenziju za donaciju.\n\n" +
                            "Igracka: " + igrackaNaziv + "\n" +
                            "Ocjena: " + recenzija.getOcjena() + "/5\n\n" +
                            "Komentar:\n" + recenzija.getTekst() + "\n");

            mailSender.send(message);
        } catch (Exception ex) {
            log.warn("Neuspjelo slanje emaila za recenziju.", ex);
        }
    }

    @Async
    public void sendRequestNotificationAsync(String to, String primateljIme, String igrackaNaziv, String napomena) {
        if (to == null || to.isBlank()) {
            log.warn("Email donatora nije definiran, preskacem slanje obavijesti o zahtjevu.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            if (fromAddress != null) {
                message.setFrom(fromAddress);
            }
            message.setSubject("Novi zahtjev za donaciju - PlayForward");

            String tekst = "Poštovani,\n\n" +
                    "Korisnik " + (primateljIme != null ? primateljIme : "(nepoznat)") +
                    " je poslao zahtjev za Vašu igračku: " + (igrackaNaziv != null ? igrackaNaziv : "(nepoznato)")
                    + ".\n\n";

            if (napomena != null && !napomena.isBlank()) {
                tekst += "Napomena primatelja:\n" + napomena + "\n\n";
            }

            tekst += "Prijavite se na PlayForward da biste pregledali i odobrili zahtjev.\n\n" +
                    "Srdačan pozdrav,\nPlayForward tim";

            message.setText(tekst);
            mailSender.send(message);
            log.info("Poslan email donatoru: {} za novi zahtjev", to);
        } catch (Exception ex) {
            log.warn("Neuspjelo slanje emaila za novi zahtjev.", ex);
        }
    }

    @Async
    public void sendWithdrawalNotificationAsync(String to, String primateljIme, String igrackaNaziv) {
        if (to == null || to.isBlank()) {
            log.warn("Email donatora nije definiran, preskacem slanje obavijesti o odustajanju.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            if (fromAddress != null) {
                message.setFrom(fromAddress);
            }
            message.setSubject("Odustajanje od zahtjeva - PlayForward");

            String tekst = "Poštovani,\n\n" +
                    "Korisnik " + (primateljIme != null ? primateljIme : "(nepoznat)") +
                    " je odustao od zahtjeva za igračku: " + (igrackaNaziv != null ? igrackaNaziv : "(nepoznato)")
                    + ".\n\n" +
                    "Igračka je sada ponovo dostupna i drugi korisnici je mogu zatražiti.\n\n" +
                    "Srdačan pozdrav,\nPlayForward tim";

            message.setText(tekst);
            mailSender.send(message);
            log.info("Poslan email donatoru: {} za odustajanje od zahtjeva", to);
        } catch (Exception ex) {
            log.warn("Neuspjelo slanje emaila za odustajanje.", ex);
        }
    }

    @Async
    public void sendApprovalNotificationAsync(String to, String donatorIme, String igrackaNaziv) {
        if (to == null || to.isBlank()) {
            log.warn("Email primatelja nije definiran, preskacem slanje obavijesti o odobrenju.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            if (fromAddress != null) {
                message.setFrom(fromAddress);
            }
            message.setSubject("Vaš zahtjev je odobren - PlayForward");

            String tekst = "Poštovani,\n\n" +
                    "Vaš zahtjev za igračku \"" + (igrackaNaziv != null ? igrackaNaziv : "(nepoznato)") +
                    "\" je odobren od strane donatora " + (donatorIme != null ? donatorIme : "(nepoznat)") + ".\n\n" +
                    "Sljedeći korak je plaćanje poštarine putem PayPal-a na PlayForward platformi.\n\n" +
                    "Srdačan pozdrav,\nPlayForward tim";

            message.setText(tekst);
            mailSender.send(message);
            log.info("Poslan email primatelju: {} za odobrenje zahtjeva", to);
        } catch (Exception ex) {
            log.warn("Neuspjelo slanje emaila za odobrenje.", ex);
        }
    }

    @Async
    public void sendRejectionNotificationAsync(String to, String donatorIme, String igrackaNaziv) {
        if (to == null || to.isBlank()) {
            log.warn("Email primatelja nije definiran, preskacem slanje obavijesti o odbijanju.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            if (fromAddress != null) {
                message.setFrom(fromAddress);
            }
            message.setSubject("Vaš zahtjev je odbijen - PlayForward");

            String tekst = "Poštovani,\n\n" +
                    "Nažalost, Vaš zahtjev za igračku \"" + (igrackaNaziv != null ? igrackaNaziv : "(nepoznato)") +
                    "\" je odbijen od strane donatora " + (donatorIme != null ? donatorIme : "(nepoznat)") + ".\n\n" +
                    "Možete pregledati druge dostupne igračke na PlayForward platformi.\n\n" +
                    "Srdačan pozdrav,\nPlayForward tim";

            message.setText(tekst);
            mailSender.send(message);
            log.info("Poslan email primatelju: {} za odbijanje zahtjeva", to);
        } catch (Exception ex) {
            log.warn("Neuspjelo slanje emaila za odbijanje.", ex);
        }
    }

    @Async
    public void sendPostagePaidNotificationAsync(String to, String primateljIme, String igrackaNaziv) {
        if (to == null || to.isBlank()) {
            log.warn("Email donatora nije definiran, preskacem slanje obavijesti o placanju postarine.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            if (fromAddress != null) {
                message.setFrom(fromAddress);
            }
            message.setSubject("Poštarina plaćena - PlayForward");

            String tekst = "Poštovani,\n\n" +
                    "Korisnik " + (primateljIme != null ? primateljIme : "(nepoznat)") +
                    " je platio poštarinu za igračku \"" + (igrackaNaziv != null ? igrackaNaziv : "(nepoznato)") + "\".\n\n" +
                    "Molimo vas da igračku pošaljete ili pripremite za preuzimanje.\n" +
                    "Nakon što primatelj preuzme igračku, potvrdite preuzimanje na PlayForward platformi.\n\n" +
                    "Srdačan pozdrav,\nPlayForward tim";

            message.setText(tekst);
            mailSender.send(message);
            log.info("Poslan email donatoru: {} za placenu postarinu", to);
        } catch (Exception ex) {
            log.warn("Neuspjelo slanje emaila za placenu postarinu.", ex);
        }
    }

    @Async
    public void sendPickupConfirmedNotificationAsync(String to, String donatorIme, String igrackaNaziv) {
        if (to == null || to.isBlank()) {
            log.warn("Email primatelja nije definiran, preskacem slanje obavijesti o preuzimanju.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            if (fromAddress != null) {
                message.setFrom(fromAddress);
            }
            message.setSubject("Preuzimanje potvrđeno - PlayForward");

            String tekst = "Poštovani,\n\n" +
                    "Donator " + (donatorIme != null ? donatorIme : "(nepoznat)") +
                    " je potvrdio da je igračka \"" + (igrackaNaziv != null ? igrackaNaziv : "(nepoznato)") +
                    "\" poslana ili preuzeta.\n\n" +
                    "Hvala što koristite PlayForward! Možete ostaviti recenziju donatoru na platformi.\n\n" +
                    "Srdačan pozdrav,\nPlayForward tim";

            message.setText(tekst);
            mailSender.send(message);
            log.info("Poslan email primatelju: {} za potvrdu preuzimanja", to);
        } catch (Exception ex) {
            log.warn("Neuspjelo slanje emaila za potvrdu preuzimanja.", ex);
        }
    }
}
