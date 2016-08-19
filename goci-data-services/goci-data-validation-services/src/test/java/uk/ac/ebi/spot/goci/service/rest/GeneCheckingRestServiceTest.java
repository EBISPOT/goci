package uk.ac.ebi.spot.goci.service.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.TestRestTemplate;
import uk.ac.ebi.spot.goci.utils.RestUrlBuilder;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 28/04/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneCheckingRestServiceTest {

    private GeneCheckingRestService geneCheckingRestService;

    @Mock
    private RestUrlBuilder restUrlBuilder;

    private static final String ENDPOINT = "/lookup/symbol/homo_sapiens/";

    private static final String VALID_GENE = "MC4R";

    private static final String VALID_RESPONSE =
            "http://rest.ensembl.org/lookup/symbol/homo_sapiens/MC4R?content-type=application/json";

    private static final String INVALID_GENE = "BAD34";

    private static final String ERROR_RESPONSE =
            "http://rest.ensembl.org/lookup/symbol/homo_sapiens/BAD34?content-type=application/json";

    @Before
    public void setUp() throws Exception {
        geneCheckingRestService = new GeneCheckingRestService(restUrlBuilder);
        geneCheckingRestService.setEndpoint(ENDPOINT);
        when(restUrlBuilder.getRestTemplate()).thenReturn(new TestRestTemplate());
    }

    @Test
    public void checkGeneSymbolIsValid() throws Exception {
        when(restUrlBuilder.createUrl(ENDPOINT, VALID_GENE)).thenReturn(VALID_RESPONSE);
        assertNull(geneCheckingRestService.checkGeneSymbolIsValid(VALID_GENE));
    }

    @Test
    public void checkGeneSymbolIsValidForInvalidGeneName() throws Exception {
        when(restUrlBuilder.createUrl(ENDPOINT, INVALID_GENE)).thenReturn(ERROR_RESPONSE);
        assertEquals("Gene symbol BAD34 is not valid", geneCheckingRestService.checkGeneSymbolIsValid(INVALID_GENE));
    }

    @Test
    public void getGeneLocation() throws Exception {
        when(restUrlBuilder.createUrl(ENDPOINT, VALID_GENE)).thenReturn(VALID_RESPONSE);

        String geneChromosome = geneCheckingRestService.getGeneLocation(VALID_GENE);
        assertThat(geneChromosome).isNotEmpty();
    }

    @Test
    public void getGeneLocationForInvalidGeneName() throws Exception {
        when(restUrlBuilder.createUrl(ENDPOINT, INVALID_GENE)).thenReturn(ERROR_RESPONSE);

        String geneChromosome = geneCheckingRestService.getGeneLocation(INVALID_GENE);
        assertThat(geneChromosome).isNull();
    }
}