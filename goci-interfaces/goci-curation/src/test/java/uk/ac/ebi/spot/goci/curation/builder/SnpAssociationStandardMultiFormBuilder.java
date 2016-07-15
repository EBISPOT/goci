package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.curation.model.SnpAssociationStandardMultiForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;

import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 15/07/2016.
 *
 * @author emma
 *         <p>
 *         SnpAssociationStandardMultiForm builder used during testing
 */
public class SnpAssociationStandardMultiFormBuilder {

    private SnpAssociationStandardMultiForm snpAssociationStandardMultiForm = new SnpAssociationStandardMultiForm();

    public SnpAssociationStandardMultiFormBuilder setSnpFormRows(List<SnpFormRow> snpFormRows) {
        snpAssociationStandardMultiForm.setSnpFormRows(snpFormRows);
        return this;
    }

    public SnpAssociationStandardMultiFormBuilder setAuthorReportedGenes(Collection<String> authorReportedGenes) {
        snpAssociationStandardMultiForm.setAuthorReportedGenes(authorReportedGenes);
        return this;
    }

    public SnpAssociationStandardMultiFormBuilder setMultiSnpHaplotypeDescr(String multiSnpHaplotypeDescr) {
        snpAssociationStandardMultiForm.setMultiSnpHaplotypeDescr(multiSnpHaplotypeDescr);
        return this;
    }

    public SnpAssociationStandardMultiFormBuilder setMultiSnpHaplotypeNum(Integer multiSnpHaplotypeNum) {
        snpAssociationStandardMultiForm.setMultiSnpHaplotypeNum(multiSnpHaplotypeNum);
        return this;
    }

    public SnpAssociationStandardMultiFormBuilder setMultiSnpHaplotype(Boolean multiSnpHaplotype) {
        snpAssociationStandardMultiForm.setMultiSnpHaplotype(multiSnpHaplotype);
        return this;
    }

    public SnpAssociationStandardMultiForm build() {
        return snpAssociationStandardMultiForm;
    }
}
