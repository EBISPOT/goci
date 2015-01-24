package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.UrlResource;
import uk.ac.ebi.spot.goci.exception.SolrIndexingException;
import uk.ac.ebi.spot.goci.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.owl.ReasonedOntologyLoader;
import uk.ac.ebi.spot.goci.service.SolrIndexer;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class SolrIndexerApplication {
    @Autowired SolrIndexer solrIndexer;

    public static void main(String[] args) {
        System.out.println("Starting Solr indexing application...");
        ApplicationContext ctx = SpringApplication.run(SolrIndexerApplication.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            long start_time = System.currentTimeMillis();
            System.out.println("Building indexes with supplied params: " + Arrays.toString(strings));
            solrIndexer.enableSysOutLogging();
            System.out.print("Converting all GWAS database objects...");
            int docCount = solrIndexer.fetchAndIndex();
            System.out.println("done!\n");
            long end_time = System.currentTimeMillis();
            String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
            System.out.println("Successfully mapped " + docCount + " documents into the GWAS solr index\n");
            System.out.println("Indexing building complete in " + time + " s. - application will now exit");
        };
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
