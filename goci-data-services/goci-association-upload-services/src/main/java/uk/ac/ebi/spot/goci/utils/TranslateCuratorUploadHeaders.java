package uk.ac.ebi.spot.goci.utils;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Created by emma on 10/06/2016.
 *
 * @author emma
 *         <p>
 *         Service class to translate headers in curator upload file to enum value
 */
@Service
@Lazy
public class TranslateCuratorUploadHeaders implements TranslateUploadHeaders {
    @Override public UploadFileHeader translateToEnumValue(String value) {

        UploadFileHeader enumValue;

        if (value.equals("Gene(s)")) {
            enumValue = UploadFileHeader.GENES;
        }
        else if (value.equals("Strongest SNP-Risk Allele")) {
            enumValue = UploadFileHeader.EFFECT_ALLELE;
        }
        else if (value.equals("SNP")) {
            enumValue = UploadFileHeader.SNP;
        }
        else if (value.equals("Proxy SNP")) {
            enumValue = UploadFileHeader.PROXY_SNP;
        }
        else if (value.equals("Independent SNP risk allele frequency in controls")) {
            enumValue = UploadFileHeader.INDEPENDENT_SNP_EFFECT_ALLELE_FREQUENCY_IN_CONTROLS;
        }
        else if (value.equals("Risk element (allele, haplotype or SNPxSNP interaction) frequency in controls")) {
            enumValue = UploadFileHeader.EFFECT_ELEMENT_FREQUENCY_IN_CONTROLS;
        }
        else if (value.equals("P-value mantissa")) {
            enumValue = UploadFileHeader.PVALUE_MANTISSA;
        }
        else if (value.equals("P-value exponent")) {
            enumValue = UploadFileHeader.PVALUE_EXPONENT;
        }
        else if (value.equals("P-value description")) {
            enumValue = UploadFileHeader.PVALUE_DESCRIPTION;
        }
        else if (value.equals("OR")) {
            enumValue = UploadFileHeader.OR;
        }
        else if (value.equals("OR reciprocal")) {
            enumValue = UploadFileHeader.OR_RECIPROCAL;
        }
        else if (value.equals("Beta")) {
            enumValue = UploadFileHeader.BETA;
        }
        else if (value.equals("Beta unit")) {
            enumValue = UploadFileHeader.BETA_UNIT;
        }
        else if (value.equals("Beta direction")) {
            enumValue = UploadFileHeader.BETA_DIRECTION;
        }
        else if (value.equals("Range")) {
            enumValue = UploadFileHeader.RANGE;
        }
        else if (value.equals("OR reciprocal range")) {
            enumValue = UploadFileHeader.OR_RECIPROCAL_RANGE;
        }
        else if (value.equals("Standard Error")) {
            enumValue = UploadFileHeader.STANDARD_ERROR;
        }
        else if (value.equals("OR/Beta description")) {
            enumValue = UploadFileHeader.DESCRIPTION;
        }
        else if (value.startsWith("Multi-SNP Haplotype")) {
            enumValue = UploadFileHeader.MULTI_SNP_HAPLOTYPE;
        }
        else if (value.startsWith("SNP:SNP interaction")) {
            enumValue = UploadFileHeader.SNP_INTERACTION;
        }
        else if (value.equals("SNP Status")) {
            enumValue = UploadFileHeader.SNP_STATUS;
        }
        else if (value.startsWith("SNP type")) {
            enumValue = UploadFileHeader.SNP_TYPE;
        }
        else if (value.equals("EFO traits")) {
            enumValue = UploadFileHeader.EFO_TRAITS;
        }
        else {
            enumValue = UploadFileHeader.UNKNOWN;
        }
        return enumValue;
    }
}
