package uk.ac.ebi.spot.goci.curation.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;

/**
 * Created by emma on 25/11/2015.
 *
 * @author emma
 *         <p>
 *         Custom validator for SnpFormColumn object, that checks for fields left blank by curators
 */
public class SnpFormColumnValidator implements Validator {
    @Override public boolean supports(Class clazz) {
        return SnpFormColumn.class.equals(clazz);
    }

    @Override public void validate(Object obj, Errors e) {
        SnpFormColumn col = (SnpFormColumn) obj;
        if (col.getSnp().isEmpty()) {
            e.rejectValue("snpFormColumns", "No SNP in column", "Please do not leave SNP field blank");
        }

        if (col.getStrongestRiskAllele().isEmpty()) {
            e.rejectValue("snpFormColumns",
                          "No Strongest SNP-Risk Allele in column",
                          "Please do not leave Strongest SNP-Risk Allele field blank");
        }
    }
}
