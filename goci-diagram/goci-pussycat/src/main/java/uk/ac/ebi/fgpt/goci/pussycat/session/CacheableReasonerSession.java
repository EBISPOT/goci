package uk.ac.ebi.fgpt.goci.pussycat.session;

import com.googlecode.ehcache.annotations.Cacheable;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.Initializable;
import uk.ac.ebi.fgpt.goci.service.GWASOWLPublisher;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 05/03/12
 */
public class CacheableReasonerSession extends Initializable implements ReasonerSession {
    private GWASOWLPublisher publisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override
    protected void doInitialization() throws Exception {
        getLog().info("Initializing reasoner session... this may take some time");
        getReasoner();
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
        getLog().info("Publishing GWAS data");
        OWLOntology gwasData = getPublisher().publishGWASData();
        getLog().info("Publishing GWAS data (inferred view)");
        return getPublisher().publishGWASDataInferredView(gwasData);
    }
}
