package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationUploadRowBuilder;
import uk.ac.ebi.spot.goci.component.ValidationChecks;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.ValidationError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 21/04/2016.
 *
 * @author emma
 *         <p>
 *         Test of CheckingService
 */
@RunWith(MockitoJUnitRunner.class)
public class ErrorCreationServiceTest {

    private ErrorCreationService errorCreationService;

    private static final AssociationUploadRow INVALID_ROW =
            new AssociationUploadRowBuilder().setRowNumber(1)
                    .setSnpType("unknown")
                    .setOrPerCopyNum((float) 0.9)
                    .build();

    private static final AssociationUploadRow VALID_ROW = new AssociationUploadRowBuilder().setSnp("rs123")
            .setStrongestAllele("rs123-?")
            .setRowNumber(1)
            .setSnpType("novel")
            .setOrPerCopyNum((float) 1.2)
            .setOrPerCopyRecip((float) 0.83)
            .build();

    @Mock
    private ValidationChecks validationChecks;

    @Before
    public void setUp() throws Exception {
        errorCreationService = new ErrorCreationService(validationChecks);
    }

    @Test
    public void testCheckSnpValueIsPresent() throws Exception {
        when(validationChecks.checkValueIsPresent(INVALID_ROW.getSnp())).thenReturn("Value is empty");
        ValidationError error1 = errorCreationService.checkSnpValueIsPresent(INVALID_ROW.getSnp());
        assertThat(error1).extracting("field", "error", "warning").contains("SNP", "Value is empty", false);

        when(validationChecks.checkValueIsPresent(VALID_ROW.getSnp())).thenReturn(null);
        ValidationError error2 = errorCreationService.checkSnpValueIsPresent(VALID_ROW.getSnp());
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckStrongestAlleleValueIsPresent() throws Exception {
        when(validationChecks.checkValueIsPresent(INVALID_ROW.getStrongestAllele())).thenReturn("Value is empty");
        ValidationError error1 =
                errorCreationService.checkStrongestAlleleValueIsPresent(INVALID_ROW.getStrongestAllele());
        assertThat(error1).extracting("field", "error", "warning").contains("Risk Allele", "Value is empty", false);

        when(validationChecks.checkValueIsPresent(VALID_ROW.getStrongestAllele())).thenReturn(null);
        ValidationError error2 =
                errorCreationService.checkStrongestAlleleValueIsPresent(INVALID_ROW.getStrongestAllele());
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckSnpType() throws Exception {
        when(validationChecks.checkSnpType(INVALID_ROW.getSnpType())).thenReturn("Value does not contain novel or known");
        ValidationError error1 = errorCreationService.checkSnpType(INVALID_ROW.getSnpType());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("SNP type", "Value does not contain novel or known", false);

        when(validationChecks.checkSnpType(VALID_ROW.getSnpType())).thenReturn(null);
        ValidationError error2 = errorCreationService.checkSnpType(VALID_ROW.getSnpType());
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);
    }

    @Test
    public void testCheckOrIsPresent() throws Exception {

        when(validationChecks.checkOrIsPresentAndMoreThanOne(INVALID_ROW.getOrPerCopyNum())).thenReturn(
                "Value is less than 1");
        ValidationError error1 = errorCreationService.checkOrIsPresentAndMoreThanOne(INVALID_ROW.getOrPerCopyNum());
        assertThat(error1).extracting("field", "error", "warning")
                .contains("OR", "Value is less than 1", false);

        when(validationChecks.checkOrIsPresentAndMoreThanOne(VALID_ROW.getOrPerCopyNum())).thenReturn(null);
        ValidationError error2 = errorCreationService.checkOrIsPresentAndMoreThanOne(VALID_ROW.getOrPerCopyNum());
        assertThat(error2).extracting("field", "error", "warning").contains(null, null, false);

    }

    @Test
    public void testCheckBetaValuesIsEmpty() throws Exception {

    }

    @Test
    public void testCheckBetaUnitIsEmpty() throws Exception {

    }

    @Test
    public void testCheckBetaDirectionIsEmpty() throws Exception {

    }

    @Test
    public void testCheckBetaIsPresent() throws Exception {

    }

    @Test
    public void testCheckBetaUnitIsPresent() throws Exception {

    }

    @Test
    public void testCheckBetaDirectionIsPresent() throws Exception {

    }

    @Test
    public void testCheckOrEmpty() throws Exception {

    }

    @Test
    public void testCheckOrRecipEmpty() throws Exception {

    }

    @Test
    public void testCheckOrPerCopyRecipRange() throws Exception {

    }

    @Test
    public void testCheckRangeIsEmpty() throws Exception {

    }

    @Test
    public void testCheckStandardErrorIsEmpty() throws Exception {

    }

    @Test
    public void testCheckDescriptionIsEmpty() throws Exception {

    }

    @Test
    public void testCheckMantissaIsLessThan10() throws Exception {

    }

    @Test
    public void testCheckExponentIsPresent() throws Exception {

    }

    @Test
    public void testCheckGene() throws Exception {

    }

    @Test
    public void testCheckRiskAllele() throws Exception {

    }
}