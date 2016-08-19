package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;

import java.util.Collection;

/**
 * Created by emma on 15/07/2016.
 *
 * @author emma
 *         <p>
 *         Builder for SnpFormRow
 */
public class SnpFormRowBuilder {

    private SnpFormRow snpFormRow = new SnpFormRow();

    public SnpFormRowBuilder setSnp(String snp) {
        snpFormRow.setSnp(snp);
        return this;
    }

    public SnpFormRowBuilder setStrongestRiskAllele(String strongestRiskAllele) {
        snpFormRow.setStrongestRiskAllele(strongestRiskAllele);
        return this;
    }

    public SnpFormRowBuilder setProxySnps(Collection<String> proxySnps) {
        snpFormRow.setProxySnps(proxySnps);
        return this;
    }

    public SnpFormRow build() {
        return snpFormRow;
    }
}