package uk.ac.ebi.fgpt.goci.sparql.pussycat.session;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import uk.ac.ebi.fgpt.goci.dao.DefaultOntologyDAO;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSessionFactory;
import uk.ac.ebi.fgpt.goci.sparql.pussycat.query.SparqlTemplate;
import uk.ac.ebi.fgpt.lode.impl.JenaVirtuosoExecutorService;

import java.io.IOException;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
@ServiceProvider
public class SparqlPussycatSessionFactory implements PussycatSessionFactory {
    private static final String efoDefaultLocation = "http://www.ebi.ac.uk/efo/efo.owl";

    private DefaultOntologyDAO ontologyDAO;
    private SparqlTemplate sparqlTemplate;

    public SparqlPussycatSessionFactory() {
        // create ontology DAO using EFO location as environment property
        ResourceLoader loader = new PathMatchingResourcePatternResolver();
        String efoLocationProperty = System.getenv("EFO.LOCATION");
        Resource efoResource;
        if (efoLocationProperty == null) {
            efoResource = loader.getResource(efoDefaultLocation);
        }
        else {
            efoResource = loader.getResource(efoLocationProperty);
        }

        OntologyConfiguration ontologyConfiguration = new OntologyConfiguration();
        ontologyConfiguration.setEfoResource(efoResource);
        try {
            ontologyConfiguration.init();
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to initialize ontology configuration", e);
        }

        // create ontology DAO for EFO
        ontologyDAO = new DefaultOntologyDAO();
        ontologyDAO.setOntologyResource(efoResource);
        ontologyDAO.setOntologyConfiguration(ontologyConfiguration);
        ontologyDAO.init();

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
            sparqlTemplate.setQueryService(executorService);
        }
    }

    public SparqlPussycatSessionFactory(DefaultOntologyDAO ontologyDAO, SparqlTemplate sparqlTemplate) {
        this.ontologyDAO = ontologyDAO;
        this.sparqlTemplate = sparqlTemplate;
    }

    @Override public PussycatSession createPussycatSession() {
        return new SparqlPussycatSession(ontologyDAO, sparqlTemplate);
    }
}
