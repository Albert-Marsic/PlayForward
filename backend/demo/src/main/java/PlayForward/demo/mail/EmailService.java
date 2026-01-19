package PlayForward.demo.mail;

import PlayForward.demo.review.Recenzija;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.from:}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = (fromAddress == null || fromAddress.isBlank())
                ? null
                : fromAddress.trim();
    }

    public void sendReviewNotification(String to, Recenzija recenzija) {
        if (to == null || to.isBlank()) {
            throw new RuntimeException("Email primatelja nije definiran.");
        }
        if (recenzija == null) {
            throw new RuntimeException("Recenzija nije dostupna.");
        }

        String igrackaNaziv = recenzija.getZahtjev() != null
                && recenzija.getZahtjev().getIgracka() != null
                ? recenzija.getZahtjev().getIgracka().getNaziv()
                : "Nepoznato";

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
                "Komentar:\n" + recenzija.getTekst() + "\n"
        );

        mailSender.send(message);
    }
}
