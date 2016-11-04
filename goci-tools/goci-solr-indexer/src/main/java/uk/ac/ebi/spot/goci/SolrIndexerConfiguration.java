package uk.ac.ebi.spot.goci;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
//import org.apache.solr.client.solrj.SolrServer;
//import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.ontology.owl.ReasonedOntologyLoader;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Collections;

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

//    @Bean SolrServer solrServer() {
//        // return new http solr server from "search.server" config element, but remove core name (probably /gwas)
//        return new HttpSolrServer(solrServer.substring(0, solrServer.lastIndexOf('/')));
//    }

    @Bean SolrClient solrClient() {
        // return new http solr server from "search.server" config element, but remove core name (probably /gwas)
        //updated from SolrServer to SolrClient followind deprecation of SolrServer
        return new HttpSolrClient(solrClient.substring(0, solrClient.lastIndexOf('/')));
    }

    @Bean OntologyLoader ontologyLoader() {
        ReasonedOntologyLoader loader = new ReasonedOntologyLoader();
        loader.setOntologyName("efo");
        loader.setOntologyURI(URI.create("http://www.ebi.ac.uk/efo"));
        loader.setOntologyResource(efoResource);
        loader.setExclusionClassURI(URI.create("http://www.geneontology.org/formats/oboInOwl#ObsoleteClass"));
        loader.setExclusionAnnotationURI(URI.create("http://www.ebi.ac.uk/efo/organizational_class"));
        loader.setSynonymURIs(Collections.singleton(URI.create("http://www.ebi.ac.uk/efo/alternative_term")));
        loader.init();
        return loader;
    }
}
