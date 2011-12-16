package uk.ac.ebi.fgpt.goci.web.view;

/**
 * A simple bean that encapsulates the data sent in a request to curate a study
 *
 * @author Tony Burdett
 * @date 26/10/11
 */
public class CurationRequestBean {
    private String updatedState;
    private String updatedEligibility;
    private String updatedOwner;


    private String restApiKey;

    private CurationRequestBean() {
        // default constructor should be private, allows JSON deserialization whilst preventing instantiation
    }

    public CurationRequestBean(String updatedState, String updatedEligibility, String updatedOwner, String restApiKey) {
        this.updatedState = updatedState;
        this.updatedEligibility = updatedEligibility;
        this.updatedOwner = updatedOwner;
        this.restApiKey = restApiKey;
    }

    public String getUpdatedState() {
        return updatedState;
    }

    public void setUpdatedState(String updatedState) {
        this.updatedState = updatedState;
    }

    public String getUpdatedEligibility() {
        return updatedEligibility;
    }

    public void setUpdatedEligibility(String updatedEligibility) {
        this.updatedEligibility = updatedEligibility;
    }

    public String getUpdatedOwner() {
        return updatedOwner;
    }

    public void setUpdatedOwner(String updatedOwner) {
        this.updatedOwner = updatedOwner;
    }

    public String getRestApiKey() {
        return restApiKey;
    }

    public void setRestApiKey(String restApiKey) {
        this.restApiKey = restApiKey;
    }

    @Override public String toString() {
        return "CurationRequestBean{" +
                "updatedState='" + updatedState + '\'' +
                ", updatedEligibility='" + updatedEligibility + '\'' +
                ", updatedOwner='" + updatedOwner + '\'' +
                ", restApiKey='" + restApiKey + '\'' +
                '}';
    }
}
