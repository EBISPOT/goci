package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.AssociationFormErrorView;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;

/**
 * Created by emma on 29/05/2015.
 */
@Service
public class AssociationFormErrorViewService {

    public AssociationFormErrorViewService() {
    }

    public AssociationFormErrorView checkFormForErrors(SnpAssociationForm snpAssociationForm) {
        AssociationFormErrorView associationErrorView = new AssociationFormErrorView();
        String snpError = "SNP errors: ";

        for (SnpFormRow snpFormRow : snpAssociationForm.getSnpFormRows()) {

            // Check SNPs
            if (snpFormRow.getSnp() != null && !snpFormRow.getSnp().isEmpty()) {
                if (snpFormRow.getSnp().contains(",")) {
                    snpError = snpError + "SNP " + snpFormRow.getSnp() + " contains a comma\n";
                }
                else if (snpFormRow.getSnp().contains("x")) {
                    snpError = snpError + "SNP " + snpFormRow.getSnp() + " contains an x character\n";
                }
                else if (snpFormRow.getSnp().contains("X")) {
                    snpError = snpError + "SNP " + snpFormRow.getSnp() + " contains an X character\n";
                }

                else if (snpFormRow.getSnp().contains(":")) {
                    snpError = snpError + "SNP " + snpFormRow.getSnp() + "contains a :\n";
                }
                else {
                    snpError = snpError + "No errors\n";
                }

            }

            associationErrorView.setSnpError(snpError);
        }
        return associationErrorView;
    }
}
