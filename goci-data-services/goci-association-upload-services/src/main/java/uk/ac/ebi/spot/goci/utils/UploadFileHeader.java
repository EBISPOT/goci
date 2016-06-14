package uk.ac.ebi.spot.goci.utils;

/**
 * Created by emma on 09/05/2016.
 *
 * @author emma
 *         <p>
 *         Enum class to hold possible file headings
 */
public enum UploadFileHeader {
    SNP,
    CHR,
    BP,
    GENOME_BUILD,
    EFFECT_ALLELE,
    OTHER_ALLELES,
    // Curator spreadsheet = Risk element (allele, haplotype or SNPxSNP interaction) frequency in controls
    EFFECT_ELEMENT_FREQUENCY_IN_CONTROLS,
    // Curator spreadsheet = Independent SNP risk allele frequency in controls
    INDEPENDENT_SNP_EFFECT_ALLELE_FREQUENCY_IN_CONTROLS,
    GENES,
    PVALUE_MANTISSA,
    PVALUE_EXPONENT,
    PVALUE_DESCRIPTION,
    OR,
    BETA,
    BETA_UNIT,
    BETA_DIRECTION,
    STANDARD_ERROR,
    RANGE,
    PROXY_SNP,
    OR_RECIPROCAL,
    OR_RECIPROCAL_RANGE,
    DESCRIPTION,
    MULTI_SNP_HAPLOTYPE,
    SNP_INTERACTION,
    SNP_STATUS,
    SNP_TYPE,
    EFO_TRAITS,
    UNKNOWN
}