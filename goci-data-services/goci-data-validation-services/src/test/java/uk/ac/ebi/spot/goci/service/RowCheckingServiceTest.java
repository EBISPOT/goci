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

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 13/07/2016.
 *
 * @author emma
 *         <p>
 *         Test for public class RowCheckingService
 */
@RunWith(MockitoJUnitRunner.class)
public class RowCheckingServiceTest {

    @Mock
    private RowChecksBuilder rowChecksBuilder;

    private RowCheckingService rowCheckingService;

    private static final AssociationUploadRow ROW_NO_ERROR =
            new AssociationUploadRowBuilder().setRowNumber(1)
                    .setSnp("rs2562796 x rs16832404")
                    .setStrongestAllele("rs2562796-T x rs16832404-G")
                    .setAuthorReportedGene("SFRP1 x SFRP2")
                    .setSnpInteraction("Y")
                    .build();

    private static final AssociationUploadRow ROW_ERROR =
            new AssociationUploadRowBuilder().setRowNumber(2).setMultiSnpHaplotype("Y")
                    .build();

    private static final ValidationError VALIDATION_ERROR1 =
            new ValidationErrorBuilder().setField("SNP").setError("Value is empty").build();
    private static final ValidationError VALIDATION_ERROR2 =
            new ValidationErrorBuilder().setField("Risk Allele").setError("Value is empty").build();

    private static final ValidationError VALIDATION_ERROR3 =
            new ValidationErrorBuilder().setField("SNP").setError("Value does not contain correct separator").build();
    private static final ValidationError VALIDATION_ERROR4 = new ValidationErrorBuilder().setField("Risk Allele")
            .setError("Value does not contain correct separator")
            .build();

    @Before
    public void setUp() throws Exception {
        rowCheckingService = new RowCheckingService(rowChecksBuilder);
    }

    @Test
    public void testRunEmptyValues() {
        // Stubbing
        when(rowChecksBuilder.runEmptyValueChecks(ROW_ERROR)).thenReturn(Arrays.asList(VALIDATION_ERROR1,
                                                                                       VALIDATION_ERROR2));

        assertThat(rowCheckingService.runChecks(ROW_ERROR)).isNotEmpty().hasSize(2);
        verify(rowChecksBuilder, never()).runSynthaxChecks(ROW_ERROR);
        verify(rowChecksBuilder, times(1)).runEmptyValueChecks(ROW_ERROR);
    }

    @Test
    public void testRunChecksSynthaxErrors() {
        // Stubbing
        when(rowChecksBuilder.runEmptyValueChecks(ROW_ERROR)).thenReturn(Collections.EMPTY_LIST);
        when(rowChecksBuilder.runSynthaxChecks(ROW_ERROR)).thenReturn(Arrays.asList(VALIDATION_ERROR3,VALIDATION_ERROR4));

        assertThat(rowCheckingService.runChecks(ROW_ERROR)).isNotEmpty().hasSize(2);
        verify(rowChecksBuilder, times(1)).runSynthaxChecks(ROW_ERROR);
        verify(rowChecksBuilder, times(1)).runEmptyValueChecks(ROW_ERROR);
    }

    @Test
    public void testRunChecksNoErrors() {
        // Stubbing
        when(rowChecksBuilder.runEmptyValueChecks(ROW_NO_ERROR)).thenReturn(Collections.EMPTY_LIST);
        when(rowChecksBuilder.runSynthaxChecks(ROW_NO_ERROR)).thenReturn(Collections.EMPTY_LIST);

        assertThat(rowCheckingService.runChecks(ROW_NO_ERROR).isEmpty());
        verify(rowChecksBuilder, times(1)).runSynthaxChecks(ROW_NO_ERROR);
        verify(rowChecksBuilder, times(1)).runEmptyValueChecks(ROW_NO_ERROR);
    }
}