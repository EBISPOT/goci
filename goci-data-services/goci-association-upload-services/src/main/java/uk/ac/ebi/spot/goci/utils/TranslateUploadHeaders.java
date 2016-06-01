package uk.ac.ebi.spot.goci.utils;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Created by emma on 09/05/2016.
 *
 * @author emma
 *         <p>
 *         Service class to translate headers in upload file to enum value
 */
@Service
@Lazy
public class TranslateUploadHeaders {

    public UploadFileHeader translateToEnumValue(String value) {

        value = value.toLowerCase();
        UploadFileHeader enumValue;

        if (value.equals("snp id(ideally rsid)(mandatory)")) {
            enumValue = UploadFileHeader.SNP_ID;
        }
        else if (value.equals("chr(optional)")) {
            enumValue = UploadFileHeader.CHR;
        }
        else if (value.equals("bp(optional)")) {
            enumValue = UploadFileHeader.BP;
        }
        else if (value.equals("genome build(optional)")) {
            enumValue = UploadFileHeader.GENOME_BUILD;
        }
        else if (value.equals("effect allele(optional)")) {
            enumValue = UploadFileHeader.EFFECT_ALLELE;
        }
        else if (value.equals("other alleles(optional)")) {
            enumValue = UploadFileHeader.OTHER_ALLELES;
        }
        else if (value.startsWith("effect allele frequency")) {
            enumValue = UploadFileHeader.EFFECT_ALLELE_FREQUENCY_IN_CONTROLS;
        }
        else if (value.equals("p-value mantissa(mandatory)")) {
            enumValue = UploadFileHeader.PVALUE_MANTISSA;
        }
        else if (value.equals("p-value exponent(mandatory)")) {
            enumValue = UploadFileHeader.PVALUE_EXPONENT;
        }
        else if (value.equals("or(optional)")) {
            enumValue = UploadFileHeader.OR;
        }
        else if (value.equals("beta(optional)")) {
            enumValue = UploadFileHeader.BETA;
        }
        else if (value.startsWith("beta unit")) {
            enumValue = UploadFileHeader.BETA_UNIT;
        }
        else if (value.startsWith("beta direction")) {
            enumValue = UploadFileHeader.BETA_DIRECTION;
        }
        else if (value.equals("or/beta se(optional)")) {
            enumValue = UploadFileHeader.STANDARD_ERROR;
        }
        else if (value.startsWith("or/beta range")) {
            enumValue = UploadFileHeader.RANGE;
        }
        else if (value.equals("association description(optional)")) {
            enumValue = UploadFileHeader.PVALUE_DESCRIPTION;
        }
        else {
            enumValue = UploadFileHeader.UNKNOWN;
        }
        return enumValue;
    }
}