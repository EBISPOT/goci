package uk.ac.ebi.fgpt.goci.pussycat.session;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import uk.ac.ebi.fgpt.goci.lang.OWLAPIFilterInterpreter;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.pussycat.reasoning.KnowledgeBaseLoadingReasonerSession;
import uk.ac.ebi.fgpt.goci.pussycat.reasoning.ReasonerSession;

import java.io.IOException;

/**
 * Loads EFO, for trait mappings, and a GWAS knowledgebase containing all the GWAS data that should be rendered.  You
 * can control where EFO and the GWAS knowledgebase are loaded from by setting the environmental variables EFO_LOCATION
 * and GWAS_KB_LOCATION before running Pussycat.  By default, EFO_LOCATION is set to 'http://www.ebi.ac.uk/efo'.  You
 * must always supply the GWAS location.
 *
 * @author Tony Burdett
 * @date 28/07/14
 */
@ServiceProvider
public class OWLPussycatSessionFactory implements PussycatSessionFactory {
    private static final String efoDefaultLoaction = "http://www.ebi.ac.uk/efo/efo.owl";

    private final OntologyConfiguration ontologyConfiguration;
    private final OWLAPIFilterInterpreter filterInterpreter;

    private ReasonerSession reasonerSession;

    public OWLPussycatSessionFactory() {
        ResourceLoader loader = new PathMatchingResourcePatternResolver();

        // get EFO, GWAS KB locations from system properties
        String efoLocationProperty = System.getenv("EFO.LOCATION");
        Resource efoResource;
        if (efoLocationProperty == null) {
            efoResource = loader.getResource(efoDefaultLoaction);
        }
        else {
            efoResource = loader.getResource(efoLocationProperty);
        }

        String gwasLocationProperty = System.getenv("GWAS.KB.LOCATION");
        Resource gwasKBResource;
        if (gwasLocationProperty == null) {
            throw new IllegalStateException("GWAS.KB.LOCATION environment variable not set - " +
                                                    "please set this to the location of the GWAS OWL knowledgebase file");
        }
        else {
            gwasKBResource = loader.getResource(gwasLocationProperty);
        }

        ontologyConfiguration = new OntologyConfiguration();
        ontologyConfiguration.setEfoResource(efoResource);
        ontologyConfiguration.setGwasDiagramSchemaResource(gwasKBResource);
        try {
            ontologyConfiguration.init();
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to initialize ontology configuration", e);
        }

        // finally, set the filter interpreter
        filterInterpreter = new OWLAPIFilterInterpreter(ontologyConfiguration);
    }

    @Override public synchronized PussycatSession createPussycatSession() {
        // create a new reasoner session to set on the pussycat session
        if (reasonerSession == null) {
            reasonerSession = createReasonerSession();
        }

        return new OWLPussycatSession(filterInterpreter, ontologyConfiguration, reasonerSession);
    }

    private ReasonerSession createReasonerSession() {
        // NB: we're only worrying about loading pre-generated knowledgebases, but we could use a data publisher
        // From old spring config:
        //            <!-- NB: which implementation to use depends on whether
        //        a) we are dynamically generating the knowledgebase (uk.ac.ebi.fgpt.goci.pussycat.reasoning.DataPublishingCacheableReasonerSession) or
        //        b) statically loading a precanned version (uk.ac.ebi.fgpt.goci.pussycat.reasoning.OntologyLoadingCacheableReasonerSession) or
        //        c) never recalculating SVG and only loading from cache (uk.ac.ebi.fgpt.goci.pussycat.reasoning.OntologyReasonerSession) -->
        //        <bean id="reasonerSession"
        //        class="uk.ac.ebi.fgpt.goci.pussycat.reasoning.OntologyReasonerSession"
        //        init-method="init">
        //        <property name="configuration" ref="config" />
        //        </bean>
        KnowledgeBaseLoadingReasonerSession rs = new KnowledgeBaseLoadingReasonerSession();
        rs.setConfiguration(ontologyConfiguration);
        rs.init();
        return rs;
    }
}
