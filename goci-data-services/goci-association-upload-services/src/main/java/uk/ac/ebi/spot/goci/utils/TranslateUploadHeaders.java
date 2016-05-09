package uk.ac.ebi.spot.goci.utils;

import org.springframework.stereotype.Service;

/**
 * Created by emma on 09/05/2016.
 *
 * @author emma
 *         <p>
 *         Service class to translate headers in upload file to enum value
 */
@Service
public class TranslateUploadHeaders {

    public UploadFileHeader translateToEnumValue(String value) {

        value = value.toLowerCase();
        UploadFileHeader enumValue;

        if (value.startsWith("snp id")) {
            enumValue = UploadFileHeader.SNP_ID;
        }
        else if (value.startsWith("chr")) {
            enumValue = UploadFileHeader.CHR;
        }
        else if (value.startsWith("bp")) {
            enumValue = UploadFileHeader.BP;
        }
        else if (value.startsWith("genome")) {
            enumValue = UploadFileHeader.GENOME_BUILD;
        }
        else if (value.startsWith("effect allele")) {
            enumValue = UploadFileHeader.EFFECT_ALLELE;
        }
        else if (value.startsWith("other alleles")) {
            enumValue = UploadFileHeader.OTHER_ALLELES;
        }
        else if (value.startsWith("effect allele frequency in controls")) {
            enumValue = UploadFileHeader.EFFECT_ALLELE_FREQUENCY_IN_CONTROLS;
        }
        else if (value.startsWith("p-value mantissa")) {
            enumValue = UploadFileHeader.PVALUE_MANTISSA;
        }
        else if (value.startsWith("p-value exponent")) {
            enumValue = UploadFileHeader.PVALUE_EXPONENT;
        }
        else if (value.startsWith("or")) {
            enumValue = UploadFileHeader.OR;
        }
        else if (value.startsWith("beta")) {
            enumValue = UploadFileHeader.BETA;
        }
        else if (value.startsWith("beta unit")) {
            enumValue = UploadFileHeader.BETA_UNIT;
        }
        else if (value.startsWith("beta direction")) {
            enumValue = UploadFileHeader.BETA_DIRECTION;
        }
        else if (value.startsWith("OR/beta SE")) {
            enumValue = UploadFileHeader.STANDARD_ERROR;
        }
        else if (value.startsWith("OR/beta range")) {
            enumValue = UploadFileHeader.RANGE;
        }
        else if (value.startsWith("Association description")) {
            enumValue = UploadFileHeader.DESCRIPTION;
        }
        else {
            enumValue = UploadFileHeader.UNKNOWN;
        }

        return enumValue;
    }
}
