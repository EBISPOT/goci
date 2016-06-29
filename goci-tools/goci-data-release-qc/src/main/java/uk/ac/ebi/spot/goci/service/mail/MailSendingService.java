package uk.ac.ebi.spot.goci.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.mail.ReleaseSummaryEmail;

/**
 * Created by dwelter on 29/06/16.
 */

@Service
public class MailSendingService {


    // Reading these from application.properties
    @Value("${mail.from}")
    private String from;
    @Value("${mail.to}")
    private String to;
    @Value("${mail.link}")
    private String link;
    @Value("${devmail.to}")
    private String devMailTo;

    private final JavaMailSender javaMailSender;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public MailSendingService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    private void sendEmail(ReleaseSummaryEmail email) {
        // Format mail message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email.getTo());
        mailMessage.setFrom(email.getFrom());
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getBody());
        javaMailSender.send(mailMessage);
        getLog().info("Email sent");
    }
}
