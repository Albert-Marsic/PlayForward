package PlayForward.demo.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReviewNotification(String toEmail, Integer ocjena, String tekst) {
        if (toEmail == null || toEmail.isBlank()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Nova recenzija na PlayForward");
        message.setText("Primili ste novu recenziju.\n\nOcjena: " + ocjena + "\nKomentar: " + tekst);

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            logger.warn("Neuspjelo slanje e-mail obavijesti za recenziju: {}", ex.getMessage());
        }
    }
}
