package uk.ac.ebi.spot.goci.service.email;

import uk.ac.ebi.spot.goci.model.GenericEmail;

public class DepositionSyncReportEmail extends GenericEmail {

    public void createBody(String reportBody) {
        this.setSubject("Deposition sync report");
        this.setBody(reportBody);
    }

}
