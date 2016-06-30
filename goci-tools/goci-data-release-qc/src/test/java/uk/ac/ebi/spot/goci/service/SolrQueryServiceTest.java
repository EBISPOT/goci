package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Dani on 30/06/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class SolrQueryServiceTest {

    private SolrQueryService solrQueryService;

    @Before
    public void setUp() throws Exception{
        solrQueryService = new SolrQueryService();
        solrQueryService.setCatalogStatsFile(new ClassPathResource("catalog-stats-test.properties"));
    }

    @Test
    public void testGetLastReleaseDate() throws Exception{
        assertNotNull(solrQueryService.getLastReleaseDate());
    }
}
