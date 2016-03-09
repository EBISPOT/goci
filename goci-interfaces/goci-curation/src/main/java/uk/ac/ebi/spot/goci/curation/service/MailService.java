package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmail;
import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmailToCurator;
import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmailToDevelopers;
import uk.ac.ebi.spot.goci.model.Study;

/**
 * Created by emma on 10/02/15.
 *
 * @author emma
 *         <p>
 *         Provides email notification to curators and developers
 */
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Send notification to curators when a study status changes
     *
     * @param study  study details
     * @param status status of study
     */
    public void sendEmailNotification(Study study, String status) {

        CurationSystemEmailToCurator email = new CurationSystemEmailToCurator();
        email.createBody(study, status);
        sendEmail(email);
    }

    /**
     * Send notification to dev list if new Ensembl release identified by nightly checks
     *
     * @param currentEnsemblReleaseNumberInDatabase current release number in database
     * @param latestEnsemblReleaseNumber            new release number from Ensembl
     */
    public void sendReleaseChangeEmail(Integer currentEnsemblReleaseNumberInDatabase, int latestEnsemblReleaseNumber) {

        CurationSystemEmailToDevelopers email = new CurationSystemEmailToDevelopers();
        email.createReleaseChangeEmail(currentEnsemblReleaseNumberInDatabase, latestEnsemblReleaseNumber);
        sendEmail(email);
    }

    /**
     * Send notification to dev list if no Ensembl release can be identified by nightly checks
     */
    public void sendReleaseNotIdentifiedProblem() {

        CurationSystemEmailToDevelopers email = new CurationSystemEmailToDevelopers();
        email.createReleaseNotIdentifiedProblem();
        sendEmail(email);
    }

    /**
     * Send notification to dev list if Ensembl API is down
     */
    public void sendEnsemblPingFailureMail() {

        CurationSystemEmailToDevelopers email = new CurationSystemEmailToDevelopers();
        email.createEnsemblPingFailureMail();
        sendEmail(email);
    }

    private void sendEmail(CurationSystemEmail email) {
        // Format mail message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email.getTo());
        mailMessage.setFrom(email.getFrom());
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getBody());
        javaMailSender.send(mailMessage);
    }

}