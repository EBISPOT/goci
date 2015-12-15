package uk.ac.ebi.spot.goci.curation.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;

/**
 * Created by emma on 25/11/2015.
 *
 * @author emma
 *         <p>
 *         Custom validator for SnpFormRow object, that checks for fields left blank by curators
 */
@Service
public class SnpFormRowValidator implements Validator {

    public boolean supports(Class clazz) {
        return SnpFormRow.class.equals(clazz);
    }

    public void validate(Object obj, Errors e) {
        SnpFormRow row = (SnpFormRow) obj;
        if (row.getSnp().isEmpty()) {
            e.rejectValue("snpFormRows", "No SNP in row", "Please do not leave SNP field blank");
        }

        if (row.getStrongestRiskAllele().isEmpty()) {
            e.rejectValue("snpFormRows",
                          "No Strongest SNP-Risk Allele in row",
                          "Please do not leave Strongest SNP-Risk Allele field blank");
        }
    }
}
