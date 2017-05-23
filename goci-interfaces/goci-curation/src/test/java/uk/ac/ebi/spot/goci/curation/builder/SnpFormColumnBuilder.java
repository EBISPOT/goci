package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;

import java.util.Collection;

/**
 * Created by emma on 15/07/2016.
 *
 * @author emma
 *         <p>
 *         Builder for SnpFormColumn
 */
public class SnpFormColumnBuilder {

    private SnpFormColumn snpFormColumn = new SnpFormColumn();

    public SnpFormColumnBuilder setAuthorReportedGenes(Collection<String> authorReportedGenes) {
        snpFormColumn.setAuthorReportedGenes(authorReportedGenes);
        return this;
    }

    public SnpFormColumnBuilder setGenomeWide(Boolean genomeWide) {
        snpFormColumn.setGenomeWide(genomeWide);
        return this;
    }

    public SnpFormColumnBuilder setLimitedList(Boolean limitedList) {
        snpFormColumn.setLimitedList(limitedList);
        return this;
    }

    public SnpFormColumnBuilder setProxySnps(Collection<String> proxySnps) {
        snpFormColumn.setProxySnps(proxySnps);
        return this;
    }

    public SnpFormColumnBuilder setRiskFrequency(String riskFrequency) {
        snpFormColumn.setRiskFrequency(riskFrequency);
        return this;
    }

    public SnpFormColumnBuilder setSnp(String snp) {
        snpFormColumn.setSnp(snp);
        return this;
    }

    public SnpFormColumnBuilder setStrongestRiskAllele(String strongestRiskAllele) {
        snpFormColumn.setStrongestRiskAllele(strongestRiskAllele);
        return this;
    }

    public SnpFormColumn build() {
        return snpFormColumn;
    }
}