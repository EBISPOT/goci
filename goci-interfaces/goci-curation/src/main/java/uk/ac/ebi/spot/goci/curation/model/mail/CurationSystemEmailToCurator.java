package uk.ac.ebi.spot.goci.curation.model.mail;

import uk.ac.ebi.spot.goci.model.Study;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by emma on 08/03/2016.
 *
 * @author emma
 *         <p>
 *         Concrete implementation of emails sent to curators
 */
public class CurationSystemEmailToCurator extends CurationSystemEmail {

    public void createBody(Study study, String status) {
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

        this.setSubject(study.getAuthor() + " - " + status);
        this.setBody(
                "The GWAS paper by " + study.getAuthor() + " with study date " + bodyStudyDate + " now has status " +
                        status
                        + "\n" + "Title: " + studyTitle
                        + "\n" + "Trait: " + studyTrait
                        + "\n" + "Pubmed link: " + pubmedLink
                        + "\n" + "Edit link: " + editStudyLink
                        + "\n" + "Current curator: " + currentCurator
                        + "\n" + "Publish Date: " + bodyPublishDate
                        + "\n" + "Notes: " + notes
                        + "\n\n");
    }
}