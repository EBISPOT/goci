package uk.ac.ebi.spot.goci.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.PublishedStudy;
import uk.ac.ebi.spot.goci.model.mail.ReleaseSummaryEmail;
import uk.ac.ebi.spot.goci.service.GOCIMailService;

import java.util.List;

/**
 * Created by dwelter on 29/06/16.
 * @Cinzia: goci-email-service.
 */

@Service
public class MailSendingService extends GOCIMailService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public MailSendingService(JavaMailSender javaMailSender) {
        super(javaMailSender);
    }

    public void sendSummaryEmail(List<PublishedStudy> studies){
        ReleaseSummaryEmail email = new ReleaseSummaryEmail();
        email.setTo(this.getTo());
        email.setFrom(this.getFrom());
        email.createBody(studies);
        sendEmail(email);
        getLog().info("Email sent");
    }



}
