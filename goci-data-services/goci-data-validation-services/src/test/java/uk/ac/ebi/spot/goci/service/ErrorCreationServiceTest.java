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


    private static final AssociationUploadRow EMPTY_ROW = new AssociationUploadRowBuilder().setRowNumber(1).build();

    @Mock
    private ValidationChecks validationChecks;

    @Before
    public void setUp() throws Exception {
        errorCreationService = new ErrorCreationService(validationChecks);
    }

    @Test
    public void testCheckSnpValueIsPresent() throws Exception {
        when(validationChecks.checkValueIsPresent(EMPTY_ROW.getSnp())).thenReturn("Empty value");
        ValidationError error = errorCreationService.checkSnpValueIsPresent(EMPTY_ROW);
        assertThat(error).extracting("field", "error").contains("SNP","Empty value");
    }

    @Test
    public void testCheckStrongestAlleleValueIsPresent() throws Exception {
        when(validationChecks.checkValueIsPresent(EMPTY_ROW.getStrongestAllele())).thenReturn("Empty value");
        ValidationError error = errorCreationService.checkStrongestAlleleValueIsPresent(EMPTY_ROW);
        assertThat(error).extracting("field", "error").contains("Risk Allele","Empty value");
    }

    @Test
    public void testCheckSnpType() throws Exception {

    }

    @Test
    public void testCheckOrIsPresent() throws Exception {

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