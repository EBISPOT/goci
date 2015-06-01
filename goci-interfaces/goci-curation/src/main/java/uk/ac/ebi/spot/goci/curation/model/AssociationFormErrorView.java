package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 29/05/2015.
 *
 * @author emma
 *         <p>
 *         Model object used to store errors in an assoication
 */
public class AssociationFormErrorView {

    private String snpErrors;

    private String riskAlleleErrors;

    private String proxyErrors;

    // Constructor
    public AssociationFormErrorView() {

    }

    public String getSnpErrors() {
        return snpErrors;
    }

    public void setSnpErrors(String snpErrors) {
        this.snpErrors = snpErrors;
    }

    public String getRiskAlleleErrors() {
        return riskAlleleErrors;
    }

    public void setRiskAlleleErrors(String riskAlleleErrors) {
        this.riskAlleleErrors = riskAlleleErrors;
    }

    public String getProxyErrors() {
        return proxyErrors;
    }

    public void setProxyErrors(String proxyErrors) {
        this.proxyErrors = proxyErrors;
    }
}
