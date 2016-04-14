package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.component.ValidationChecks;
import uk.ac.ebi.spot.goci.model.Association;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 14/04/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckingServiceTest {

    @Mock
    private ValidationChecks validationChecks;

    private CheckingService checkingService;

    private Association ASS_BAD_PVALUE = new AssociationBuilder().setId((long) 100).setPvalueMantissa(-99).build();

    @Before
    public void setUp() throws Exception {
        checkingService = new CheckingService(validationChecks);
    }

    @Test
    public void testRunPvalueChecks() throws Exception {

        when(validationChecks.checkMantissaIsLessThan10(ASS_BAD_PVALUE.getPvalueMantissa())).thenReturn("P-value mantisaa not valid");

        assertThat(checkingService.runPvalueChecks(ASS_BAD_PVALUE)).isNotEmpty();
        assertThat(checkingService.runPvalueChecks(ASS_BAD_PVALUE)).hasSize(1);
        assertThat(checkingService.runPvalueChecks(ASS_BAD_PVALUE)).extracting("columnName",
                                                                               "error")
                .contains(tuple( "P-value Mantissa", "P-value mantisaa not valid"));
    }

    @Test
    public void testRunAnnotationChecks() throws Exception {

    }

    @Test
    public void testRunOrChecks() throws Exception {

    }

    @Test
    public void testRunBetaChecks() throws Exception {

    }

    @Test
    public void testRunNoEffectErrors() throws Exception {

    }
}