package uk.ac.ebi.fgpt.goci.pussycat.manager;

import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;

import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * Created by dwelter on 27/05/14.
 */
public class SPARQLPussycatManager implements PussycatManager {
    @Override
    public void setPussycatSessions(Collection<PussycatSession> pussycatSessions) {

    }

    @Override
    public Collection<PussycatSession> getPussycatSessions() {
        return null;
    }

    @Override
    public PussycatSession createPussycatSession() {
        return null;
    }

    @Override
    public RenderletNexus createRenderletNexus(OntologyConfiguration configuration, PussycatSession pussycatSession) throws PussycatSessionNotReadyException {
        return null;
    }

    @Override
    public boolean hasAvailablePussycatSession(HttpSession session) {
        return false;
    }

    @Override
    public PussycatSession getPussycatSession(HttpSession session) {
        return null;
    }

    @Override
    public PussycatSession bindPussycatSession(HttpSession session, PussycatSession pussycatSession) {
        return null;
    }

    @Override
    public boolean hasAvailableRenderletNexus(HttpSession session) {
        return false;
    }

    @Override
    public RenderletNexus getRenderletNexus(HttpSession session) {
        return null;
    }

    @Override
    public RenderletNexus bindRenderletNexus(HttpSession session, RenderletNexus RenderletNexus) {
        return null;
    }

    @Override
    public boolean unbindResources(HttpSession session) {
        return false;
    }
}
