package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationUploadRowBuilder;
import uk.ac.ebi.spot.goci.builder.ValidationErrorBuilder;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.ValidationError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 21/04/2016.
 *
 * @author emma
 *         <p>
 *         Test for RowChecksBuilder
 */
@RunWith(MockitoJUnitRunner.class)
public class RowChecksBuilderTest {

    private RowChecksBuilder rowChecksBuilder;

    @Mock
    private ErrorCreationService errorCreationService;

    private static final AssociationUploadRow EMPTY_ROW = new AssociationUploadRowBuilder().setRowNumber(1).build();

    private static final AssociationUploadRow ROW_WITH_NO_SNP =
            new AssociationUploadRowBuilder().setRowNumber(1).setStrongestAllele("rs123456-?").build();

    private static final AssociationUploadRow ROW_WITH_NO_RA =
            new AssociationUploadRowBuilder().setRowNumber(1).setSnp("rs123456").build();

    private static final AssociationUploadRow INTERACTION_ROW_NO_ERROR =
            new AssociationUploadRowBuilder().setRowNumber(1)
                    .setSnp("rs2562796 x rs16832404")
                    .setStrongestAllele("rs2562796-T x rs16832404-G")
                    .setSnpInteraction("Y")
                    .build();

    private static final AssociationUploadRow INTERACTION_ROW_ERROR =
            new AssociationUploadRowBuilder().setRowNumber(1)
                    .setSnp("rs2562796 ; rs16832404")
                    .setStrongestAllele("rs2562796-T;rs16832404-G")
                    .setAuthorReportedGene("SFRP1 - SFRP2")
                    .setSnpInteraction("Y")
                    .build();

    private static final ValidationError ERROR_MISSING_SNP =
            new ValidationErrorBuilder().setField("SNP").setError("Missing value").build();

    private static final ValidationError ERROR_MISSING_RA =
            new ValidationErrorBuilder().setField("Strongest SNP-Risk Allele/Effect Allele")
                    .setError("Missing value")
                    .build();

    private static final ValidationError ERROR_03 =
            new ValidationErrorBuilder().build();

    private static final ValidationError SNP_INTERACTION_ERROR_01 =
            new ValidationErrorBuilder().setField("SNP").setError("Value does not contain correct separator").build();

    private static final ValidationError SNP_INTERACTION_ERROR_02 =
            new ValidationErrorBuilder().setField("Risk Allele")
                    .setError("Value does not contain correct separator")
                    .build();

    private static final ValidationError SNP_INTERACTION_ERROR_03 =
            new ValidationErrorBuilder().setField("Gene").setError("Value does not contain correct separator").build();

    @Before
    public void setUp() throws Exception {
        rowChecksBuilder = new RowChecksBuilder(errorCreationService);
    }

    @Test
    public void testRunEmptyValueChecks() throws Exception {

        // Stubbing
        when(errorCreationService.checkSnpValueIsPresent(EMPTY_ROW.getSnp())).thenReturn(ERROR_MISSING_SNP);
        when(errorCreationService.checkStrongestAlleleValueIsPresent(EMPTY_ROW.getStrongestAllele())).thenReturn(ERROR_MISSING_RA);

        when(errorCreationService.checkSnpValueIsPresent(ROW_WITH_NO_SNP.getSnp())).thenReturn(ERROR_MISSING_SNP);
        when(errorCreationService.checkStrongestAlleleValueIsPresent(ROW_WITH_NO_SNP.getStrongestAllele())).thenReturn(ERROR_03);

        when(errorCreationService.checkSnpValueIsPresent(ROW_WITH_NO_RA.getSnp())).thenReturn(ERROR_03);
        when(errorCreationService.checkStrongestAlleleValueIsPresent(ROW_WITH_NO_RA.getStrongestAllele())).thenReturn(ERROR_MISSING_RA);

        // Test of no SNP and Risk allele
        assertThat(rowChecksBuilder.runEmptyValueChecks(EMPTY_ROW)).hasSize(2);
        assertThat(rowChecksBuilder.runEmptyValueChecks(EMPTY_ROW)).extracting("field", "error")
                .contains(tuple(
                        "SNP",
                        "Missing value"), tuple("Strongest SNP-Risk Allele/Effect Allele", "Missing value"));

        // Test of no SNP
        assertThat(rowChecksBuilder.runEmptyValueChecks(ROW_WITH_NO_SNP)).hasSize(1);
        assertThat(rowChecksBuilder.runEmptyValueChecks(ROW_WITH_NO_SNP)).extracting("field", "error")
                .contains(tuple("SNP", "Missing value"));

        // Test of no RA
        assertThat(rowChecksBuilder.runEmptyValueChecks(ROW_WITH_NO_RA)).hasSize(1);
        assertThat(rowChecksBuilder.runEmptyValueChecks(ROW_WITH_NO_RA)).extracting("field", "error")
                .contains(tuple("Strongest SNP-Risk Allele/Effect Allele", "Missing value"));

    }

    @Test
    public void testRunSynthaxChecks() throws Exception {
        // Stubbing
        when(errorCreationService.checkSnpSynthax(INTERACTION_ROW_NO_ERROR.getSnp(), "x")).thenReturn(ERROR_03);
        when(errorCreationService.checkRiskAlleleSynthax(INTERACTION_ROW_NO_ERROR.getStrongestAllele(), "x")).thenReturn(ERROR_03);
        when(errorCreationService.checkSnpSynthax(INTERACTION_ROW_ERROR.getSnp(), "x")).thenReturn(SNP_INTERACTION_ERROR_01);
        when(errorCreationService.checkRiskAlleleSynthax(INTERACTION_ROW_ERROR.getStrongestAllele(), "x")).thenReturn(
                SNP_INTERACTION_ERROR_02);
        when(errorCreationService.checkGeneSynthax(INTERACTION_ROW_ERROR.getAuthorReportedGene(), "x")).thenReturn(
                SNP_INTERACTION_ERROR_03);

        assertThat(rowChecksBuilder.runSynthaxChecks(INTERACTION_ROW_NO_ERROR)).hasSize(0);
        assertThat(rowChecksBuilder.runSynthaxChecks(INTERACTION_ROW_ERROR)).hasSize(3);
        assertThat(rowChecksBuilder.runSynthaxChecks(INTERACTION_ROW_ERROR)).extracting("field", "error")
                .contains(tuple("SNP", "Value does not contain correct separator"),
                          tuple("Risk Allele", "Value does not contain correct separator"),
                          tuple("Gene", "Value does not contain correct separator"));
    }
}