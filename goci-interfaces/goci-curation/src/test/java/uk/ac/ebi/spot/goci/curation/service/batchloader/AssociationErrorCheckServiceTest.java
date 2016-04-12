package uk.ac.ebi.spot.goci.curation.service.batchloader;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.BatchUploadErrorBuilder;
import uk.ac.ebi.spot.goci.curation.builder.BatchUploadRowBuilder;
import uk.ac.ebi.spot.goci.model.BatchUploadError;
import uk.ac.ebi.spot.goci.model.BatchUploadRow;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 24/03/2016.
 *
 * @author emma
 *         <p>
 *         Test for AssociationUploadErrorService
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationErrorCheckServiceTest {

    private AssociationErrorCheckService associationErrorCheckService;

    @Mock
    private CheckService checkService;

    private static final BatchUploadRow ROW_WITH_ERRORS = new BatchUploadRowBuilder().setRowNumber(1)
            .setEffectType("OR")
            .setBetaNum((float) 0.78)
            .setBetaDirection("increase")
            .setBetaUnit("cm")
            .setSnpType("UNKNOWN")
            .build();


    private static final BatchUploadRow ROW_NO_ERRORS = new BatchUploadRowBuilder().setRowNumber(1)
            .setEffectType("Beta")
            .setBetaNum((float) 0.78)
            .setBetaDirection("increase")
            .setBetaUnit("cm")
            .setSnpType("novel")
            .build();


    private static final BatchUploadError ANNOTATION_ERROR1 =
            new BatchUploadErrorBuilder().setRow(1).setColumnName("SNP type")
                    .setError("SNP type does not contain novel or known")
                    .build();

    private static final BatchUploadError ANNOTATION_ERROR2 =
            new BatchUploadErrorBuilder().setRow(1).setColumnName("OR")
                    .setError("OR num is empty for association with effect type: OR")
                    .build();

    private static final BatchUploadError ANNOTATION_ERROR3 =
            new BatchUploadErrorBuilder().setRow(1).setColumnName("Beta")
                    .setError("Beta value found for association with effect type: OR")
                    .build();

    private static final BatchUploadError ANNOTATION_ERROR4 =
            new BatchUploadErrorBuilder().setRow(1).setColumnName("Beta unit")
                    .setError("Beta unit found for association with effect type: OR")
                    .build();

    private static final BatchUploadError ANNOTATION_ERROR5 =
            new BatchUploadErrorBuilder().setRow(1).setColumnName("Beta Direction")
                    .setError("Beta direction found for association with effect type: OR")
                    .build();


    @Before
    public void setUp() throws Exception {
        associationErrorCheckService = new AssociationErrorCheckService(checkService);
    }

    @Test
    public void testMocks() {
        // Test mock creation
        assertNotNull(checkService);
    }

    @Test
    public void testRunFullChecks() {

        // Stubbing
        when(checkService.runAnnotationChecks(ROW_WITH_ERRORS)).thenReturn(Arrays.asList(ANNOTATION_ERROR1,
                                                                                         ANNOTATION_ERROR2,
                                                                                         ANNOTATION_ERROR3,
                                                                                         ANNOTATION_ERROR4,
                                                                                         ANNOTATION_ERROR5));
        assertThat(associationErrorCheckService.runFullChecks(Arrays.asList(ROW_WITH_ERRORS,
                                                                            ROW_NO_ERRORS))).isNotEmpty();
        assertThat(associationErrorCheckService.runFullChecks(Arrays.asList(ROW_WITH_ERRORS,
                                                                            ROW_NO_ERRORS))).hasSize(5);
    }
}