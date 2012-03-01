package uk.ac.ebi.fgpt.goci.pussycat.manager;

import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;

import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * Manages the relationship between a user-based http session and a {@link uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession}.
 * Using this manager, it is possible to share a single PussycatSession across many user sessions.
 *
 * @author Tony Burdett
 * @date 01/03/12
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
     * Returns the collection of currently active sessions
     *
     * @return all currently active {@link PussycatSession}s
     */
    Collection<PussycatSession> getPussycatSessions();

    /**
     * Binds the supplied {@link HttpSession} to the {@link PussycatSession} with the supplied ID.  An
     * IllegalArgumentException is thrown if there is no active session with the supplied ID.
     *
     * @param session           the HttpSession to bind
     * @param pussycatSessionID the ID of the pussycat session to join
     * @return the active PussycatSession that has just been joined
     * @throws IllegalArgumentException if there is no pussycat session with the supplied ID
     */
    PussycatSession joinPussycatSession(HttpSession session, String pussycatSessionID) throws IllegalArgumentException;

    /**
     * Creates a new {@link PussycatSession}, and binds the supplied {@link HttpSession} to it.
     *
     * @param session the HttpSession to create a new PussycatSession for
     * @return the newly created pussycat session
     */
    PussycatSession createPussycatSession(HttpSession session);
}
