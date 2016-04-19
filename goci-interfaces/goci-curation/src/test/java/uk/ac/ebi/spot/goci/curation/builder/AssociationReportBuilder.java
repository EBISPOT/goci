package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.AssociationReport;

/**
 * Created by emma on 09/03/2016.
 * @author emma
 *
 * Test for uk.ac.ebi.spot.goci.model.AssociationReport
 */
public class AssociationReportBuilder {

    private AssociationReport associationReport = new AssociationReport();

    public AssociationReportBuilder setSnpGeneOnDiffChr(String error){
        associationReport.setSnpGeneOnDiffChr(error);
        return this;
    }

    public AssociationReport build(){
        return associationReport;
    }
}
