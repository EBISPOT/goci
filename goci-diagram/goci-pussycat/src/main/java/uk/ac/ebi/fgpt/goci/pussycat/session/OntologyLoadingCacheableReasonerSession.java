package uk.ac.ebi.fgpt.goci.pussycat.session;

import com.googlecode.ehcache.annotations.Cacheable;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.Initializable;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.service.GWASOWLPublisher;

import java.io.IOException;
import java.net.URI;

/**
 * /** A reasoner session that uses loads an OWL ontology from the provided resource, and then uses the GOCI
 * DataPublisher to generate the inferred view using the reasoning mechanisms provided by that data publisher.
 * <p/>
 * The resulting reasoner is cached in-memory using ehcache to provide caching functionality.
 *
 * @author Tony Burdett
 * @date 13/04/12
 */
public class OntologyLoadingCacheableReasonerSession extends Initializable implements ReasonerSession {
    private OntologyConfiguration configuration;
    private Resource ontologyResource;
    private GWASOWLPublisher publisher;
    private OWLReasoner reasoner;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override
    protected void doInitialization() throws Exception {
        getLog().debug("Initializing reasoner session, this may take some time...");
        getReasoner();
    }

    public OntologyConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(OntologyConfiguration configuration) {
        this.configuration = configuration;
    }

    public Resource getOntologyResource() {
        return ontologyResource;
    }

    public void setOntologyResource(Resource ontologyResource) {
        this.ontologyResource = ontologyResource;
    }

    public GWASOWLPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(GWASOWLPublisher publisher) {
        this.publisher = publisher;
    }

    @Override public boolean isReasonerInitialized() {
        return isReady();
    }

    @Override
    @Cacheable(cacheName = "reasonerCache")
    public OWLReasoner getReasoner() throws OWLConversionException {
        try {
            if (reasoner == null) {
                URI gwasDataURI = getOntologyResource().getURI();
                getLog().info("Loading GWAS data from " + gwasDataURI);
                OWLOntology gwasData = getConfiguration().getOWLOntologyManager().loadOntology(IRI.create(gwasDataURI));
                getLog().debug("Publishing GWAS data (inferred view)");
                reasoner = getPublisher().publishGWASDataInferredView(gwasData);
            }
            else {
                getLog().warn("Failed to retrieve reasoner from ehcache, returning reference");
            }
            return reasoner;
        }
        catch (IOException e) {
            throw new OWLConversionException("Failed to load ontology resource", e);
        }
        catch (OWLOntologyCreationException e) {
            throw new OWLConversionException("Failed to load ontology resource", e);
        }
    }
}
