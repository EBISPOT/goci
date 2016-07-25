package uk.ac.ebi.spot.goci.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.PublishedStudy;
import uk.ac.ebi.spot.goci.model.mail.ReleaseSummaryEmail;

import java.util.List;

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

    private final JavaMailSender javaMailSender;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public MailSendingService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendSummaryEmail(List<PublishedStudy> studies){
        ReleaseSummaryEmail email = new ReleaseSummaryEmail();
        email.setTo(this.to);
        email.setFrom(this.from);
        email.createBody(studies);
        sendEmail(email);
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
