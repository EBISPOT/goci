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

    private Long merged;

    public SnpFormRow() {
    }

    public SnpFormRow(String snp, String strongestRiskAllele, String proxySnp, Long merged) {
        this.snp = snp;
        this.strongestRiskAllele = strongestRiskAllele;
        this.proxySnp = proxySnp;
        this.merged = merged;
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

    public Long getMerged() { return merged; }

    public void setMerged(Long merged) {
        this.merged = merged;
    }
}
