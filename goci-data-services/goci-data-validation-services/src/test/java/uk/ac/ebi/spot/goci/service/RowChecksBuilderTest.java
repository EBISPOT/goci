package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationUploadRowBuilder;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

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

    private static final AssociationUploadRow ROW_WITH_SNP =
            new AssociationUploadRowBuilder().setRowNumber(1).setStrongestAllele("rs123456-?").build();

    private static final AssociationUploadRow ROW_WITH_RA =
            new AssociationUploadRowBuilder().setRowNumber(1).setSnp("rs123456").build();

    @Before
    public void setUp() throws Exception {
        rowChecksBuilder = new RowChecksBuilder(checkingService);
    }

    @Test
    public void testRunEmptyValueChecks() throws Exception {

        // TODO MOCK RETURN TYPE
        // TODO NEED TO BUILD ERROR

 //    when(checkingService.checkSnpValueIsPresent(EMPTY_ROW)).thenReturn();

        assertThat(rowChecksBuilder.runEmptyValueChecks(EMPTY_ROW)).hasSize(2);
        assertThat(rowChecksBuilder.runEmptyValueChecks(EMPTY_ROW)).extracting("field", "error")
                .contains(tuple(
                        "SNP",
                        "Missing value"), tuple("Strongest SNP-Risk Allele/Effect Allele", "Missing value"));

    }
}