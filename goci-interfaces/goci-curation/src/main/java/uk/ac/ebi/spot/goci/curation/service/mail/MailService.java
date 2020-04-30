package uk.ac.ebi.spot.goci.curation.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmailToCurator;
import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmailToDevelopers;
import uk.ac.ebi.spot.goci.curation.model.mail.NcbiExportEmailToDevelopers;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.service.GOCIMailService;

/**
 * Created by emma on 10/02/15.
 *
 * @author emma
 *         <p>
 *         Provides email notification to curators and developers
 */
@Service
public class MailService extends GOCIMailService {

    // Reading these from application.properties.
    // Extended from GOCIMailService
    @Value("${mail.link}")
    private String link;
    @Value("${devmail.to}")
    private String devMailTo;


    private EmailMappingErrorsService emailMappingErrorsService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public MailService(JavaMailSender javaMailSender,
                       EmailMappingErrorsService emailMappingErrorsService) {
        super(javaMailSender);
        this.emailMappingErrorsService = emailMappingErrorsService;
    }

    /**
     * Send notification to curators when a study status changes,
     * this method will be run in a new thread
     *
     * @param study  study details
     * @param status status of study
     */
    public void sendEmailNotification(Study study, String status) {

        getLog().info("Sending email for study ".concat(String.valueOf(study.getId())));
        CurationSystemEmailToCurator email = new CurationSystemEmailToCurator();
        email.setTo(this.getTo());
        email.setLink(this.link);
        email.setFrom(this.getFrom());
        email.createBody(study, status);
        email = emailMappingErrorsService.getMappingDetails(study, email);
        sendEmail(email);
    }

    /**
     * Send notification to dev list if new Ensembl release identified by nightly checks
     *
     * @param currentEnsemblReleaseNumberInDatabase current release number in database
     * @param latestEnsemblReleaseNumber            new release number from Ensembl
     */
    public void sendReleaseChangeEmail(Integer currentEnsemblReleaseNumberInDatabase, int latestEnsemblReleaseNumber,
                                       String ensemblRestServer, String ensemblDbVersion) {

        CurationSystemEmailToDevelopers email = new CurationSystemEmailToDevelopers();
        email.setTo(this.devMailTo);
        email.setLink(this.link);
        email.setFrom(this.getFrom());
        email.createReleaseChangeEmail(currentEnsemblReleaseNumberInDatabase, latestEnsemblReleaseNumber,
                ensemblRestServer, ensemblDbVersion);
        sendEmail(email);
    }

    /**
     * Send notification to dev list if no Ensembl release can be identified by nightly checks
     */
    public void sendReleaseNotIdentifiedProblem() {

        CurationSystemEmailToDevelopers email = new CurationSystemEmailToDevelopers();
        email.setTo(this.devMailTo);
        email.setLink(this.link);
        email.setFrom(this.getFrom());
        email.createReleaseNotIdentifiedProblem();
        sendEmail(email);
    }

    /**
     * Send notification to dev list if Ensembl API is down
     */
    public void sendEnsemblPingFailureMail() {

        CurationSystemEmailToDevelopers email = new CurationSystemEmailToDevelopers();
        email.setTo(this.devMailTo);
        email.setLink(this.link);
        email.setFrom(this.getFrom());
        email.createEnsemblPingFailureMail();
        sendEmail(email);
    }

    public void sendNcbiFTPUploadEmail(String subject) {
        NcbiExportEmailToDevelopers email = new NcbiExportEmailToDevelopers();
        email.setTo(this.devMailTo);
        email.setLink(this.link);
        email.setFrom(this.getFrom());
        email.createNCBIFTPEmail(subject);
        sendEmail(email);

    }
}