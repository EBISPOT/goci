package uk.ac.ebi.spot.goci.curation.model;

import java.util.Collection;

/**
 * Created by emma on 12/02/15.
 *
 * @author emma
 *         <p/>
 *         Service class to deal with columns used by curators to enter snp/association details for interaction studies
 */
public class SnpFormColumn {

    private String snp;

    private String strongestRiskAllele;

    private String riskFrequency;

    private Collection<String> authorReportedGenes;

    // Constructors
    public SnpFormColumn() {
    }

    public SnpFormColumn(String snp, String strongestRiskAllele, String riskFrequency, Collection<String> authorReportedGenes) {
        this.snp = snp;
        this.strongestRiskAllele = strongestRiskAllele;
        this.riskFrequency = riskFrequency;
        this.authorReportedGenes = authorReportedGenes;
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

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public void setRiskFrequency(String riskFrequency) {
        this.riskFrequency = riskFrequency;
    }

    public Collection<String> getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public void setAuthorReportedGenes(Collection<String> authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
    }
}
