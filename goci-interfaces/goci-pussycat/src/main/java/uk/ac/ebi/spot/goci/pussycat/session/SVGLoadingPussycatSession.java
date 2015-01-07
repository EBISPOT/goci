package uk.ac.ebi.spot.goci.pussycat.session;

import uk.ac.ebi.spot.goci.lang.Filter;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.spot.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A pussycat session that acts as a proxy over a disk based cache of SVG documents, but performs no actual rendering
 * itself.
 * <p/>
 * The caching strategy is to take the supplied home directory and, within this directory, look for a uniquely named
 * file for each request to {@link #performRendering(uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus,
 * uk.ac.ebi.spot.goci.lang.Filter[])}.  This implementation does this by creating a hash of the supplied arguments and
 * loading an SVG file with the same name as the hash, if it exists.  If no file with this name exists, and exception is
 * raised.
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
    @Override public Collection<Renderlet> getAvailableRenderlets() {
        return Collections.emptyList();
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
                getLog().debug("Expected cached SVG file '" + filename + "' but this file is absent");
                throw new PussycatSessionNotReadyException("The requested view is not currently available.");
            }
            return svg;
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read SVG from cache (" + e.getMessage() + ")", e);
        }
    }

    @Override public List<AssociationSummary> getAssociationSummaries(List<URI> associationURIs) {
        // todo - implement this!
        throw new UnsupportedOperationException("This pussycat instance does not have access to association summary " +
                                                        "data");
    }

    @Override public Set<URI> getRelatedTraits(String traitName) {
        // todo - implement this!
        throw new UnsupportedOperationException("This pussycat instance does not have access to related trait " +
                                                        "information");
    }
}
