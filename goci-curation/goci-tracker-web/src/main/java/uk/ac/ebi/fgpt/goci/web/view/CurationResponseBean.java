package uk.ac.ebi.fgpt.goci.web.view;

/**
 * A simple bean indicating whether a submission request was successful, and if so returns the created task.
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
public class CurationResponseBean {
    private final boolean operationSuccessful;
    private final String statusMessage;
    private final String curatedStudyID;

    public CurationResponseBean(boolean operationSuccessful,
                                String statusMessage,
                                String curatedStudyID) {
        this.operationSuccessful = operationSuccessful;
        this.statusMessage = statusMessage;
        this.curatedStudyID = curatedStudyID;
    }

    public boolean isOperationSuccessful() {
        return operationSuccessful;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getCuratedStudyID() {
        return curatedStudyID;
    }
}
