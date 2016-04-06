package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.BatchUploadRowBuilder;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadRow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;


/**
 * Created by emma on 30/03/2016.
 *
 * @author emma
 *         <p>
 *         Test for goci/goci-interfaces/goci-curation/src/main/java/uk/ac/ebi/spot/goci/curation/service/batchloader/CheckService.java
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckServiceTest {

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

    private static final BatchUploadRow
            ROW_WITH_INCORRECT_SNP_TYPE = new BatchUploadRowBuilder().setRowNumber(7).setSnpType("error").build();

    private static final BatchUploadRow
            ROW_WITH_CORRECT_SNP_TYPE = new BatchUploadRowBuilder().setRowNumber(8).setSnpType("novel").build();

    private static final BatchUploadRow
            ROW_WITH_EMPTY_SNP_TYPE = new BatchUploadRowBuilder().setRowNumber(9).build();

    private static final BatchUploadRow BETA_DIRECTION_ERRORS = new BatchUploadRowBuilder().setRowNumber(10)
            .setEffectType("Beta")
            .setBetaNum((float) 0.78)
            .setBetaDirection("down")
            .setBetaUnit("cm")
            .build();

    @Before
    public void setUp() throws Exception {
        checkService = new CheckService();
    }

    @Test
    public void testRunAnnotationChecks() throws Exception {

        // Error due to wrong value
        assertThat(checkService.runAnnotationChecks(ROW_WITH_INCORRECT_SNP_TYPE)).isNotEmpty();
        assertThat(checkService.runAnnotationChecks(ROW_WITH_INCORRECT_SNP_TYPE)).hasSize(1);
        assertThat(checkService.runAnnotationChecks(ROW_WITH_INCORRECT_SNP_TYPE)).extracting("row").containsOnly(7);
        assertThat(checkService.runAnnotationChecks(ROW_WITH_INCORRECT_SNP_TYPE)).extracting("row",
                                                                                             "columnName",
                                                                                             "error")
                .contains(tuple(7, "SNP type", "SNP type does not contain novel or known"));

        // Error due to empty value
        assertThat(checkService.runAnnotationChecks(ROW_WITH_EMPTY_SNP_TYPE)).isNotEmpty();
        assertThat(checkService.runAnnotationChecks(ROW_WITH_EMPTY_SNP_TYPE)).hasSize(1);
        assertThat(checkService.runAnnotationChecks(ROW_WITH_EMPTY_SNP_TYPE)).extracting("row").containsOnly(9);
        assertThat(checkService.runAnnotationChecks(ROW_WITH_EMPTY_SNP_TYPE)).extracting("row", "columnName", "error")
                .contains(tuple(9, "SNP type", "SNP type is empty"));

        // No errors
        assertThat(checkService.runAnnotationChecks(ROW_WITH_CORRECT_SNP_TYPE)).isEmpty();
    }

    @Test
    public void testRunOrChecks() throws Exception {

        // No errors
        assertThat(checkService.runOrChecks(OR_NO_ERRORS, "OR")).isEmpty();

        // Errors
        assertThat(checkService.runOrChecks(OR_ERRORS, "OR")).isNotEmpty();
        assertThat(checkService.runOrChecks(OR_ERRORS, "OR")).hasSize(4);
        assertThat(checkService.runOrChecks(OR_ERRORS, "OR")).extracting("row").containsOnly(1);
        assertThat(checkService.runOrChecks(OR_ERRORS, "OR")).extracting("row", "columnName", "error")
                .contains(tuple(1, "OR", "OR num is empty for association with effect type: OR"),
                          tuple(1, "Beta", "Beta value found for association with effect type: OR"),
                          tuple(1, "Beta unit", "Beta unit found for association with effect type: OR"),
                          tuple(1, "Beta Direction", "Beta direction found for association with effect type: OR"));
    }

    @Test
    public void testRunBetaChecks() throws Exception {

        // No errors
        assertThat(checkService.runBetaChecks(BETA_NO_ERRORS, "Beta")).isEmpty();

        // Errors
        assertThat(checkService.runBetaChecks(BETA_ERRORS, "Beta")).isNotEmpty();
        assertThat(checkService.runBetaChecks(BETA_ERRORS, "Beta")).hasSize(6);
        assertThat(checkService.runBetaChecks(BETA_ERRORS, "Beta")).extracting("row").containsOnly(6);
        assertThat(checkService.runBetaChecks(BETA_ERRORS, "Beta")).extracting("row", "columnName", "error")
                .contains(tuple(6, "Beta", "Beta is empty for association with effect type: Beta"),
                          tuple(6, "Beta Unit", "Beta unit is empty for association with effect type: Beta"),
                          tuple(6, "Beta Direction", "Beta direction is empty for association with effect type: Beta"),
                          tuple(6, "OR", "OR num found for association with effect type: Beta"),
                          tuple(6, "OR reciprocal", "OR reciprocal found for association with effect type: Beta"),
                          tuple(6,
                                "OR reciprocal range",
                                "OR reciprocal range found for association with effect type: Beta"));

        assertThat(checkService.runBetaChecks(BETA_DIRECTION_ERRORS, "Beta")).isNotEmpty();
        assertThat(checkService.runBetaChecks(BETA_DIRECTION_ERRORS, "Beta")).hasSize(1);
        assertThat(checkService.runBetaChecks(BETA_DIRECTION_ERRORS, "Beta")).extracting("row").containsOnly(10);
        assertThat(checkService.runBetaChecks(BETA_DIRECTION_ERRORS, "Beta")).extracting("row", "columnName", "error")
                .contains(tuple(10,
                                "Beta Direction",
                                "Beta direction is not increase or decrease for association with effect type: Beta"));
    }

    @Test
    public void testRunNoEffectErrors() throws Exception {

        // No errors
        assertThat(checkService.runNoEffectErrors(NR_NO_ERRORS, "NR")).isEmpty();

        // Errors
        assertThat(checkService.runNoEffectErrors(NR_ERRORS, "NR")).isNotEmpty();
        assertThat(checkService.runNoEffectErrors(NR_ERRORS, "NR")).hasSize(9);
        assertThat(checkService.runNoEffectErrors(NR_ERRORS, "NR")).extracting("row").containsOnly(4);
        assertThat(checkService.runNoEffectErrors(NR_ERRORS, "NR")).extracting("row", "columnName", "error")
                .contains(tuple(4, "OR", "OR num found for association with effect type: NR"),
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
                                "OR/Beta description found for association with effect type: NR"));
    }
}