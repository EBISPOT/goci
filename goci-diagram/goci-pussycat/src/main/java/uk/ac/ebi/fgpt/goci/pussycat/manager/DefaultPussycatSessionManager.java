package uk.ac.ebi.fgpt.goci.pussycat.manager;

import uk.ac.ebi.fgpt.goci.pussycat.session.DummyPussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A basic implementation of a {@link PussycatSessionManager} that stores mappngs between http sessions and pussycat
 * sessions in an internal memory-based map.
 *
 * @author Tony Burdett
 * @date 01/03/12
 */
public class DefaultPussycatSessionManager implements PussycatSessionManager {
    Map<HttpSession, PussycatSession> sessionMap;

    public DefaultPussycatSessionManager() {
        this.sessionMap = new HashMap<HttpSession, PussycatSession>();
    }

    public boolean hasAvailableSession(HttpSession session) {
        return sessionMap.containsKey(session);
    }

    public PussycatSession getPussycatSession(HttpSession session) {
        return sessionMap.get(session);
    }

    public Collection<PussycatSession> getPussycatSessions() {
        return sessionMap.values();
    }

    public PussycatSession joinPussycatSession(HttpSession session, String pussycatSessionID)
            throws IllegalArgumentException {
        for (PussycatSession pussycatSession : getPussycatSessions()) {
            if (pussycatSession.getSessionID().equals(pussycatSessionID)) {
                return sessionMap.put(session, pussycatSession);
            }
        }
        // if we got to here, pussycat session with the supplied ID was not found
        throw new IllegalArgumentException("There was no active PussycatSession with id '" + pussycatSessionID + "'");
    }

    public PussycatSession createPussycatSession(HttpSession session) {
        PussycatSession pussycatSession = new DummyPussycatSession();
        sessionMap.put(session, pussycatSession);
        return pussycatSession;
    }
}
