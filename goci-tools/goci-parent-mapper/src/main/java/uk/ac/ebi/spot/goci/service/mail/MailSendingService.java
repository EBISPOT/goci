package uk.ac.ebi.spot.goci.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.mail.ParentMappingReportEmail;
import uk.ac.ebi.spot.goci.service.GOCIMailService;

import java.util.List;

@Service
public class MailSendingService extends GOCIMailService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public MailSendingService(JavaMailSender javaMailSender) {
        super(javaMailSender);
    }

    public void sendReportEmail(List<String> unmappedTerms){
        ParentMappingReportEmail email = new ParentMappingReportEmail();
        email.setTo("gwas-curator@ebi.ac.uk");
        email.setFrom(this.getFrom());
        email.createBody(unmappedTerms);
        sendEmail(email);
        getLog().info("Email sent");
    }
}
