package uk.ac.ebi.spot.goci.sparql.pussycat.session;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import uk.ac.ebi.fgpt.lode.impl.JenaVirtuosoExecutorService;
import uk.ac.ebi.spot.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.spot.goci.pussycat.session.PussycatSessionFactory;
import uk.ac.ebi.spot.goci.pussycat.service.OntologyService;
import uk.ac.ebi.spot.goci.sparql.pussycat.query.SparqlTemplate;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
@ServiceProvider
@Component
public class SparqlPussycatSessionFactory implements PussycatSessionFactory {
    private static final String efoDefaultLocation = "http://www.ebi.ac.uk/efo/efo.owl";

//    @Autowired
    private OntologyService ontologyService;

//    @Autowired
    private SparqlTemplate sparqlTemplate;

    @Autowired(required = false)
    public SparqlPussycatSessionFactory(OntologyService ontologyService) {
        // create ontology Service using EFO location as environment property
        ResourceLoader loader = new PathMatchingResourcePatternResolver();
        String efoLocationProperty = System.getenv("EFO.LOCATION");
        Resource efoResource;
        if (efoLocationProperty == null) {
            efoResource = loader.getResource(efoDefaultLocation);
        }
        else {
            efoResource = loader.getResource(efoLocationProperty);
        }

        // create ontology Service for EFO
        this.ontologyService = ontologyService;


        // get SPARQL endpoint location from system properties
        String gwasSparqlUrl = System.getenv("GWAS.SPARQL");
        if (gwasSparqlUrl == null) {
            throw new IllegalStateException("GWAS.SPARQL environment variable not set - " +
                                                    "please set this to the full path of the GWAS Catalog " +
                                                    "SPARQL endpoint");
        }
        else {
            JenaVirtuosoExecutorService executorService = new JenaVirtuosoExecutorService();
            executorService.setEndpointURL(gwasSparqlUrl);
            sparqlTemplate = new SparqlTemplate();
            sparqlTemplate.setJenaQueryExecutionService(executorService);
        }
    }

    @Autowired(required = false)
    public SparqlPussycatSessionFactory(OntologyService ontologyService, SparqlTemplate sparqlTemplate) {
        this.ontologyService = ontologyService;
        this.sparqlTemplate = sparqlTemplate;
    }

    @Override public PussycatSession createPussycatSession() {
//        return new SparqlPussycatSession(ontologyService, sparqlTemplate);
        return new SparqlPussycatSession();
    }
}
