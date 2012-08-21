package uk.ac.ebi.fgpt.goci.pussycat.manager;

import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;

import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * Manages the relationships between a user-based HttpSession and resources required by Pussycat,
 * including a {@link PussycatSession} and a {@link RenderletNexus}.
 * <p/>
 * Using this manager, it is possible to share a single PussycatSession across many user sessions,
 * but each RenderletNexus must be unique to the HttpSession.
 *
 * @author Tony Burdett
 * @date 01/03/12
 */
public interface PussycatManager {
    /**
     * Sets a collection of pussycat sessions that are immediately available for subsequent HttpSessions to join and
     * use
     *
     * @param pussycatSessions the pussycat sessions to make available
     */
    void setPussycatSessions(Collection<PussycatSession> pussycatSessions);

    /**
     * Returns the collection of currently active sessions
     *
     * @return all currently active {@link PussycatSession}s
     */
    Collection<PussycatSession> getPussycatSessions();

    /**
     * Creates a new {@link PussycatSession}.
     *
     * @return the newly created pussycat session
     */
    PussycatSession createPussycatSession();

    /**
     * Creates a new {@link RenderletNexus} that can utilise the supplied {@link PussycatSession}.
     *
     * @return the newly created renderlet nexus
     */
    RenderletNexus createRenderletNexus(OntologyConfiguration configuration, PussycatSession pussycatSession) throws
            PussycatSessionNotReadyException;

    /**
     * Returns an indication as to whether the passed HttpSession is currently bound to a server-side {@link
     * PussycatSession}.
     *
     * @param session the user's HttpSession
     * @return true if this session is bound to a PussycatSession, false otherwise
     */
    boolean hasAvailablePussycatSession(HttpSession session);

    /**
     * Returns the appropriate PussycatSession for the given HttpSession
     *
     * @param session the http session currently held by the request
     * @return a pussycat session containing the required data
     */
    PussycatSession getPussycatSession(HttpSession session);

    /**
     * Binds the supplied {@link HttpSession} to the supplied {@link PussycatSession}.
     *
     * @param session         the HttpSession to bind
     * @param pussycatSession the pussycat session to join
     * @return the active PussycatSession that has just been joined
     * @throws IllegalArgumentException if there is no pussycat session with the supplied ID
     */
    PussycatSession bindPussycatSession(HttpSession session, PussycatSession pussycatSession);

    /**
     * Returns an indication as to whether the passed HttpSession has a currently assigned {@link
     * RenderletNexus}.
     *
     * @param session the user's HttpSession
     * @return true if this session has previously been assigned a renderlet nexus, false otherwise
     */
    boolean hasAvailableRenderletNexus(HttpSession session);

    /**
     * Returns the renderlet nexus currently assigned to the given HttpSession, if present
     *
     * @param session the http session currently held by the request
     * @return a renderlet nexus assigned to the supplied HttpSession, if present, or null otherwise
     */
    RenderletNexus getRenderletNexus(HttpSession session);

    /**
     * Binds the supplied {@link HttpSession} to the supplied {@link RenderletNexus}.
     *
     * @param session        the HttpSession to bind
     * @param RenderletNexus the pussycat session to join
     * @return the active RenderletNexus that has just been joined
     * @throws IllegalArgumentException if there is no pussycat session with the supplied ID
     */
    RenderletNexus bindRenderletNexus(HttpSession session, RenderletNexus RenderletNexus);

    /**
     * Releases any resources associaed with the supplied {@link HttpSession}.  This will have the effect of
     * unbinding any {@link PussycatSession}s and the {@link RenderletNexus} bound to this HttpSession,
     * if present.  Any other resources held by this HttpSession should also be released when this method is called.
     *
     * @param session the HttpSession to which Pussycat resources are bound
     */
    boolean unbindResources(HttpSession session);
}
