package uk.ac.ebi.spot.goci.curation.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmailToCurator;
import uk.ac.ebi.spot.goci.curation.service.AssociationMappingErrorService;
import uk.ac.ebi.spot.goci.curation.service.AssociationValidationReportService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Set;

/**
 * Created by emma on 09/03/2016.
 *
 * @author emma
 */
@Service
public class EmailMappingErrorsService {

    private AssociationValidationReportService associationValidationReportService;

    @Autowired
    public EmailMappingErrorsService(AssociationValidationReportService associationValidationReportService) {
        this.associationValidationReportService = associationValidationReportService;
    }

    public CurationSystemEmailToCurator getMappingDetails(Study study, CurationSystemEmailToCurator email) {

        String associationSummary = "";

        Collection<Association> associations = study.getAssociations();

        if (associations.isEmpty()) {
            associationSummary = "No associations for this study";
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
                        email.getLink() + "associations/" + association.getId();

                Set<String> validationWarnings = associationValidationReportService.getWarningSet(association.getId());
                String validationWarningsForEmailBody = formatWarnings(validationWarnings);

                // Only include details of associations with errors
                // In future we may want to include all association details can remove this if condition
                if (!validationWarningsForEmailBody.contains("No validation warnings found")) {
                    associationSummary = associationSummary + "Association: " + associationLink + "\n"
                            + "Validation Results: " + validationWarningsForEmailBody
                            + "Last Mapping Date: " + mappingDate + "\n"
                            + "Last Mapping Performed By: " + performer + "\n\n";
                }
            }
        }

        if (associationSummary.isEmpty()) {
            associationSummary = "Note: No validation warnings detected for any association in this study.";
        }

        email.addToBody(associationSummary);
        return email;
    }

    // Format the errors to include in the email
    private String formatWarnings(Set<String> warnings) {

        String formattedWarnings = "";

        // Format errors
        if (!warnings.isEmpty()) {
            for (String warning : warnings) {
                formattedWarnings = formattedWarnings.concat(warning).concat("\n");
            }
        }
        else {
            formattedWarnings = "No validation warnings found" + "\n";
        }

        return formattedWarnings;
    }
}