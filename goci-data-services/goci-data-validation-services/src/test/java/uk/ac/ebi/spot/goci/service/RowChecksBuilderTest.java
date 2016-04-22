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
    private CheckingService checkingService;

    private static final AssociationUploadRow EMPTY_ROW = new AssociationUploadRowBuilder().setRowNumber(1).build();

    private static final AssociationUploadRow ROW_WITH_NO_SNP =
            new AssociationUploadRowBuilder().setRowNumber(1).setStrongestAllele("rs123456-?").build();

    private static final AssociationUploadRow ROW_WITH_NO_RA =
            new AssociationUploadRowBuilder().setRowNumber(1).setSnp("rs123456").build();

    private static final ValidationError ERROR_MISSING_SNP =
            new ValidationErrorBuilder().setField("SNP").setError("Missing value").build();

    private static final ValidationError ERROR_MISSING_RA =
            new ValidationErrorBuilder().setField("Strongest SNP-Risk Allele/Effect Allele")
                    .setError("Missing value")
                    .build();

    private static final ValidationError ERROR_03 =
            new ValidationErrorBuilder().build();
    
    @Before
    public void setUp() throws Exception {
        rowChecksBuilder = new RowChecksBuilder(checkingService);
    }

    @Test
    public void testRunEmptyValueChecks() throws Exception {

        // Stubbing
        when(checkingService.checkSnpValueIsPresent(EMPTY_ROW)).thenReturn(ERROR_MISSING_SNP);
        when(checkingService.checkStrongestAlleleValueIsPresent(EMPTY_ROW)).thenReturn(ERROR_MISSING_RA);

        when(checkingService.checkSnpValueIsPresent(ROW_WITH_NO_SNP)).thenReturn(ERROR_MISSING_SNP);
        when(checkingService.checkStrongestAlleleValueIsPresent(ROW_WITH_NO_SNP)).thenReturn(ERROR_03);

        when(checkingService.checkSnpValueIsPresent(ROW_WITH_NO_RA)).thenReturn(ERROR_03);
        when(checkingService.checkStrongestAlleleValueIsPresent(ROW_WITH_NO_RA)).thenReturn(ERROR_MISSING_RA);

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
}