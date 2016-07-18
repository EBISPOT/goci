package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.AssociationReport;

import java.util.Date;

/**
 * Created by emma on 09/03/2016.
 *
 * @author emma
 *         <p>
 *         Test for uk.ac.ebi.spot.goci.model.AssociationReport
 */
public class AssociationReportBuilder {

    private AssociationReport associationReport = new AssociationReport();

    public AssociationReportBuilder setSnpGeneOnDiffChr(String error) {
        associationReport.setSnpGeneOnDiffChr(error);
        return this;
    }

    public AssociationReportBuilder setErrorCheckedByCurator(Boolean errorCheckedByCurator) {
        associationReport.setErrorCheckedByCurator(errorCheckedByCurator);
        return this;
    }

    public AssociationReportBuilder setLastUpdateDate(Date lastUpdateDate) {
        associationReport.setLastUpdateDate(lastUpdateDate);
        return this;
    }

    public AssociationReport build() {
        return associationReport;
    }
}
