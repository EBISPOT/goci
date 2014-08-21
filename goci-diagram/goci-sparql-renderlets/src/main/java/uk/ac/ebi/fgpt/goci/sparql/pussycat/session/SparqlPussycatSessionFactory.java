package uk.ac.ebi.fgpt.goci.sparql.pussycat.session;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSessionFactory;
import uk.ac.ebi.fgpt.goci.sparql.pussycat.query.SparqlTemplate;
import uk.ac.ebi.fgpt.lode.impl.JenaVirtuosoExecutorService;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
@ServiceProvider
public class SparqlPussycatSessionFactory implements PussycatSessionFactory {
    private SparqlTemplate sparqlTemplate;

    public SparqlPussycatSessionFactory() {
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

    public SparqlPussycatSessionFactory(SparqlTemplate sparqlTemplate) {
        this.sparqlTemplate = sparqlTemplate;
    }

    @Override public PussycatSession createPussycatSession() {
        return new SparqlPussycatSession(sparqlTemplate);
    }
}
