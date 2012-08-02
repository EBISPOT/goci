package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.pussycat.ReasonerSessionBasedReasonerProxy;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.io.IOException;
import java.util.Collection;

/**
 * A pussycat session that acts as a proxy over a concrete implementation, but caches (to disk) SVG documents that are
 * rendered on demand.
 * <p/>
 * The caching strategy is to take the supplied home directory and create within this directory a uniquely named file
 * for each request to {@link #performRendering(org.semanticweb.owlapi.model.OWLClassExpression,
 * uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus)}.  This implementation does this by creating a hash of the
 * supplied arguments and saving the SVG output to this file.  It is possible to reacquire documents from this cache on
 * repeated requests, effectively creating a disk-based "lazy-load" strategy for SVG rendering.
 *
 * @author Tony Burdett
 * @date 02/08/12
 */
public class SVGCachingPussycatSession extends AbstractSVGIOPussycatSession implements PussycatSession {
    private GOCIDataPublisherPussycatSession proxiedSession;
    private ReasonerSessionBasedReasonerProxy reasonerProxy;

    public GOCIDataPublisherPussycatSession getProxiedSession() {
        return proxiedSession;
    }

    public void setProxiedSession(GOCIDataPublisherPussycatSession proxiedSession) {
        this.proxiedSession = proxiedSession;
    }

    @Override public String getSessionID() {
        return getProxiedSession().getSessionID();
    }

    @Override public Collection<Renderlet> getAvailableRenderlets() {
        return getProxiedSession().getAvailableRenderlets();
    }

    @Override public String performRendering(OWLClassExpression classExpression, RenderletNexus renderletNexus)
            throws PussycatSessionNotReadyException {
        String filename = generateFilename(classExpression);
        String svg;
        try {
            if (isInCache(filename)) {
                // this document already exists in cache, load document
                getLog().debug("Reusing cached SVG file for '" + classExpression + "' (file " + filename + ")");
                svg = readSVG(filename);
            }
            else {
                // need to perform rendering, delegate to proxy
                getLog().debug("No cached SVG file for '" + classExpression + "' " +
                                       "(filename expected: " + filename + "), delegating request");
                svg = getProxiedSession().performRendering(classExpression, renderletNexus);
                // and write the svg to disk
                writeSVG(filename, svg);
            }
            return svg;
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read SVG from cache (" + e.getMessage() + ")", e);
        }
    }

    @Override public boolean clearRendering() {
        try {
            clearCache();
            return true;
        }
        catch (Exception e) {
            getLog().error("Failed to empty cache: " + e.getMessage(), e);
            return false;
        }
    }

    @Override public OWLReasoner getReasoner() throws OWLConversionException, PussycatSessionNotReadyException {
        if (reasonerProxy == null) {
            reasonerProxy = new ReasonerSessionBasedReasonerProxy(getProxiedSession().getReasonerSession());
        }
        return reasonerProxy;
    }
}
