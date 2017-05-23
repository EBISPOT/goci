package uk.ac.ebi.spot.goci;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.ontology.config.OntologyLoaderConfig;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.ontology.owl.ReasonedOntologyLoader;

import javax.validation.constraints.NotNull;

//import org.apache.solr.client.solrj.SolrServer;
//import org.apache.solr.client.solrj.impl.HttpSolrServer;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 26/01/15
 */
@Component
public class SolrIndexerConfiguration {
    @NotNull @Value("${search.server}")
    private String solrClient;

    @NotNull @Value("${efo.location}")
    private Resource efoResource;

    @Autowired
    OntologyLoaderConfig ontologyLoaderConfig;


    @Bean SolrClient solrClient() {
        // return new http solr server from "search.server" config element, but remove core name (probably /gwas)
        //updated from SolrServer to SolrClient followind deprecation of SolrServer
        return new HttpSolrClient(solrClient.substring(0, solrClient.lastIndexOf('/')));
    }

    @Bean OntologyLoader ontologyLoader() {
        ReasonedOntologyLoader loader = ontologyLoaderConfig.setEfoOntologyLoader(efoResource);
        return loader;
    }
}
