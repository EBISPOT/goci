package uk.ac.ebi.spot.goci.component;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.service.rest.GeneCheckingRestService;
import uk.ac.ebi.spot.goci.service.rest.SnpCheckingRestService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 21/06/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidationChecksTest {

    @Mock
    private GeneCheckingRestService geneCheckingRestService;

    @Mock
    private SnpCheckingRestService snpCheckingRestService;

    private ValidationChecks validationChecks;

    @Before
    public void setUp() throws Exception {
        validationChecks = new ValidationChecks(geneCheckingRestService, snpCheckingRestService);
    }

    @Test
    public void checkValueIsPresent() throws Exception {
        assertThat(validationChecks.checkValueIsPresent("rs123")).isNull();
        assertThat(validationChecks.checkValueIsPresent("")).isEqualTo("Value is empty");
        assertThat(validationChecks.checkValueIsPresent(null)).isEqualTo("Value is empty");
    }

    @Test
    public void checkValueIsEmptyStringValue() throws Exception {
        assertThat(validationChecks.checkValueIsEmpty("unit increase")).isEqualTo("Value is not empty");
        assertThat(validationChecks.checkValueIsEmpty("")).isNull();
    }

    @Test
    public void checkValueIsEmptyFloatValue() throws Exception {
        assertThat(validationChecks.checkValueIsEmpty((float) 0.66)).isEqualTo("Value is not empty");
        assertThat(validationChecks.checkValueIsEmpty("")).isNull();
    }

    @Test
    public void checkSnpType() throws Exception {
        assertThat(validationChecks.checkSnpType("novel")).isNull();
        assertThat(validationChecks.checkSnpType("known")).isNull();
        assertThat(validationChecks.checkSnpType("something else")).isEqualTo("Value does not contain novel or known");
        assertThat(validationChecks.checkSnpType(null)).isEqualTo("Value is empty");
    }

    @Test
    public void checkOrIsPresentAndMoreThanOne() throws Exception {
        assertThat(validationChecks.checkOrIsPresentAndMoreThanOne((float) 1.22)).isNull();
        assertThat(validationChecks.checkOrIsPresentAndMoreThanOne((float) 0.56)).isEqualTo("Value is less than 1");
        assertThat(validationChecks.checkOrIsPresentAndMoreThanOne(null)).isEqualTo("Value is empty");
    }

    @Test
    public void checkBetaIsPresentAndIsNotNegative() throws Exception {
        assertThat(validationChecks.checkBetaIsPresentAndIsNotNegative((float) 0.56)).isNull();
        assertThat(validationChecks.checkBetaIsPresentAndIsNotNegative((float) -0.22)).isEqualTo("Value is less than 0");
        assertThat(validationChecks.checkBetaIsPresentAndIsNotNegative(null)).isEqualTo("Value is empty");
    }

    @Test
    public void checkBetaDirectionIsPresent() throws Exception {
        assertThat(validationChecks.checkBetaDirectionIsPresent("increase")).isNull();
        assertThat(validationChecks.checkBetaDirectionIsPresent("decrease")).isNull();
        assertThat(validationChecks.checkBetaDirectionIsPresent("some other value")).isEqualTo(
                "Value is not increase or decrease");
        assertThat(validationChecks.checkBetaDirectionIsPresent(null)).isEqualTo("Value is empty");
    }

    @Test
    public void checkMantissaIsLessThan10() throws Exception {
        assertThat(validationChecks.checkMantissaIsLessThan10(123)).isEqualTo("Value not valid i.e. greater than 9");
        assertThat(validationChecks.checkMantissaIsLessThan10(null)).isEqualTo("Value is empty");
        assertThat(validationChecks.checkMantissaIsLessThan10(5)).isNull();
    }

    @Test
    public void checkExponentIsPresent() throws Exception {
        assertThat(validationChecks.checkExponentIsPresent(null)).isEqualTo("Value is empty");
        assertThat(validationChecks.checkExponentIsPresent(0)).isEqualTo("Value is zero");
        assertThat(validationChecks.checkExponentIsPresent(7)).isNull();
    }

    @Test
    public void checkGene() throws Exception {

        // Stubbing
        when(geneCheckingRestService.checkGeneSymbolIsValid("MADEUPGENE")).thenReturn(
                "Gene symbol MADEUPGENE is not valid");
        when(geneCheckingRestService.checkGeneSymbolIsValid("HBS1L")).thenReturn(null);

        assertThat(validationChecks.checkGene("")).isEqualTo("Gene name is empty");
        assertThat(validationChecks.checkGene(null)).isEqualTo("Gene name is empty");
        assertThat(validationChecks.checkGene("MADEUPGENE")).isEqualTo("Gene symbol MADEUPGENE is not valid");
        assertThat(validationChecks.checkGene("HBS1L")).isNull();
    }

    @Test
    public void checkSnp() throws Exception {

        // Stubbing
        when(snpCheckingRestService.checkSnpIdentifierIsValid("MADEUPSNP")).thenReturn(
                "SNP identifier MADEUPSNP is not valid");
        when(snpCheckingRestService.checkSnpIdentifierIsValid("rs7329174")).thenReturn(null);

        assertThat(validationChecks.checkSnp("")).isEqualTo("SNP identifier is empty");
        assertThat(validationChecks.checkSnp(null)).isEqualTo("SNP identifier is empty");
        assertThat(validationChecks.checkSnp("MADEUPSNP")).isEqualTo("SNP identifier MADEUPSNP is not valid");
        assertThat(validationChecks.checkSnp("rs7329174")).isNull();
    }

    @Test
    public void checkSnpGeneLocation() throws Exception {

        // Stubbing
        when(snpCheckingRestService.getSnpLocations("rs7329174")).thenReturn(Collections.singleton("13"));
        when(snpCheckingRestService.getSnpLocations("rs1234")).thenReturn(Collections.EMPTY_SET);
        when(snpCheckingRestService.getSnpLocations("rs11894081")).thenReturn(Collections.singleton("X"));
        when(geneCheckingRestService.getGeneLocation("ELF1")).thenReturn("13");

        when(snpCheckingRestService.checkSnpIdentifierIsValid("rs7329174")).thenReturn(null);
        when(geneCheckingRestService.checkGeneSymbolIsValid("ELF1")).thenReturn(null);
        when(snpCheckingRestService.checkSnpIdentifierIsValid("MADEUPSNP")).thenReturn(
                "SNP identifier MADEUPSNP is not valid");
        when(geneCheckingRestService.checkGeneSymbolIsValid("MADEUPGENE")).thenReturn(
                "Gene symbol MADEUPGENE is not valid");

        // Test pass condition
        assertThat(validationChecks.checkSnpGeneLocation("rs7329174", "ELF1")).isNull();

        // Test invalid SNP condition
        assertThat(validationChecks.checkSnpGeneLocation("MADEUPSNP", "ELF1")).isEqualTo(
                "SNP MADEUPSNP is not valid, cannot check if gene is on same chromosome as SNP");

        // Test invalid Gene condition
        assertThat(validationChecks.checkSnpGeneLocation("rs7329174", "MADEUPGENE")).isEqualTo(
                "Gene MADEUPGENE is not valid, cannot check if gene is on same chromosome as SNP");

        // Test SNP with no locations
        assertThat(validationChecks.checkSnpGeneLocation("rs1234", "SFRP1")).isEqualTo(
                "SNP rs1234 has no location details, cannot check if gene is on same chromosome as SNP");

        // Test SNP with different location to gene
        assertThat(validationChecks.checkSnpGeneLocation("rs11894081", "ELF1")).isEqualTo(
                "Gene ELF1 and SNP rs11894081 are not on same chromosome");
    }

    @Test
    public void checkRiskAllele() throws Exception {
        assertThat(validationChecks.checkRiskAllele("A")).isNull();
        assertThat(validationChecks.checkRiskAllele("T")).isNull();
        assertThat(validationChecks.checkRiskAllele("C")).isNull();
        assertThat(validationChecks.checkRiskAllele("G")).isNull();
        assertThat(validationChecks.checkRiskAllele("?")).isNull();
        assertThat(validationChecks.checkRiskAllele("")).isEqualTo("Risk allele is empty");
        assertThat(validationChecks.checkRiskAllele(null)).isEqualTo("Risk allele is empty");
        assertThat(validationChecks.checkRiskAllele("CHR1234")).isEqualTo("Risk allele is not one of [A, T, G, C, ?]");
    }

    @Test
    public void checkRiskFrequency() throws Exception {
        assertThat(validationChecks.checkRiskFrequency("NR")).isNull();
        assertThat(validationChecks.checkRiskFrequency("0.24")).isNull();
        assertThat(validationChecks.checkRiskFrequency("(aac)")).isEqualTo(
                "Value is invalid i.e. not equal to NR or a number");
        assertThat(validationChecks.checkRiskFrequency("989")).isEqualTo(
                "Value is invalid, value is not between 0 and 1");
    }

    @Test
    public void checkSynthax() throws Exception {
        assertThat(validationChecks.checkSynthax("rs123 x rs456", "x")).isNull();
        assertThat(validationChecks.checkSynthax("rs123-rs456", "x")).isEqualTo(
                "Value does not contain correct separator");
    }

    @Test
    public void checkSnpStatus() throws Exception {
        assertThat(validationChecks.checkSnpStatus(true, false)).isNull();
        assertThat(validationChecks.checkSnpStatus(false, true)).isNull();
        assertThat(validationChecks.checkSnpStatus(false, false)).isEqualTo("No status selected");
    }
}