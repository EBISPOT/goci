package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.util.Collection;

/**
 * Created by emma on 08/04/2016.
 *
 * @author emma
 *         <p>
 *         Risk allele builder used during testing
 */
public class RiskAlleleBuilder {

    private RiskAllele riskAllele = new RiskAllele();

    public RiskAlleleBuilder setId(Long id) {
        riskAllele.setId(id);
        return this;
    }

    public RiskAlleleBuilder setRiskAlleleName(String riskAlleleName) {
        riskAllele.setRiskAlleleName(riskAlleleName);
        return this;
    }

    public RiskAlleleBuilder setRiskFrequency(String riskFrequency) {
        riskAllele.setRiskFrequency(riskFrequency);
        return this;
    }

    public RiskAlleleBuilder setGenomeWide(Boolean genomeWide) {
        riskAllele.setGenomeWide(genomeWide);
        return this;
    }

    public RiskAlleleBuilder setLimitedList(Boolean limitedList) {
        riskAllele.setLimitedList(limitedList);
        return this;
    }

    public RiskAlleleBuilder setSnp(SingleNucleotidePolymorphism snp) {
        riskAllele.setSnp(snp);
        return this;
    }

    public RiskAlleleBuilder setProxySnps(Collection<SingleNucleotidePolymorphism> proxySnps) {
        riskAllele.setProxySnps(proxySnps);
        return this;
    }

    public RiskAlleleBuilder setLoci(Collection<Locus> loci) {
        riskAllele.setLoci(loci);
        return this;
    }

    public RiskAllele build() {
        return riskAllele;
    }
}