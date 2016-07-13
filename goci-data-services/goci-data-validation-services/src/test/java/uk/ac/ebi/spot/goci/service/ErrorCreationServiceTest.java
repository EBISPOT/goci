package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.component.ValidationChecks;
import uk.ac.ebi.spot.goci.model.ValidationError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 21/04/2016.
 *
 * @author emma
 *         <p>
 *         Test of ErrorCreationService
 */
@RunWith(MockitoJUnitRunner.class)
public class ErrorCreationServiceTest {

    private ErrorCreationService errorCreationService;

    @Mock
    private ValidationChecks validationChecks;

    @Before
    public void setUp() throws Exception {
        errorCreationService = new ErrorCreationService(validationChecks);
    }

    @Test
    public void testCheckSnpValueIsPresent() throws Exception {
        when(validationChecks.checkValueIsPresent("")).thenReturn("Value is empty");
        ValidationError error1 = errorCreationService.checkSnpValueIsPresent("");
        assertThat(error1).extracting("field", "error", "warning").contains("SNP", "Value is empty", false);

        when(validationChecks.checkValueIsPresent("rs123")).thenReturn(null);
        ValidationError error2 = errorCreationService.checkSnpValueIsPresent("rs123");
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckStrongestAlleleValueIsPresent() throws Exception {
        when(validationChecks.checkValueIsPresent("")).thenReturn("Value is empty");
        ValidationError error1 =
                errorCreationService.checkStrongestAlleleValueIsPresent("");
        assertThat(error1).extracting("field", "error", "warning").contains("Risk Allele", "Value is empty", false);

        when(validationChecks.checkValueIsPresent("rs123-?")).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkStrongestAlleleValueIsPresent("rs123-?");
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckSnpType() throws Exception {
        when(validationChecks.checkSnpType("bigger")).thenReturn("Value does not contain novel or known");
        ValidationError error1 = errorCreationService.checkSnpType("bigger");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("SNP type", "Value does not contain novel or known", false);

        when(validationChecks.checkSnpType("novel")).thenReturn(null);
        ValidationError error2 = errorCreationService.checkSnpType("novel");
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckOrIsPresentAndMoreThanOne() throws Exception {

        when(validationChecks.checkOrIsPresentAndMoreThanOne((float) 0.5)).thenReturn(
                "Value is less than 1");
        ValidationError error1 = errorCreationService.checkOrIsPresentAndMoreThanOne((float) 0.5);
        assertThat(error1).extracting("field", "error", "warning")
                .contains("OR", "Value is less than 1", false);

        when(validationChecks.checkOrIsPresentAndMoreThanOne((float) 1.23)).thenReturn(null);
        ValidationError error2 = errorCreationService.checkOrIsPresentAndMoreThanOne((float) 1.23);
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckOrRecipIsPresentAndLessThanOne() throws Exception {
        when(validationChecks.checkOrRecipIsPresentAndLessThanOne((float) 10.23)).thenReturn(
                "Value is more than 1");
        ValidationError error1 =
                errorCreationService.checkOrRecipIsPresentAndLessThanOne((float) 10.23);
        assertThat(error1).extracting("field", "error", "warning")
                .contains("OR reciprocal", "Value is more than 1", false);

        when(validationChecks.checkOrRecipIsPresentAndLessThanOne((float) 0.5)).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkOrRecipIsPresentAndLessThanOne((float) 0.5);
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckBetaValuesIsEmpty() throws Exception {
        when(validationChecks.checkValueIsEmpty(Matchers.anyFloat())).thenReturn("Value is not empty");
        ValidationError error1 = errorCreationService.checkBetaValuesIsEmpty(Matchers.anyFloat());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Beta", "Value is not empty", false);

        Float nullValue = null;
        when(validationChecks.checkValueIsEmpty(nullValue)).thenReturn(null);
        ValidationError error2 = errorCreationService.checkBetaValuesIsEmpty(nullValue);
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckBetaUnitIsEmpty() throws Exception {
        when(validationChecks.checkValueIsEmpty(Matchers.anyString())).thenReturn("Value is not empty");
        ValidationError error1 = errorCreationService.checkBetaUnitIsEmpty(Matchers.anyString());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Beta Unit", "Value is not empty", false);

        when(validationChecks.checkValueIsEmpty("")).thenReturn(null);
        ValidationError error2 = errorCreationService.checkBetaUnitIsEmpty("");
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckBetaDirectionIsEmpty() throws Exception {
        when(validationChecks.checkValueIsEmpty(Matchers.anyString())).thenReturn(
                "Value is not empty");
        ValidationError error1 = errorCreationService.checkBetaDirectionIsEmpty(Matchers.anyString());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Beta Direction", "Value is not empty", false);

        when(validationChecks.checkValueIsEmpty("")).thenReturn(null);
        ValidationError error2 = errorCreationService.checkBetaDirectionIsEmpty("");
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckBetaIsPresentAndIsNotNegative() throws Exception {
        when(validationChecks.checkBetaIsPresentAndIsNotNegative((float) -0.8)).thenReturn(
                "Value is less than 0");
        ValidationError error1 = errorCreationService.checkBetaIsPresentAndIsNotNegative((float) -0.8);
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Beta", "Value is less than 0", false);

        when(validationChecks.checkBetaIsPresentAndIsNotNegative((float) 0.6)).thenReturn(null);
        ValidationError error2 = errorCreationService.checkBetaIsPresentAndIsNotNegative((float) 0.6);
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckBetaUnitIsPresent() throws Exception {
        when(validationChecks.checkValueIsPresent("")).thenReturn(
                "Value is empty");
        ValidationError error1 = errorCreationService.checkBetaUnitIsPresent("");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Beta Unit", "Value is empty", false);

        when(validationChecks.checkValueIsPresent("cm")).thenReturn(null);
        ValidationError error2 = errorCreationService.checkBetaUnitIsPresent("cm");
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckBetaDirectionIsPresent() throws Exception {
        when(validationChecks.checkBetaDirectionIsPresent("greater than")).thenReturn(
                "Value is not increase or decrease");
        ValidationError error1 = errorCreationService.checkBetaDirectionIsPresent("greater than");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Beta Direction", "Value is not increase or decrease", false);

        when(validationChecks.checkBetaDirectionIsPresent("increase")).thenReturn(null);
        ValidationError error2 = errorCreationService.checkBetaDirectionIsPresent("increase");
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckOrEmpty() throws Exception {
        when(validationChecks.checkValueIsEmpty(Matchers.anyFloat())).thenReturn(
                "Value is not empty");
        ValidationError error1 = errorCreationService.checkOrEmpty(Matchers.anyFloat());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("OR", "Value is not empty", false);

        Float nullValue = null;
        when(validationChecks.checkValueIsEmpty(nullValue)).thenReturn(null);
        ValidationError error2 = errorCreationService.checkOrEmpty(nullValue);
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckOrRecipEmpty() throws Exception {
        when(validationChecks.checkValueIsEmpty(Matchers.anyFloat())).thenReturn(
                "Value is not empty");
        ValidationError error1 = errorCreationService.checkOrRecipEmpty(Matchers.anyFloat());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("OR reciprocal", "Value is not empty", false);

        Float nullValue = null;
        when(validationChecks.checkValueIsEmpty(nullValue)).thenReturn(null);
        ValidationError error2 = errorCreationService.checkOrRecipEmpty(nullValue);
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckOrPerCopyRecipRangeIsEmpty() throws Exception {
        when(validationChecks.checkValueIsEmpty(Matchers.anyString())).thenReturn("Value is not empty");
        ValidationError error1 =
                errorCreationService.checkOrPerCopyRecipRangeIsEmpty(Matchers.anyString());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("OR reciprocal range", "Value is not empty", false);

        when(validationChecks.checkValueIsEmpty("")).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkOrPerCopyRecipRangeIsEmpty("");
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckRangeIsEmpty() throws Exception {
        when(validationChecks.checkValueIsEmpty(Matchers.anyString())).thenReturn("Value is not empty");
        ValidationError error1 = errorCreationService.checkRangeIsEmpty(Matchers.anyString());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Range", "Value is not empty", false);

        String nullRange = null;
        when(validationChecks.checkValueIsEmpty(nullRange)).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkRangeIsEmpty(nullRange);
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckStandardErrorIsEmpty() throws Exception {
        when(validationChecks.checkValueIsEmpty(Matchers.anyFloat())).thenReturn(
                "Value is not empty");
        ValidationError error1 =
                errorCreationService.checkStandardErrorIsEmpty(Matchers.anyFloat());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Standard Error", "Value is not empty", false);

        Float nullNum = null;
        when(validationChecks.checkValueIsEmpty(nullNum)).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkStandardErrorIsEmpty(nullNum);
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckDescriptionIsEmpty() throws Exception {
        when(validationChecks.checkValueIsEmpty(Matchers.anyString())).thenReturn("Value is not empty");
        ValidationError error1 = errorCreationService.checkDescriptionIsEmpty(Matchers.anyString());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("OR/Beta description", "Value is not empty", false);

        String nullValue = null;
        when(validationChecks.checkValueIsEmpty(nullValue)).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkDescriptionIsEmpty(nullValue);
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckMantissaIsLessThan10() throws Exception {
        when(validationChecks.checkMantissaIsLessThan10(25)).thenReturn(
                "Value not valid i.e. greater than 9");
        ValidationError error1 =
                errorCreationService.checkMantissaIsLessThan10(25);
        assertThat(error1).extracting("field", "error", "warning")
                .contains("P-value Mantissa", "Value not valid i.e. greater than 9", false);

        when(validationChecks.checkMantissaIsLessThan10(6)).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkMantissaIsLessThan10(6);
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckExponentIsPresent() throws Exception {
        when(validationChecks.checkExponentIsPresent(null)).thenReturn(
                "Value is empty");
        ValidationError error1 =
                errorCreationService.checkExponentIsPresent(null);
        assertThat(error1).extracting("field", "error", "warning")
                .contains("P-value exponent", "Value is empty", false);

        when(validationChecks.checkExponentIsPresent(Matchers.anyInt())).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkExponentIsPresent(Matchers.anyInt());
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckPvalueDescriptionIsPresent() throws Exception {
        when(validationChecks.checkValueIsPresent("")).thenReturn(
                "Value is empty");
        ValidationError error1 =
                errorCreationService.checkPvalueDescriptionIsPresent("");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("P-value description", "Value is empty", true);

        when(validationChecks.checkValueIsPresent(Matchers.anyString())).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkPvalueDescriptionIsPresent(Matchers.anyString());
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckGene() throws Exception {
        when(validationChecks.checkGene("testX")).thenReturn("Gene synbol testX is not valid");
        ValidationError error1 = errorCreationService.checkGene("testX");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Gene", "Gene synbol testX is not valid", true);

        when(validationChecks.checkGene("SFRP1")).thenReturn(null);
        ValidationError error2 = errorCreationService.checkGene("SFRP1");
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);

        when(validationChecks.checkGene("")).thenReturn("Gene name is empty");
        ValidationError error3 = errorCreationService.checkGene("");
        assertThat(error3).extracting("field", "error", "warning").contains("Gene", "Gene name is empty", true);
    }

    @Test
    public void testCheckRiskAllele() throws Exception {
        when(validationChecks.checkRiskAllele("CHR4456:89")).thenReturn("Value does not start with rs or contain -");
        ValidationError error1 =
                errorCreationService.checkRiskAllele("CHR4456:89");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Risk Allele", "Value does not start with rs or contain -", true);

        when(validationChecks.checkRiskAllele("")).thenReturn("Value is empty");
        ValidationError error2 =
                errorCreationService.checkRiskAllele("");
        assertThat(error2).extracting("field", "error", "warning")
                .contains("Risk Allele", "Value is empty", false);

        when(validationChecks.checkRiskAllele("rs1234-?")).thenReturn(null);
        ValidationError error3 =
                errorCreationService.checkRiskAllele("rs1234-?");
        assertThat(error3).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckSnp() throws Exception {
        when(validationChecks.checkSnp("kgp4567")).thenReturn("SNP identifier kgp4567 is not valid");
        ValidationError error1 =
                errorCreationService.checkSnp("kgp4567");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("SNP", "SNP identifier kgp4567 is not valid", true);

        when(validationChecks.checkSnp("")).thenReturn("SNP identifier is empty");
        ValidationError error2 =
                errorCreationService.checkSnp("");
        assertThat(error2).extracting("field", "error", "warning")
                .contains("SNP", "SNP identifier is empty", false);

        when(validationChecks.checkSnp("rs1234")).thenReturn(null);
        ValidationError error3 =
                errorCreationService.checkSnp("rs1234");
        assertThat(error3).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckAssociationRiskFrequency() throws Exception {
        when(validationChecks.checkRiskFrequency("10")).thenReturn("Value is invalid, value is not between 0 and 1");
        ValidationError error1 =
                errorCreationService.checkAssociationRiskFrequency("10");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Risk element (allele, haplotype or SNPxSNP interaction) frequency in controls",
                          "Value is invalid, value is not between 0 and 1",
                          false);

        when(validationChecks.checkRiskFrequency("")).thenReturn("Value is empty");
        ValidationError error2 =
                errorCreationService.checkAssociationRiskFrequency("");
        assertThat(error2).extracting("field", "error", "warning")
                .contains("Risk element (allele, haplotype or SNPxSNP interaction) frequency in controls",
                          "Value is empty",
                          false);

        when(validationChecks.checkRiskFrequency("0.78")).thenReturn(null);
        ValidationError error3 =
                errorCreationService.checkAssociationRiskFrequency("0.78");
        assertThat(error3).extracting("field", "error", "warning")
                .contains(null,
                          null,
                          false);
    }

    @Test
    public void testCheckAlleleRiskFrequency() throws Exception {
        when(validationChecks.checkRiskFrequency("10")).thenReturn("Value is invalid, value is not between 0 and 1");
        ValidationError error1 =
                errorCreationService.checkAlleleRiskFrequency("10");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Independent SNP risk allele frequency in controls",
                          "Value is invalid, value is not between 0 and 1",
                          false);

        when(validationChecks.checkRiskFrequency("")).thenReturn("Value is empty");
        ValidationError error2 =
                errorCreationService.checkAlleleRiskFrequency("");
        assertThat(error2).extracting("field", "error", "warning")
                .contains("Independent SNP risk allele frequency in controls",
                          "Value is empty",
                          false);

        when(validationChecks.checkRiskFrequency("0.78")).thenReturn(null);
        ValidationError error3 =
                errorCreationService.checkAlleleRiskFrequency("0.78");
        assertThat(error3).extracting("field", "error", "warning")
                .contains(null,
                          null,
                          false);
    }

    @Test
    public void testCheckRangeIsPresent() throws Exception {
        when(validationChecks.checkValueIsPresent("")).thenReturn("Value is empty");
        ValidationError error1 =
                errorCreationService.checkRangeIsPresent("");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Range",
                          "Value is empty",
                          false);

        when(validationChecks.checkValueIsPresent(Matchers.anyString())).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkRangeIsPresent(Matchers.anyString());
        assertThat(error2).extracting("field", "error", "warning")
                .contains(null,
                          null,
                          false);
    }

    @Test
    public void testCheckOrPerCopyRecipRangeIsPresent() throws Exception {
        when(validationChecks.checkValueIsPresent("")).thenReturn("Value is empty");
        ValidationError error1 =
                errorCreationService.checkOrPerCopyRecipRangeIsPresent("");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("OR reciprocal range",
                          "Value is empty",
                          false);

        when(validationChecks.checkValueIsPresent(Matchers.anyString())).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkAlleleRiskFrequency(Matchers.anyString());
        assertThat(error2).extracting("field", "error", "warning")
                .contains(null,
                          null,
                          false);
    }

    @Test
    public void testCheckSnpGeneLocation() throws Exception {
        when(validationChecks.checkSnpGeneLocation("rsTEST", "TEST")).thenReturn(
                "Gene TEST and SNP rsTEST are not on the same chromosome");
        ValidationError error1 =
                errorCreationService.checkSnpGeneLocation("rsTEST", "TEST");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Gene",
                          "Gene TEST and SNP rsTEST are not on the same chromosome",
                          true);

        when(validationChecks.checkSnpGeneLocation("rs2981579", "FGFR2")).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkSnpGeneLocation("rs2981579", "FGFR2");
        assertThat(error2).extracting("field", "error", "warning")
                .contains(null,
                          null,
                          false);

        when(validationChecks.checkSnpGeneLocation("rs99", "FGFR2")).thenReturn(
                "SNP rs99 has no location details, cannot check if gene is on same chromosome as SNP");
        ValidationError error3 =
                errorCreationService.checkSnpGeneLocation("rs99", "FGFR2");
        assertThat(error3).extracting("field", "error", "warning")
                .contains("SNP",
                          "SNP rs99 has no location details, cannot check if gene is on same chromosome as SNP",
                          true);
    }

    @Test
    public void testCheckSnpSynthax() throws Exception {
        when(validationChecks.checkSynthax("rsTEST x rs123",
                                           ";")).thenReturn("Value does not contain correct separator");
        ValidationError error1 =
                errorCreationService.checkSnpSynthax("rsTEST x rs123", ";");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("SNP",
                          "Value does not contain correct separator",
                          false);

        when(validationChecks.checkSynthax("rs2981579; rs123", ";")).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkSnpSynthax("rs2981579; rs123", ";");
        assertThat(error2).extracting("field", "error", "warning")
                .contains(null,
                          null,
                          false);
    }

    @Test
    public void testCheckRiskAlleleSynthax() throws Exception {
        when(validationChecks.checkSynthax("rsTEST-? x rs123-A",
                                           ";")).thenReturn("Value does not contain correct separator");
        ValidationError error1 =
                errorCreationService.checkRiskAlleleSynthax("rsTEST-? x rs123-A", ";");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Risk Allele",
                          "Value does not contain correct separator",
                          false);

        when(validationChecks.checkSynthax("rs2981579; rs123", ";")).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkRiskAlleleSynthax("rs2981579; rs123", ";");
        assertThat(error2).extracting("field", "error", "warning")
                .contains(null,
                          null,
                          false);
    }

    @Test
    public void testCheckGeneSynthax() throws Exception {
        when(validationChecks.checkSynthax("SFRP1 x SFRP2",
                                           ";")).thenReturn("Value does not contain correct separator");
        ValidationError error1 =
                errorCreationService.checkGeneSynthax("SFRP1 x SFRP2", ";");
        assertThat(error1).extracting("field", "error", "warning")
                .contains("Gene",
                          "Value does not contain correct separator",
                          false);

        when(validationChecks.checkSynthax("rs2981579; rs123", ";")).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkGeneSynthax("rs2981579; rs123", ";");
        assertThat(error2).extracting("field", "error", "warning")
                .contains(null,
                          null,
                          false);
    }

    @Test
    public void testCheckSnpStatusIsPresent() throws Exception {
        when(validationChecks.checkSnpStatus(false, false)).thenReturn("No status selected");
        ValidationError error1 =
                errorCreationService.checkSnpStatusIsPresent(false, false);
        assertThat(error1).extracting("field", "error", "warning")
                .contains("SNP Status", "No status selected", false);

        when(validationChecks.checkSnpStatus(true, true)).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkSnpStatusIsPresent(true, true);
        assertThat(error2).extracting("field", "error", "warning")
                .contains(null,
                          null,
                          false);
    }
}