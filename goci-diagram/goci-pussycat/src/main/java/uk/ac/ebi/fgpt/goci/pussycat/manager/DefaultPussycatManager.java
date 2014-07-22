package uk.ac.ebi.fgpt.goci.pussycat.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexusFactory;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSessionFactory;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A default implementation of a {@link uk.ac.ebi.fgpt.goci.pussycat.manager.PussycatManager} that uses a prewired
 * {@link uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexusFactory} to construct any new, required instances of a
 * renderlet nexus.
 *
 * @author Tony Burdett
 * @date 04/06/14
 */
public class DefaultPussycatManager implements PussycatManager {
    private PussycatSessionFactory sessionFactory;
    private RenderletNexusFactory nexusFactory;

    private Set<PussycatSession> sessions;
    private Map<HttpSession, PussycatSession> sessionMap;

    private Map<HttpSession, RenderletNexus> nexusMap = new HashMap<HttpSession, RenderletNexus>();

    private Logger log = LoggerFactory.getLogger(getClass());

    public DefaultPussycatManager() {
        this.sessions = new HashSet<PussycatSession>();
        this.sessionMap = new HashMap<HttpSession, PussycatSession>();
        this.nexusMap = new HashMap<HttpSession, RenderletNexus>();
    }

    protected Logger getLog() {
        return log;
    }

    public PussycatSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(PussycatSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public RenderletNexusFactory getNexusFactory() {
        return nexusFactory;
    }

    public void setNexusFactory(RenderletNexusFactory nexusFactory) {
        this.nexusFactory = nexusFactory;
    }

    public Collection<PussycatSession> getPussycatSessions() {
        return sessions;
    }

    public boolean hasAvailablePussycatSession(HttpSession session) {
        return sessionMap.containsKey(session);
    }

    public PussycatSession getPussycatSession(HttpSession session) {
        return sessionMap.get(session);
    }

    public PussycatSession bindPussycatSession(HttpSession session, PussycatSession pussycatSession) {
        if (!sessions.contains(pussycatSession)) {
            sessions.add(pussycatSession);
        }
        sessionMap.put(session, pussycatSession);
        return pussycatSession;
    }

    @Override public boolean hasAvailableRenderletNexus(HttpSession session) {
        return nexusMap.containsKey(session);
    }

    @Override public RenderletNexus getRenderletNexus(HttpSession session) {
        return nexusMap.get(session);
    }

    @Override public RenderletNexus bindRenderletNexus(HttpSession session, RenderletNexus renderletNexus) {
        nexusMap.put(session, renderletNexus);
        return renderletNexus;
    }

    @Override public boolean unbindResources(HttpSession session) {
        boolean sessionUnbound = false;
        boolean nexusUnbound = false;
        if (sessionMap.containsKey(session)) {
            PussycatSession ps = sessionMap.remove(session);
            getLog().debug("PussycatSession '" + ps.getSessionID() + "' is no longer bound to " +
                                   "HttpSession '" + session.getId() + "'");
            sessionUnbound = true;
        }
        else {
            getLog().debug("Cannot unbind PussycatSession for HttpSession '" + session.getId() + "' " +
                                   "- no linked PussycatSession resource");
        }
        if (nexusMap.containsKey(session)) {
            RenderletNexus rn = nexusMap.remove(session);
            getLog().debug("RenderletNexus '" + rn + "' is no longer bound to " +
                                   "HttpSession '" + session.getId() + "'");
            nexusUnbound = true;
        }
        else {
            getLog().debug("Cannot unbind RenderletNexus for HttpSession '" + session.getId() + "' " +
                                   "- no linked RenderletNexus resource");
        }
        return sessionUnbound && nexusUnbound;
    }

    public PussycatSession createPussycatSession() {
        return getSessionFactory().createPussycatSession();
    }

    @Override public RenderletNexus createRenderletNexus(PussycatSession session)
            throws PussycatSessionNotReadyException {
        return getNexusFactory().createRenderletNexus(session);
    }
}
