package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.Study;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

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

    private AssociationMappingErrorService associationMappingErrorService;

    // Reading these from application.properties
    @Value("${mail.from}")
    private String from;
    @Value("${mail.to}")
    private String to;
    @Value("${mail.link}")
    private String link;
    @Value("${devmail.to}")
    private String devMailTo;

    @Autowired
    public MailService(JavaMailSender javaMailSender,
                       AssociationMappingErrorService associationMappingErrorService) {
        this.javaMailSender = javaMailSender;
        this.associationMappingErrorService = associationMappingErrorService;
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

        // Format dates
        Date studyDate = study.getPublicationDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String bodyStudyDate = dateFormat.format(studyDate);

        Date publishDate = study.getHousekeeping().getCatalogPublishDate();
        String bodyPublishDate = null;
        if (publishDate != null) {
            bodyPublishDate = dateFormat.format(publishDate);
        }

        String editStudyLink = getLink() + "studies/" + study.getId();

        String mappingDetails = getMappingDetails(study);
        if (mappingDetails.isEmpty()) {
            mappingDetails = "Note: No mapping errors detected for any association in this study.";
        }

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
                        + "\n" + "Publish Date: " + bodyPublishDate
                        + "\n" + "Notes: " + notes
                        + "\n\n" +
                        mappingDetails);
        javaMailSender.send(mailMessage);
    }

    private String getMappingDetails(Study study) {

        String mappingDetails = "";

        Collection<Association> associations = study.getAssociations();

        if (associations.isEmpty()) {
            mappingDetails = "No associations for this study";
        }
        else {

            for (Association association : associations) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String mappingDate = "";
                String performer = "";

                if (association.getLastMappingDate() != null) {
                    mappingDate = dateFormat.format(association.getLastMappingDate());
                }

                if (association.getLastMappingPerformedBy() != null) {
                    performer = association.getLastMappingPerformedBy();
                }

                String associationLink =
                        getLink() + "associations/" + association.getId();

                AssociationReport report = association.getAssociationReport();
                Map<String, String> associationErrorMap =
                        associationMappingErrorService.createAssociationErrorMap(report);
                String errors = formatErrors(associationErrorMap);

                // Only include details of associations with errors
                // In future we may want to include all association details can remove this if condition
                if (!errors.contains("No mapping errors found")) {
                    mappingDetails = mappingDetails + "Association: " + associationLink + "\n"
                            + "Last Mapping Date: " + mappingDate + "\n"
                            + "Last Mapping Performed By: " + performer + "\n"
                            + "Mapping errors: " + errors + "\n";
                }
            }
        }


        return mappingDetails;
    }

    // Format the errors to include in the email
    private String formatErrors(Map<String, String> map) {

        String errors = "";

        // Format errors
        if (!map.isEmpty()) {
            for (String key : map.keySet()) {
                errors = errors + map.get(key) + "\n";
            }
        }
        else {
            errors = "No mapping errors found" + "\n";
        }

        return errors;
    }

    public void sendReleaseChangeEmail(Integer currentEnsemblReleaseNumberInDatabase, int latestEnsemblReleaseNumber) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(getTo());
        mailMessage.setFrom(getFrom());
        mailMessage.setSubject("New Ensembl Release Identified");
        mailMessage.setText(
                "The latest Ensembl release is number "
                        + latestEnsemblReleaseNumber
                        + "."
                        + "\n"
                        + "The GWAS catalog is mapped to Ensembl release "
                        + currentEnsemblReleaseNumberInDatabase
                        + "."
                        + "\n\n"
                        + "All associations will now be remapped to the latest Ensembl release.");
        javaMailSender.send(mailMessage);
    }


    /**
     * Send notification to dev list if no Ensembl release can be identified by nightly checks
     */
    public void sendReleaseNotIdentifiedProblem() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(getDevMailTo());
        mailMessage.setFrom(getFrom());
        mailMessage.setSubject("Problem Determining Latest Ensembl Release");
        mailMessage.setText(
                "The latest Ensembl release cannot be identified from REST API. Please check logs");
        javaMailSender.send(mailMessage);
    }

    public void sendEnsemblPingFailureMail() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(getDevMailTo());
        mailMessage.setFrom(getFrom());
        mailMessage.setSubject("Ensembl Daily Ping Failed");
        mailMessage.setText(
                "Daily ping of Ensembl API failed. Please check logs.");
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDevMailTo() {
        return devMailTo;
    }

    public void setDevMailTo(String devMailTo) {
        this.devMailTo = devMailTo;
    }


}