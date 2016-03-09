package uk.ac.ebi.spot.goci.curation.model.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.ac.ebi.spot.goci.curation.service.AssociationMappingErrorService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.Study;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by emma on 08/03/2016.
 *
 * @author emma
 *         <p>
 *         Concrete implementation of emails sent to curators
 */
public class CurationSystemEmailToCurator extends CurationSystemEmail {

    @Value("${mail.to}")
    private String curatorTo;

    @Autowired
    private AssociationMappingErrorService associationMappingErrorService;

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

        String mappingDetails = getMappingDetails(study);
        if (mappingDetails.isEmpty()) {
            mappingDetails = "Note: No mapping errors detected for any association in this study.";
        }

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
                        + "\n\n" +
                        mappingDetails);
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

    @Override void setTo(String to) {
        this.curatorTo = to;
    }
}
