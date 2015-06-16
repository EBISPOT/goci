package uk.ac.ebi.spot.goci.curation.model;

import java.util.Collection;

/**
 * Created by emma on 12/02/15.
 *
 * @author emma
 *         <p>
 *         Service class to deal with columns used by curators to enter snp/association details for interaction studies
 */
public class SnpFormColumn {

    private String snp;

    private String strongestRiskAllele;

    private String riskFrequency;

    private Collection<String> authorReportedGenes;

    private Collection<String> proxySnps;

    private Boolean genomeWide = false;

    private Boolean limitedList = false;

    // Constructors
    public SnpFormColumn() {
    }

    public SnpFormColumn(String snp,
                         String strongestRiskAllele,
                         String riskFrequency,
                         Collection<String> authorReportedGenes,
                         Collection<String> proxySnps,
                         Boolean genomeWide,
                         Boolean limitedList) {
        this.snp = snp;
        this.strongestRiskAllele = strongestRiskAllele;
        this.riskFrequency = riskFrequency;
        this.authorReportedGenes = authorReportedGenes;
        this.proxySnps = proxySnps;
        this.genomeWide = genomeWide;
        this.limitedList = limitedList;
    }

    public String getSnp() {
        return snp;
    }

    public SnpFormColumn setSnp(String snp) {
        this.snp = snp;
        return this;
    }

    public String getStrongestRiskAllele() {
        return strongestRiskAllele;
    }

    public SnpFormColumn setStrongestRiskAllele(String strongestRiskAllele) {
        this.strongestRiskAllele = strongestRiskAllele;
        return this;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public SnpFormColumn setRiskFrequency(String riskFrequency) {
        this.riskFrequency = riskFrequency;
        return this;
    }

    public Collection<String> getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public SnpFormColumn setAuthorReportedGenes(Collection<String> authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
        return this;
    }


    public Collection<String> getProxySnps() {
        return proxySnps;
    }

    public void setProxySnps(Collection<String> proxySnps) {
        this.proxySnps = proxySnps;
    }

    public Boolean getGenomeWide() {
        return genomeWide;
    }

    public void setGenomeWide(Boolean genomeWide) {
        this.genomeWide = genomeWide;
    }

    public Boolean getLimitedList() {
        return limitedList;
    }

    public void setLimitedList(Boolean limitedList) {
        this.limitedList = limitedList;
    }
}
