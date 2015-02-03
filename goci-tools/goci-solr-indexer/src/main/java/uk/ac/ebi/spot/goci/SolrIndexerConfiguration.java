package uk.ac.ebi.spot.goci;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.exception.SolrIndexingException;
import uk.ac.ebi.spot.goci.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.owl.ReasonedOntologyLoader;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
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
    private String solrServer;

    @Bean SolrServer solrServer() {
        return new HttpSolrServer(solrServer);
    }

    @Bean OntologyLoader ontologyLoader() {
        try {
            ReasonedOntologyLoader loader = new ReasonedOntologyLoader();
            loader.setOntologyName("efo");
            loader.setOntologyURI(URI.create("http://www.ebi.ac.uk/efo"));
            loader.setOntologyResource(new UrlResource("http://www.ebi.ac.uk/efo/efo.owl"));
            loader.setExclusionClassURI(URI.create("http://www.geneontology.org/formats/oboInOwl#ObsoleteClass"));
            loader.setExclusionAnnotationURI(URI.create("http://www.ebi.ac.uk/efo/organizational_class"));
            loader.setSynonymURIs(Collections.singleton(URI.create("http://www.ebi.ac.uk/efo/alternative_term")));
            loader.init();
            return loader;
        }
        catch (MalformedURLException e) {
            throw new SolrIndexingException("Failed to load ontology", e);
        }
    }

}
