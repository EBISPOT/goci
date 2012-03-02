package uk.ac.ebi.fgpt.goci.pussycat.manager;

import uk.ac.ebi.fgpt.goci.pussycat.session.GOCIDataPublisherPussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.service.GWASOWLPublisher;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * A basic implementation of a {@link PussycatSessionManager} that stores mappngs between http sessions and pussycat
 * sessions in an internal memory-based map.
 *
 * @author Tony Burdett
 * @date 01/03/12
 */
public class DefaultPussycatSessionManager implements PussycatSessionManager {
    private Set<PussycatSession> pussycatSessions;
    private Map<HttpSession, PussycatSession> sessionMap;

    private GWASOWLPublisher publisher;

    public DefaultPussycatSessionManager() {
        this.pussycatSessions = new HashSet<PussycatSession>();
        this.sessionMap = new HashMap<HttpSession, PussycatSession>();
    }

    public GWASOWLPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(GWASOWLPublisher publisher) {
        this.publisher = publisher;
    }

    public boolean hasAvailableSession(HttpSession session) {
        return sessionMap.containsKey(session);
    }

    public PussycatSession getPussycatSession(HttpSession session) {
        return sessionMap.get(session);
    }

    public void setPussycatSessions(Collection<PussycatSession> pussycatSessions) {
        this.pussycatSessions.addAll(pussycatSessions);
    }

    public Collection<PussycatSession> getPussycatSessions() {
        return pussycatSessions;
    }

    public void addPussycatSession(PussycatSession pussycatSession) {
        pussycatSessions.add(pussycatSession);
    }


    public PussycatSession joinPussycatSession(HttpSession session, PussycatSession pussycatSession) {
        if (!pussycatSessions.contains(pussycatSession)) {
            pussycatSessions.add(pussycatSession);
        }
        return sessionMap.put(session, pussycatSession);
    }

    public PussycatSession createPussycatSession() {
        GOCIDataPublisherPussycatSession pussycatSession = new GOCIDataPublisherPussycatSession();
        pussycatSession.setPublisher(publisher);
        pussycatSessions.add(pussycatSession);
        return pussycatSession;
    }
}
