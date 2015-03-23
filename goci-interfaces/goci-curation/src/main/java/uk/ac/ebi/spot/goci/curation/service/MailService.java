package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.StudyErrorView;
import uk.ac.ebi.spot.goci.model.Study;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 10/02/15.
 *
 * @author emma
 *         <p>
 *         Provides email notification
 */
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    // Reading these from application.properties
    @Value("${mail.from}")
    private String from;
    @Value("${mail.to}")
    private String to;

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmailNotification(Study study, String status) {

        // Set up some of the values used in mail body
        String studyTitle = study.getTitle();
        String pubmedLink = "http://europepmc.org/abstract/MED/" + study.getPubmedId();
        String currentCurator = study.getHousekeeping().getCurator().getLastName();

        // These could be null so catch this case
        String studyTrait = null;
        if (study.getDiseaseTrait() != null && !study.getDiseaseTrait().getTrait().isEmpty()) {
            studyTrait = study.getDiseaseTrait().getTrait();
        }

        String notes = null;
        if (study.getHousekeeping().getNotes() != null && !study.getHousekeeping().getNotes().isEmpty()) {
            notes = study.getHousekeeping().getNotes();
        }

        // Format date
        Date studyDate = study.getStudyDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String bodyStudyDate = dateFormat.format(studyDate);

        String editStudyLink = "http://garfield.ebi.ac.uk:8080/gwas/curation/studies/" + study.getId();

        // Format mail message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(getTo());
        mailMessage.setFrom(getFrom());
        mailMessage.setSubject(study.getAuthor() + " - " + status);
        mailMessage.setText(
                "The GWAS paper by " + study.getAuthor() + " with study date " + bodyStudyDate + " now has status " +
                        status
                        + "\n" + "Title: " + studyTitle
                        + "\n" + "Trait: " + studyTrait
                        + "\n" + "Pubmed link: " + pubmedLink
                        + "\n" + "Edit link: " + editStudyLink
                        + "\n" + "Current curator: " + currentCurator
                        + "\n" + "Notes: " + notes);
        javaMailSender.send(mailMessage);

    }

    // Send single email with all study errors
    public void sendDailyAuditEmail(Collection<StudyErrorView> studyErrorViews) {

        // Create email body
        String emailBody = "";

        for (StudyErrorView studyErrorView : studyErrorViews) {

            // Title
            String title = "Title: " + studyErrorView.getTitle() + "\n";

            // Date
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String sendToNCBIDate = "";
            if (studyErrorView.getSendToNCBIDate() != null) {
                sendToNCBIDate = df.format(studyErrorView.getSendToNCBIDate());
            }
            String sendToNCBIDateBody = "Send To NCBI date: " + sendToNCBIDate + "\n";

            // Pubmed error
            String pubmedIdErrorFound = "No";
            if (studyErrorView.getPubmedIdError() != null) {
                pubmedIdErrorFound = "Yes";
            }
            String pubmedErrorBody = "Pubmed ID error: " + pubmedIdErrorFound + "\n";

            // SNP error
            String snpErrors = "";
            if (!studyErrorView.getSnpErrors().isEmpty()) {
                snpErrors = studyErrorView.getSnpErrors().toString();
            }
            else {snpErrors = "none";}
            String snpErrorBody = "SNP Error(s): " + snpErrors + "\n";

            // Gene Not On Genome Error
            String geneNotOnGenomeErrors = "";
            if (!studyErrorView.getGeneNotOnGenomeErrors().isEmpty()) {
                geneNotOnGenomeErrors = studyErrorView.getGeneNotOnGenomeErrors().toString();
            }
            else {geneNotOnGenomeErrors = "none";}
            String geneNotOnGenomeErrorsBody = "Gene Not On Genome Error(s): " + geneNotOnGenomeErrors + "\n";

            // SNP Gene On Different Chromosome Error
            String snpGeneOnDiffChrErrors = "";
            if (!studyErrorView.getSnpGeneOnDiffChrErrors().isEmpty()) {
                snpGeneOnDiffChrErrors = studyErrorView.getSnpGeneOnDiffChrErrors().toString();
            }
            else {snpGeneOnDiffChrErrors = "none";}
            String snpGeneOnDiffChrErrorsBody =
                    "SNP Gene On Different Chromosome Error(s): " + snpGeneOnDiffChrErrors + "\n";

            // No Gene For Symbol Error
            String noGeneForSymbolErrors = "";
            if (!studyErrorView.getNoGeneForSymbolErrors().isEmpty()) {
                noGeneForSymbolErrors = studyErrorView.getNoGeneForSymbolErrors().toString();
            }
            else {noGeneForSymbolErrors = "none";}
            String noGeneForSymbolErrorsBody = "No Gene For Symbol Error(s): " + noGeneForSymbolErrors + "\n";

            // Edit link
            Long studyId = studyErrorView.getStudyId();
            String editStudyLink = "Edit link: http://garfield.ebi.ac.uk:8080/gwas/curation/studies/" + studyId;

            // Create email body
            emailBody = emailBody + "\n" + title + sendToNCBIDateBody + pubmedErrorBody + snpErrorBody +
                    geneNotOnGenomeErrorsBody + snpGeneOnDiffChrErrorsBody + noGeneForSymbolErrorsBody + editStudyLink +
                    "\n";
        }

        // Format mail message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(getTo());
        mailMessage.setFrom(getFrom());
        mailMessage.setSubject("GWAS NCBI DataFlow Audit Trail");
        mailMessage.setText("The following studies have status NCBI pipeline error:" + "\n"
                                    + emailBody);

        javaMailSender.send(mailMessage);
    }

    // Getter and setters
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}