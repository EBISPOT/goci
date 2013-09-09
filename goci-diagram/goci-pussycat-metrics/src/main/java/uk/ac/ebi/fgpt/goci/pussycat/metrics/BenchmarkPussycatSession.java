package uk.ac.ebi.fgpt.goci.pussycat.metrics;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.session.AbstractSVGIOPussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.ReasonerSession;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 18/09/12
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkPussycatSession extends AbstractSVGIOPussycatSession {
    private Collection<Renderlet> renderlets;

    private ReasonerSession reasonerSession;
    private OntologyConfiguration ontologyConfiguration;

    private Logger bm_log = LoggerFactory.getLogger("benchmark.output.log");

    public BenchmarkPussycatSession(){
        super();

        // set up this session
        this.renderlets = getAvailableRenderlets();

    }


    @Override
    public Collection<Renderlet> getAvailableRenderlets() {
        if (renderlets == null) {
            ServiceLoader<Renderlet> renderletLoader = ServiceLoader.load(Renderlet.class);
            Collection<Renderlet> loadedRenderlets = new HashSet<Renderlet>();
            for (Renderlet renderlet : renderletLoader) {
                loadedRenderlets.add(renderlet);
            }
            getLog().debug("Loaded " + loadedRenderlets.size() + " renderlets");
            return loadedRenderlets;
        }
        else {
            return renderlets;
        }
    }

    public ReasonerSession getReasonerSession() {
        return reasonerSession;
    }

    public void setReasonerSession(ReasonerSession reasonerSession) {
        this.reasonerSession = reasonerSession;
    }

    public OntologyConfiguration getOntologyConfiguration() {
        return ontologyConfiguration;
    }

    public void setOntologyConfiguration(OntologyConfiguration ontologyConfiguration) {
        this.ontologyConfiguration = ontologyConfiguration;
    }


    public String performRendering(OWLClassExpression classExpression, RenderletNexus renderletNexus)
            throws PussycatSessionNotReadyException {

            bm_log.info("Rendering request: rendering SVG representing '" + classExpression + "'...");
            long start, end;
            start = System.currentTimeMillis();
            String svg = renderletNexus.getSVG(classExpression);
            try {
                writeSVG("gwas-diagram.svg", svg);
            }
            catch (Exception e) {
                getLog().debug("Failed to write out generated SVG: " + e.getMessage());
            }
            end = System.currentTimeMillis();
            double time = ((double) (end - start)) / 1000;
            bm_log.info("Rendering for class expression " + classExpression + " complete in  " + time + " s.  ");

            return svg;
    }

    @Override
    public boolean clearRendering() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OWLReasoner getReasoner() throws OWLConversionException, PussycatSessionNotReadyException {
        if (getReasonerSession().isReasonerInitialized()) {
            getLog().debug("Pussycat Session '" + getSessionID() + "' is fully initialized and ready to serve data");
            return getReasonerSession().getReasoner();
        }
        else {
            getLog().debug("Pussycat Session '" + getSessionID() + "' is not yet initialized - waiting for reasoner");
            throw new PussycatSessionNotReadyException("Reasoner is being initialized");
        }
    }
}
