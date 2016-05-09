package uk.ac.ebi.spot.goci.utils;

/**
 * Created by emma on 09/05/2016.
 *
 * @author emma
 *         <p>
 *         Enum class to hold possible file headings Based on speci for author submission spreadsheet
 */
public enum UploadFileHeader {
    SNP_ID,
    CHR,
    BP,
    GENOME_BUILD,
    EFFECT_ALLELE,
    OTHER_ALLELES,
    EFFECT_ALLELE_FREQUENCY_IN_CONTROLS,
    GENES,
    PVALUE_MANTISSA,
    PVALUE_EXPONENT,
    OR,
    BETA,
    BETA_UNIT,
    BETA_DIRECTION,
    STANDARD_ERROR,
    RANGE,
    DESCRIPTION,
    UNKNOWN
}