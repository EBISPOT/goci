package uk.ac.ebi.spot.goci.curation.model.mail;

import uk.ac.ebi.spot.goci.curation.service.deposition.ImportLog;
import uk.ac.ebi.spot.goci.model.GenericEmail;

public class SubmissionImportEmailToDevelopers extends GenericEmail {

    public void createBody(boolean outcome, String pmid, String submissionID, ImportLog importLog) {
        this.setSubject("Submission import [" + submissionID + " | " + pmid + "] - outcome: " + outcome);
        this.setBody(" - Submission: " + submissionID + "\n" +
                " - PMID: " + pmid + "\n" +
                " - Outcome: " + outcome + "\n" +
                " -------------------------- \n" +
                importLog.pretty(false));
    }
}
