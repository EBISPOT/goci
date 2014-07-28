package uk.ac.ebi.fgpt.goci.pussycat.session;

import uk.ac.ebi.fgpt.goci.lang.Filter;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.Collection;

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
     * @throws uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException if this Pussycat session is not
     *                                                                                 yet initialized and therefore
     *                                                                                 unable to perform any rendering
     */
    String performRendering(RenderletNexus renderletNexus, Filter... filters) throws PussycatSessionNotReadyException;
}
