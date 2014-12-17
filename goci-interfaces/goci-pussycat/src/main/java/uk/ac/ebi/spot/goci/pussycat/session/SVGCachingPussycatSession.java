package uk.ac.ebi.spot.goci.pussycat.session;

import uk.ac.ebi.spot.goci.lang.Filter;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.spot.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A pussycat session that acts as a proxy over a concrete implementation, but caches (to disk) SVG documents that are
 * rendered on demand.
 * <p/>
 * The caching strategy is to take the supplied home directory and create within this directory a uniquely named file
 * for each request to {@link #performRendering(uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus,
 * uk.ac.ebi.spot.goci.lang.Filter[])}.  This implementation does this by creating a hash of the supplied arguments and
 * saving the SVG output to this file.  It is possible to reacquire documents from this cache on repeated requests,
 * effectively creating a disk-based "lazy-load" strategy for SVG rendering.
 *
 * @author Tony Burdett
 * @date 02/08/12
 */
public class SVGCachingPussycatSession extends AbstractSVGIOPussycatSession implements PussycatSession {
    private PussycatSession proxiedSession;

    public PussycatSession getProxiedSession() {
        return proxiedSession;
    }

    public void setProxiedSession(PussycatSession proxiedSession) {
        this.proxiedSession = proxiedSession;
    }

    @Override public String getSessionID() {
        return getProxiedSession().getSessionID();
    }

    @Override public Collection<Renderlet> getAvailableRenderlets() {
        return getProxiedSession().getAvailableRenderlets();
    }

    @Override public String performRendering(RenderletNexus renderletNexus, Filter... filters)
            throws PussycatSessionNotReadyException {
        String filename = generateFilename(filters);
        String svg;
        try {
            if (isInCache(filename)) {
                // this document already exists in cache, load document
                getLog().debug("Reusing cached SVG file for the supplied filters (file " + filename + ")");
                svg = readSVG(filename);
            }
            else {
                // need to perform rendering, delegate to proxy
                getLog().debug("No cached SVG file for the supplied filters " +
                                       "(filename expected: " + filename + "), delegating request");
                svg = getProxiedSession().performRendering(renderletNexus, filters);
                // and write the svg to disk
                writeSVG(filename, svg);
            }
            return svg;
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read SVG from cache (" + e.getMessage() + ")", e);
        }
    }

    @Override public List<AssociationSummary> getAssociationSummaries(List<URI> associationURIs) {
        // delegates to underlying session to retrieve actual data
        return getProxiedSession().getAssociationSummaries(associationURIs);
    }

    @Override public Set<URI> getRelatedTraits(String traitName) {
        // delegates to underlying session to retrieve actual data
        return getProxiedSession().getRelatedTraits(traitName);
    }
}
