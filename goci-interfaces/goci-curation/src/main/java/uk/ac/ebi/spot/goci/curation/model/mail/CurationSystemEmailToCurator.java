package uk.ac.ebi.spot.goci.curation.model.mail;

import uk.ac.ebi.spot.goci.model.GenericEmail;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyNote;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 08/03/2016.
 *
 * @author emma
 *         <p>
 *         Concrete implementation of emails sent to curators
 */
public class CurationSystemEmailToCurator extends GenericEmail {

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

        StringBuilder notes = new StringBuilder();
        Collection<StudyNote> studyNotes = study.getNotes();
        if (!studyNotes.isEmpty()) {
            studyNotes.forEach(studyNote -> {
                notes.append(studyNote.toStringForEamil()).append("\n");
                notes.append("-------------------------------------------------------------\n\n");
            });
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
                        + "\n" + "Notes: \n" + notes
                        + "\n\n");
    }
}