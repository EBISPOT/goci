package uk.ac.ebi.fgpt.goci.pussycat.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexusFactory;
import uk.ac.ebi.fgpt.goci.pussycat.session.GOCIDataPublisherPussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.ReasonerSession;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * A basic implementation of a {@link PussycatManager} that stores mappngs between http sessions and pussycat
 * sessions in an internal memory-based map.
 *
 * @author Tony Burdett
 *         Date 01/03/12
 */
public class OWLPussycatManager implements PussycatManager {
    private Set<PussycatSession> pussycatSessions;
    private Map<HttpSession, PussycatSession> sessionMap;

    private Map<HttpSession, RenderletNexus> nexusMap = new HashMap<HttpSession, RenderletNexus>();

    private ReasonerSession reasonerSession;

    private Logger log = LoggerFactory.getLogger(getClass());

    public OWLPussycatManager() {
        this.pussycatSessions = new HashSet<PussycatSession>();
        this.sessionMap = new HashMap<HttpSession, PussycatSession>();
        this.nexusMap = new HashMap<HttpSession, RenderletNexus>();
    }

    protected Logger getLog() {
        return log;
    }

    public ReasonerSession getReasonerSession() {
        return reasonerSession;
    }

    public void setReasonerSession(ReasonerSession reasonerSession) {
        this.reasonerSession = reasonerSession;
    }

    public void setPussycatSessions(Collection<PussycatSession> pussycatSessions) {
        this.pussycatSessions.addAll(pussycatSessions);
    }

    public Collection<PussycatSession> getPussycatSessions() {
        return pussycatSessions;
    }

    public boolean hasAvailablePussycatSession(HttpSession session) {
        return sessionMap.containsKey(session);
    }

    public PussycatSession getPussycatSession(HttpSession session) {
        return sessionMap.get(session);
    }

    public PussycatSession bindPussycatSession(HttpSession session, PussycatSession pussycatSession) {
        if (!pussycatSessions.contains(pussycatSession)) {
            pussycatSessions.add(pussycatSession);
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
        // create a new pussycat session
        GOCIDataPublisherPussycatSession pussycatSession = new GOCIDataPublisherPussycatSession();
        pussycatSession.setReasonerSession(getReasonerSession());

        // add the new session to the set of available sessions
        getPussycatSessions().add(pussycatSession);
        return pussycatSession;
    }

    @Override
    public RenderletNexus createRenderletNexus(OntologyConfiguration configuration, PussycatSession pussycatSession)
            throws PussycatSessionNotReadyException {
        try {
            Collection<Renderlet> renderlets = pussycatSession.getAvailableRenderlets();
            RenderletNexus renderletNexus = RenderletNexusFactory.createOWLRenderletNexus(
                    configuration.getOWLOntologyManager(),
                    pussycatSession.getReasoner(),
                    configuration.getEfoLabels());

            for (Renderlet r : renderlets) {
                renderletNexus.register(r);
            }

            getLog().debug("Created new RenderletNexus for PussycatSession '" + pussycatSession + "'");
            return renderletNexus;
        }
        catch (OWLConversionException e) {
            throw new RuntimeException("Unexpected exception occurred obtaining reasoner", e);
        }
    }
}
