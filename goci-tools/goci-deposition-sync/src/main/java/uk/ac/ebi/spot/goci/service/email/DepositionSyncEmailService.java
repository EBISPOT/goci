package uk.ac.ebi.spot.goci.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.service.GOCIMailService;

@Service
public class DepositionSyncEmailService extends GOCIMailService {

    @Value("${mail.link}")
    private String link;

    @Value("${devmail.to}")
    private String devMailTo;

    @Autowired
    public DepositionSyncEmailService(JavaMailSender javaMailSender) {
        super(javaMailSender);
    }

    public void sendSubmissionImportNotification(String reportBody) {
        DepositionSyncReportEmail email = new DepositionSyncReportEmail();
        email.setTo(this.devMailTo);
        email.setLink(this.link);
        email.setFrom(this.getFrom());
        email.createBody(reportBody);
        sendEmail(email);
        sendEmail(email);
    }
}
