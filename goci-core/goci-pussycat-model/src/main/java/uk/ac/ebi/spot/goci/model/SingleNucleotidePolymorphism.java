package uk.ac.ebi.spot.goci.model;

/**
 * A simple object modelling a SNP, as represented by the data in the GWAS catalog database.  THis simply wraps up the
 * RS ID (SNP representative ID from dbSNP), the name of the chromosome on which this SNP is found, the cytogenetic band
 * in which the SNP is located, and the absolute position (in base pairs) of the SNP in the genome.
 *
 * @author Tony Burdett
 * Date 24/01/12
 */
public interface SingleNucleotidePolymorphism extends GWASObject {
    /**
     * The rsID of this SNP, as assigned by dbSNP
     *
     * @return a snp identifier (should be unique)
     */
    String getRSID();

    /**
     * The name of the chromosome on which this SNP is located
     *
     * @return the chromosome name
     */
    String getChromosomeName();

    /**
     * The cytogenetic band in which this SNP is located
     *
     * @return the band name
     */
    String getCytogeneticBandName();

    /**
     * The location, in base pairs, of this SNP in the genome
     *
     * @return the SNP location
     */
    String getSNPLocation();
}
