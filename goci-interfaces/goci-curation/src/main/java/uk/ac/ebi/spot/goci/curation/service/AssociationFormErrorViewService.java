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
                String error = checkSnpOrRiskAllele(snpFormRow.getSnp());
                snpError = snpError+error;
            }

            associationErrorView.setSnpError(snpError);
        }
        return associationErrorView;
    }

    public String checkSnpOrRiskAllele(String snpValue) {

        String error = "";
        if (snpValue.contains(",")) {
            error = "SNP " + snpValue + " contains a comma\n";
        }
        if (snpValue.contains("x")) {
            error = error + "SNP " + snpValue + " contains an x character\n";
        }
        if (snpValue.contains("X")) {
            error = error + "SNP " + snpValue + " contains an X character\n";
        }

        if (snpValue.contains(":")) {
            error = error + "SNP " + snpValue + "contains a :\n";
        }
        else {
            error = "No errors\n";
        }

        return error;
    }
}
