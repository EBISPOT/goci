package uk.ac.ebi.fgpt.goci.pussycat.manager;

import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;

import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * Manages the relationship between a user-based http session and a {@link uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession}.
 * Using this manager, it is possible to share a single PussycatSession across many user sessions.
 *
 * @author Tony Burdett
 * Date 01/03/12
 */
public interface PussycatSessionManager {
    /**
     * Returns an indication as to whether the passed HttpSession is currently bound to a server-side {@link
     * PussycatSession}.
     *
     * @param session the user's HttpSession
     * @return true if this session is bound to a PussycatSession, false otherwise
     */
    boolean hasAvailableSession(HttpSession session);

    /**
     * Returns the appropriate PussycatSession for the given HttpSession
     *
     * @param session the http session currently held by the request
     * @return a pussycat session containing the required data
     */
    PussycatSession getPussycatSession(HttpSession session);

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
     * Adds a pussycat session to this manager, making it available for use by subsequent HTTP sessions.  Sessions that
     * wish to utilise this pussycat session must call {@link #joinPussycatSession(javax.servlet.http.HttpSession,
     * PussycatSession)} to associate themselves with it
     *
     * @param pussycatSession the pussycat session to add
     */
    void addPussycatSession(PussycatSession pussycatSession);

    /**
     * Binds the supplied {@link HttpSession} to the supplied {@link PussycatSession}.
     *
     * @param session         the HttpSession to bind
     * @param pussycatSession the pussycat session to join
     * @return the active PussycatSession that has just been joined
     * @throws IllegalArgumentException if there is no pussycat session with the supplied ID
     */
    PussycatSession joinPussycatSession(HttpSession session, PussycatSession pussycatSession);

    /**
     * Creates a new {@link PussycatSession}, and binds the supplied {@link HttpSession} to it.
     *
     * @return the newly created pussycat session
     */
    PussycatSession createPussycatSession();
}
