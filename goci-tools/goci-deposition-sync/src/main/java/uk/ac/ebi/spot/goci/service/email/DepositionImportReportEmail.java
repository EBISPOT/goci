package uk.ac.ebi.spot.goci.service.email;

import uk.ac.ebi.spot.goci.model.GenericEmail;

public class DepositionImportReportEmail extends GenericEmail {

    public void createBody(String reportBody) {
        this.setSubject("Deposition sync: Import report");
        this.setBody(reportBody);
    }

}
