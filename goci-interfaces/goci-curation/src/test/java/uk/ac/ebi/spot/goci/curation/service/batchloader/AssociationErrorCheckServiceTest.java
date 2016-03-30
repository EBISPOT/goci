package uk.ac.ebi.spot.goci.curation.service.batchloader;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.BatchUploadRowBuilder;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadError;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadRow;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertNotNull;

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

    private static final BatchUploadRow OR_ERRORS = new BatchUploadRowBuilder().setRowNumber(1)
            .setEffectType("OR")
            .setBetaNum((float) 0.78)
            .setBetaDirection("increase")
            .setBetaUnit("cm")
            .build();

    private static final BatchUploadRow OR_NO_ERRORS = new BatchUploadRowBuilder().setRowNumber(2)
            .setEffectType("OR")
            .setOrPerCopyNum((float) 1.22)
            .setOrPerCopyRecip((float) 0.78)
            .build();

    private static final BatchUploadRow NR_NO_ERRORS = new BatchUploadRowBuilder().setRowNumber(3)
            .setEffectType("NR")
            .build();

    private static final BatchUploadRow NR_ERRORS = new BatchUploadRowBuilder().setRowNumber(4)
            .setEffectType("NR")
            .setOrPerCopyNum((float) 1.22)
            .setOrPerCopyRecip((float) 0.78)
            .setBetaNum((float) 0.78)
            .setBetaDirection("increase")
            .setBetaUnit("cm")
            .setRange("[0.82-0.92]")
            .setOrPerCopyRecipRange("[0.82-0.92]")
            .setStandardError((float) 0.6)
            .setDescription("test")
            .build();

    private static final BatchUploadRow BETA_NO_ERRORS = new BatchUploadRowBuilder().setRowNumber(5)
            .setEffectType("Beta")
            .setBetaNum((float) 0.78)
            .setBetaDirection("increase")
            .setBetaUnit("cm")
            .build();

    private static final BatchUploadRow BETA_ERRORS = new BatchUploadRowBuilder().setRowNumber(6)
            .setEffectType("Beta")
            .setOrPerCopyNum((float) 1.22)
            .setOrPerCopyRecip((float) 0.78)
            .setOrPerCopyRecipRange("[0.82-0.92]")
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
    public void test


    @Test
    public void testCheckRowForErrors() throws Exception {

        Collection<BatchUploadError> batchUploadErrors = associationErrorCheckService.runFullChecks(Arrays.asList(
                OR_ERRORS, OR_NO_ERRORS, NR_ERRORS,
                NR_NO_ERRORS, BETA_NO_ERRORS, BETA_ERRORS));

        assertThat(batchUploadErrors).isNotEmpty();
        assertThat(batchUploadErrors).hasSize(19);
        assertThat(batchUploadErrors).extracting("row").contains(1, 4, 6);
        assertThat(batchUploadErrors).extracting("row").doesNotContain(2, 3, 5);
        assertThat(batchUploadErrors).extracting("row", "columnName", "error")
                .contains(tuple(1, "OR", "OR num is empty for association with effect type: OR"),
                          tuple(1, "Beta", "Beta value found for association with effect type: OR"),
                          tuple(1, "Beta unit", "Beta unit found for association with effect type: OR"),
                          tuple(1, "Beta Direction", "Beta direction found for association with effect type: OR"),
                          // row 4 checks
                          tuple(4, "OR", "OR num found for association with effect type: NR"),
                          tuple(4, "OR reciprocal", "OR reciprocal found for association with effect type: NR"),
                          tuple(4,
                                "OR reciprocal range",
                                "OR reciprocal range found for association with effect type: NR"),
                          tuple(4, "Beta", "Beta value found for association with effect type: NR"),
                          tuple(4, "Beta unit", "Beta unit found for association with effect type: NR"),
                          tuple(4, "Beta Direction", "Beta direction found for association with effect type: NR"),
                          tuple(4, "Range", "Range found for association with effect type: NR"),
                          tuple(4, "Standard Error", "Standard error found for association with effect type: NR"),
                          tuple(4,
                                "OR/Beta description",
                                "OR/Beta description found for association with effect type: NR"),
                          // row 6 checks
                          tuple(6, "Beta", "Beta is empty for association with effect type: Beta"),
                          tuple(6, "Beta Unit", "Beta unit is empty for association with effect type: Beta"),
                          tuple(6, "Beta Direction", "Beta direction is empty for association with effect type: Beta"),
                          tuple(6, "OR", "OR num found for association with effect type: Beta"),
                          tuple(6, "OR reciprocal", "OR reciprocal found for association with effect type: Beta"),
                          tuple(6,
                                "OR reciprocal range",
                                "OR reciprocal range found for association with effect type: Beta")
                );
    }
}