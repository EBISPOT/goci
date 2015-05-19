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

    private Collection<String> proxies;

    // Constructors
    public SnpFormColumn() {
    }

    public SnpFormColumn(String snp,
                         String strongestRiskAllele,
                         String riskFrequency,
                         Collection<String> authorReportedGenes, Collection<String> proxies) {
        this.snp = snp;
        this.strongestRiskAllele = strongestRiskAllele;
        this.riskFrequency = riskFrequency;
        this.authorReportedGenes = authorReportedGenes;
        this.proxies = proxies;
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

    public Collection<String> getProxies() {
        return proxies;
    }

    public SnpFormColumn setProxies(Collection<String> proxies) {
        this.proxies = proxies;
        return this;
    }
}
