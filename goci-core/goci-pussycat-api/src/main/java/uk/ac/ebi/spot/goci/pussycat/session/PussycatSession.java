package uk.ac.ebi.spot.goci.pussycat.session;

import uk.ac.ebi.spot.goci.pussycat.exception.NoRenderableDataException;
import uk.ac.ebi.spot.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.spot.goci.pussycat.lang.Filter;
import uk.ac.ebi.spot.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

/**
 * A Pussycat session maintains the loaded data required by Pussycat in order to render an informative display to the
 * user(s).  One Pussycat session may be shared across multiple user http sessions.
 *
 * @author Tony Burdett Date 27/02/12
 */
public interface PussycatSession {
    /**
     * A unique ID for this pussycat session.  This session ID can be used if multiple users should be sharing the same
     * session
     *
     * @return a unique session id
     */
    String getSessionID();

    /**
     * Returns a collection of all available renderlets.  Whether available renderlets are instantiated on demand or
     * returned from a cache depends on the underlying implementation.
     *
     * @return a collection of the renderlets that are available to this system
     */
    Collection<Renderlet> getAvailableRenderlets();

    /**
     * Fetches all data that fulfils the given filters and renders it using any available renderlets.  The
     * RenderletNexus for this session is used to ensure available renderlets know how to arrange their output.
     *
     * @param renderletNexus a renderletNexus that controls interactions between the configured renderlets for this
     *                       pussycat session
     * @return a well formatted SVG string that should be returned to the client
     * @throws uk.ac.ebi.spot.goci.pussycat.exception.PussycatSessionNotReadyException if this Pussycat session is not
     *                                                                                 yet initialized and therefore
     *                                                                                 unable to perform any rendering
     */
    String performRendering(RenderletNexus renderletNexus, Filter... filters)
            throws PussycatSessionNotReadyException, NoRenderableDataException;

    /**
     * Fetches a list of lightweight association summary objects that encapsulates an overview of the salient
     * information for all associations with the supplied IDs in the request.  The returned list will implicitly have
     * the same ordering as that of the supplied argument, and if any of the association URIs requested in the argument
     * were missing, the list will contain null elements.
     *
     * @param associationURIs the URIs of the associations to retrieve
     * @return a list of association summaries with the same ordering as the request
     */
    //    List<AssociationSummary> getAssociationSummaries(List<URI> associationURIs);

    /**
     * Returns a list of URI identifiers for traits that are related to the supplied trait name.  This will basically
     * expand the set of traits based on rules defined by the implementation to return a set of identifiers.  So, for
     * example, if a user supplies the trait name "diabetes mellitus" you would expect to recieve back a list of URIs
     * containing all concepts "related to" diabetes meelitus.  For example, a minimal implementation would be to return
     * the URI representing diabetes mellitus and URIs for all of it's subclasses.
     *
     * @param traitName the name of the the trait to filter on
     * @return a set of URIs for all associated traits
     */
    Set<URI> getRelatedTraits(String traitName);
}
