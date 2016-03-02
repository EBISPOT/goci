package uk.ac.ebi.spot.goci.curation.model;

import java.util.Collection;

/**
 * Created by emma on 26/01/15.
 *
 * @author emma
 *         <p>
 *         Service class that deals with rows on SNP association form
 */
public class SnpFormRow {

    private String snp;

    private String strongestRiskAllele;

    private Collection<String> proxySnps;

//    private Long merged;

    public SnpFormRow() {
    }

    public SnpFormRow(String snp, String strongestRiskAllele, Collection<String> proxySnps) {
        this.snp = snp;
        this.strongestRiskAllele = strongestRiskAllele;
        this.proxySnps = proxySnps;
//        this.merged = merged;
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

    public Collection<String> getProxySnps() {
        return proxySnps;
    }

    public void setProxySnps(Collection<String> proxySnps) {
        this.proxySnps = proxySnps;
    }

//    public Long getMerged() { return merged; }
//
//    public void setMerged(Long merged) {
//        this.merged = merged;
//    }
}
