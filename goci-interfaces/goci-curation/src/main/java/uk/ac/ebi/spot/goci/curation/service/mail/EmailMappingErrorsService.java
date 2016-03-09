package uk.ac.ebi.spot.goci.curation.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmailToCurator;
import uk.ac.ebi.spot.goci.curation.service.AssociationMappingErrorService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.Study;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

/**
 * Created by emma on 09/03/2016.
 *
 * @author emma
 */
@Service
public class EmailMappingErrorsService {

    private AssociationMappingErrorService associationMappingErrorService;

    @Autowired
    public EmailMappingErrorsService(AssociationMappingErrorService associationMappingErrorService) {
        this.associationMappingErrorService = associationMappingErrorService;
    }

    public CurationSystemEmailToCurator getMappingDetails(Study study, CurationSystemEmailToCurator email) {

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
                        email.getLink() + "associations/" + association.getId();

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

        if (mappingDetails.isEmpty()) {
            mappingDetails = "Note: No mapping errors detected for any association in this study.";
        }

        email.addToBody(mappingDetails);
        return email;
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


}
