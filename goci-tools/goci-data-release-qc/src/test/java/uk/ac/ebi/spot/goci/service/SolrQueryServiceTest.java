package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import java.net.URL;

import static org.junit.Assert.assertEquals;
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
        solrQueryService.setServer(new URL("http://snoopy.ebi.ac.uk:8985/solr/gwas"));
    }

    @Test
    public void testGetLastReleaseDate() throws Exception{
        assertNotNull(solrQueryService.getLastReleaseDate());
    }



    @Test
    public void testBuildSolrQuery() throws Exception{
        assertNotNull(solrQueryService.buildSolrQuery("2016-06-01"));
        assertEquals("http://snoopy.ebi.ac.uk:8985/solr/gwas/select?q=catalogPublishDate%3A%7B2016-06-01T00%3A00%3A00Z+TO+*%5D&fq=resourcename%3Astudy&wt=json",
                     solrQueryService.buildSolrQuery("2016-06-01"));
    }

//    @Test
//    public void testQuerySolr() throws Exception{
//        String query = "http://snoopy.ebi.ac.uk:8985/solr/gwas/select?q=catalogPublishDate%3A%7B2016-06-01T00%3A00%3A00Z+TO+*%5D&fq=resourcename%3Astudy&wt=json";
//
//        assertNotNull(solrQueryService.querySolr(query));
//    }
}
