package uk.ac.ebi.spot.goci.curation.model;

import java.util.HashMap;
import java.util.Map;

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

    private Map<String, String> associationErrorMap = new HashMap<>();

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

    public Map<String, String> getAssociationErrorMap() {
        return associationErrorMap;
    }

    public void setAssociationErrorMap(Map<String, String> associationErrorMap) {
        this.associationErrorMap = associationErrorMap;
    }
}
