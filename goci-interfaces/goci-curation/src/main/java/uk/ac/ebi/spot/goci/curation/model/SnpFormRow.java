package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 26/01/15.
 *
 * @author emma
 *         <p/>
 *         Service class that deals with rows on SNP association form
 */
public class SnpFormRow {

    private String snp;

    private String strongestRiskAllele;

    private String proxySnp;

    public SnpFormRow() {
    }

    public SnpFormRow(String snp, String strongestRiskAllele, String proxySnp) {
        this.snp = snp;
        this.strongestRiskAllele = strongestRiskAllele;
        this.proxySnp = proxySnp;
    }

    public String getSnp() {
        return snp;
    }

    public void setSnp(String snp) {
        this.snp = snp;
    }

    public String getStrongestRiskAllele() {
        return strongestRiskAllele;
    }

    public void setStrongestRiskAllele(String strongestRiskAllele) {
        this.strongestRiskAllele = strongestRiskAllele;
    }

    public String getProxySnp() {
        return proxySnp;
    }

    public void setProxySnp(String proxySnp) {
        this.proxySnp = proxySnp;
    }
}
