package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.StudyAuditView;
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
 *         Provides email notification to curators
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

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
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
        Date studyDate = study.getPublicationDate();
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
    public void sendDailyAuditEmail(Collection<StudyAuditView> studiesWithNcbiErrors,
                                    Integer totalStudiesWithNcbiErrors,
                                    Integer totalStudiesWithImportErrors,
                                    Integer totalNumberOfStudiesSentToNcbi,
                                    Collection<StudyAuditView> studiesSentToNcbi) {

        // Create email body
        String emailBody = "";

        // If we have errors, construct body of email
        if (!studiesWithNcbiErrors.isEmpty()) {
            for (StudyAuditView studyWithNcbiError : studiesWithNcbiErrors) {

                // General information
                String title = "Title: " + studyWithNcbiError.getTitle() + "\n";
                String author = "Author: " + studyWithNcbiError.getAuthor() + "\n";
                String pubmedId = "Pubmed Id: " + studyWithNcbiError.getPubmedId() + "\n";

                // Dates
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String sendToNCBIDate = "";
                if (studyWithNcbiError.getSendToNCBIDate() != null) {
                    sendToNCBIDate = df.format(studyWithNcbiError.getSendToNCBIDate());
                }
                String sendToNCBIDateBody = "Send To NCBI date: " + sendToNCBIDate + "\n";

                String studyDate = "";
                if (studyWithNcbiError.getPublicationDate() != null) {
                    studyDate = df.format(studyWithNcbiError.getPublicationDate());
                }
                String studyDateBody = "Study Date: " + studyDate + "\n";

                // Pubmed error
                String pubmedIdErrorFound = "No";
                if (studyWithNcbiError.getPubmedIdError() != null) {
                    pubmedIdErrorFound = "Yes";
                }
                String pubmedErrorBody = "Pubmed ID error: " + pubmedIdErrorFound + "\n";

                // SNP error
                String snpErrors = "";
                if (!studyWithNcbiError.getSnpErrors().isEmpty()) {
                    snpErrors = studyWithNcbiError.getSnpErrors().toString();
                    snpErrors = createErrorForEmail(snpErrors);
                }
                else {snpErrors = "none";}
                String snpErrorBody = "SNP Error(s): " + snpErrors + "\n";

                // Gene Not On Genome Error
                String geneNotOnGenomeErrors = "";
                if (!studyWithNcbiError.getGeneNotOnGenomeErrors().isEmpty()) {
                    geneNotOnGenomeErrors = studyWithNcbiError.getGeneNotOnGenomeErrors().toString();
                    geneNotOnGenomeErrors = createErrorForEmail(geneNotOnGenomeErrors);
                }
                else {geneNotOnGenomeErrors = "none";}
                String geneNotOnGenomeErrorsBody = "Gene Not On Genome Error(s): " + geneNotOnGenomeErrors + "\n";

                // SNP Gene On Different Chromosome Error
                String snpGeneOnDiffChrErrors = "";
                if (!studyWithNcbiError.getSnpGeneOnDiffChrErrors().isEmpty()) {
                    snpGeneOnDiffChrErrors = studyWithNcbiError.getSnpGeneOnDiffChrErrors().toString();
                    snpGeneOnDiffChrErrors = createErrorForEmail(snpGeneOnDiffChrErrors);
                }
                else {snpGeneOnDiffChrErrors = "none";}
                String snpGeneOnDiffChrErrorsBody =
                        "SNP Gene On Different Chromosome Error(s): " + snpGeneOnDiffChrErrors + "\n";

                // No Gene For Symbol Error
                String noGeneForSymbolErrors = "";
                if (!studyWithNcbiError.getNoGeneForSymbolErrors().isEmpty()) {
                    noGeneForSymbolErrors = studyWithNcbiError.getNoGeneForSymbolErrors().toString();
                    noGeneForSymbolErrors = createErrorForEmail(noGeneForSymbolErrors);
                }
                else {noGeneForSymbolErrors = "none";}
                String noGeneForSymbolErrorsBody = "No Gene For Symbol Error(s): " + noGeneForSymbolErrors + "\n";

                // Edit link
                Long studyId = studyWithNcbiError.getStudyId();
                String editStudyLink = "Edit link: http://garfield.ebi.ac.uk:8080/gwas/curation/studies/" + studyId;

                // Create email body
                emailBody = emailBody + "\n" + title + author + studyDateBody + sendToNCBIDateBody + pubmedId +
                        pubmedErrorBody +
                        snpErrorBody +
                        geneNotOnGenomeErrorsBody + snpGeneOnDiffChrErrorsBody + noGeneForSymbolErrorsBody +
                        editStudyLink +
                        "\n";
            }
        }

        else {
            emailBody = "\nNo errors found\n";
        }

        // Create summary view of studies sent to NCBI
        String sentToNcbiSummary = "";
        if (!studiesSentToNcbi.isEmpty()) {
            for (StudyAuditView studySentToNcbi : studiesSentToNcbi) {
                String title = studySentToNcbi.getTitle();
                String author = studySentToNcbi.getAuthor();
                String pubmedId = studySentToNcbi.getPubmedId();

                // Edit link
                Long studyId = studySentToNcbi.getStudyId();
                String editStudyLink = "Edit link: http://garfield.ebi.ac.uk:8080/gwas/curation/studies/" + studyId;

                sentToNcbiSummary =
                        sentToNcbiSummary + "\n" + title + "\t" + author + "\t" + pubmedId + "\t" + editStudyLink +
                                "\n";

            }
        }

        else {sentToNcbiSummary = "No studies sent to NCBI";}


        // Format mail message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(getTo());
        mailMessage.setFrom(getFrom());
        mailMessage.setSubject("GWAS Curation daily audit report");
        mailMessage.setText(
                "\nSummary of studies sent to NCBI for mapping: " + "\n" +
                        "Total number of studies with status 'Send To NCBI' before pipeline ran (see details below): " +
                        totalNumberOfStudiesSentToNcbi + "\n"
                        + sentToNcbiSummary
                        + "\n\nSummary of errors:\n" +
                        "Total number of studies with data import errors: " + totalStudiesWithImportErrors +
                        "\n" + "Total number of studies with NCBI pipeline errors (see details below): " +
                        totalStudiesWithNcbiErrors + "\n" + emailBody);

        getLog().info("Sending daily audit email");
        javaMailSender.send(mailMessage);
    }


    // Format text for email
    private String createErrorForEmail(String errorString) {
        String emailString = errorString;
        emailString = emailString.replaceAll("\\[", "");
        emailString = emailString.replaceAll("]", "");
        emailString = emailString.replaceAll(", ", "\n");
        return emailString;
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