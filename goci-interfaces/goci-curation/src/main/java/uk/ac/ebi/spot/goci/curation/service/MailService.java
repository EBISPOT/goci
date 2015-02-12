package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Study;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by emma on 10/02/15.
 *
 * @author emma
 *         <p/>
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
        String studyTrait = study.getDiseaseTrait().getTrait();
        String studyTitle = study.getTitle();
        String pubmedLink = "http://www.ncbi.nlm.nih.gov/pubmed/" + study.getPubmedId();
        String notes = study.getHousekeeping().getNotes();
        String currentCurator = study.getHousekeeping().getCurator().getLastName();

        // Format date
        Date studyDate = study.getStudyDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String bodyStudyDate = dateFormat.format(studyDate);

        // TODO CHANGE THIS
        String editStudyLink = "http://localhost:55000/studies/" + study.getId();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(getTo());
        mailMessage.setFrom(getFrom());
        mailMessage.setSubject(study.getAuthor() + " - " + status);
        mailMessage.setText("The GWAS paper by " + study.getAuthor() + " with study date " + bodyStudyDate + " now has status " + status
                + "\n" + "Title: " + studyTitle
                + "\n" + "Trait: " + studyTrait
                + "\n" + "Pubmed link: " + pubmedLink
                + "\n" + "Edit link: " + editStudyLink
                + "\n" + "Current curator: " + currentCurator
                + "\n" + "Notes: " + notes);
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