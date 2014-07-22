package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * A pussycat session that acts as a proxy over a disk based cache of SVG documents, but performs no actual rendering
 * itself.
 * <p/>
 * The caching strategy is to take the supplied home directory and, within this directory, look for a uniquely named
 * file for each request to {@link #performRendering(org.semanticweb.owlapi.model.OWLClassExpression,
 * uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus)}.  This implementation does this by creating a hash of the
 * supplied arguments and loading an SVG file with the same name as the hash, if it exists.  If no file with this name
 * exists, and exception is raised.
 * <p/>
 * This implementation is a good one to use in live, production environments as it means it is possible to pre-calculate
 * SVG-based views somewhere else, caching them to disk and copying them to a production enviroment.  This massively
 * reduces the computational workload for the live version.
 * <p/>
 * All pussycat sessions need to support some reasoner requests, however, to enable the clients to perform subclass
 * requests.  This implementation does not load any data, but will load and use EFO to support reasoner queries.
 * However, if any instance level requests are made no instances will be returned.
 *
 * @author Tony Burdett
 * @date 02/08/12
 */
public class SVGLoadingPussycatSession extends AbstractSVGIOPussycatSession {
    private ReasonerSession reasonerSession;

    public ReasonerSession getReasonerSession() {
        return reasonerSession;
    }

    public void setReasonerSession(ReasonerSession reasonerSession) {
        this.reasonerSession = reasonerSession;
    }

    @Override public Collection<Renderlet> getAvailableRenderlets() {
        return Collections.emptyList();
    }

    @Override public String performRendering(OWLClassExpression classExpression, RenderletNexus renderletNexus)
            throws PussycatSessionNotReadyException {
        String filename = generateFilename(classExpression.toString());
        String svg;
        try {
            if (isInCache(filename)) {
                // this document already exists in cache, load document
                getLog().debug("Reusing cached SVG file for '" + classExpression + "' (file " + filename + ")");
                svg = readSVG(filename);
            }
            else {
                getLog().debug("Expected cached SVG file '" + filename + "' but this file is absent");
                throw new PussycatSessionNotReadyException("The requested view is not currently available.");
            }
            return svg;
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read SVG from cache (" + e.getMessage() + ")", e);
        }
    }

    @Override public boolean clearRendering() {
        throw new UnsupportedOperationException("This operation is not available in this implementation");
    }

    @Override public OWLReasoner getReasoner() throws OWLConversionException, PussycatSessionNotReadyException {
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
