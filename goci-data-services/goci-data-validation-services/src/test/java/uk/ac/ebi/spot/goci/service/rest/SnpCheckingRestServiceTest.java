package uk.ac.ebi.spot.goci.service.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.TestRestTemplate;
import uk.ac.ebi.spot.goci.service.EnsemblRestTemplateService;
import uk.ac.ebi.spot.goci.service.EnsemblRestcallHistoryService;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;


/**
 * Created by emma on 27/04/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class SnpCheckingRestServiceTest {

    private SnpCheckingRestService snpCheckingRestService;

    @Mock
    private EnsemblRestTemplateService ensemblRestTemplateService;

    @Mock
    private EnsemblRestcallHistoryService ensemblRestcallHistoryService;

    private static final String VALID_RSID = "rs12967135";

    private static final String INVALID_RSID = "SNP_A-2171106";

    private static final String eRelease = "";

    private static final String ENDPOINT = "/variation/homo_sapiens/";

    private static final String URL1 =
            "http://rest.ensembl.org//variation/homo_sapiens/rs12967135?content-type=application/json";

    private static final String URL2 =
            "http://rest.ensembl.org//variation/homo_sapiens/SNP_A-2171106?content-type=application/json";

    @Before
    public void setUp() throws Exception {
        snpCheckingRestService = new SnpCheckingRestService(ensemblRestTemplateService, ensemblRestcallHistoryService);
        snpCheckingRestService.setEndpoint(ENDPOINT);
        when(ensemblRestTemplateService.getRestTemplate()).thenReturn(new TestRestTemplate());
    }

    @Test
    public void checkSnpIdentifierIsValid() throws Exception {
        //when(ensemblRestTemplateService.createUrl(ENDPOINT, VALID_RSID)).thenReturn(URL1);
        //assertNull(snpCheckingRestService.checkSnpIdentifierIsValid(VALID_RSID, eRelease));
    }
/*
    @Test
    public void checkSnpIdentifierIsValidWithInvalidSnp() throws Exception {
        when(ensemblRestTemplateService.createUrl(ENDPOINT, INVALID_RSID)).thenReturn(URL2);
        assertEquals("SNP identifier SNP_A-2171106 is not valid",
                     snpCheckingRestService.checkSnpIdentifierIsValid(INVALID_RSID, eRelease));
    }

    @Test
    public void getSnpLocations() throws Exception {
        when(ensemblRestTemplateService.createUrl(ENDPOINT, VALID_RSID)).thenReturn(URL1);

        Set<String> snpLocations = snpCheckingRestService.getSnpLocations(VALID_RSID,eRelease);
        assertThat(snpLocations).isNotEmpty();
    }

    @Test
    public void getSnpLocationsForInvalidSnp() throws Exception {
        when(ensemblRestTemplateService.createUrl(ENDPOINT, INVALID_RSID)).thenReturn(URL2);

        Set<String> snpLocations = snpCheckingRestService.getSnpLocations(INVALID_RSID,eRelease);
        assertThat(snpLocations).isEmpty();
    }
    */
}