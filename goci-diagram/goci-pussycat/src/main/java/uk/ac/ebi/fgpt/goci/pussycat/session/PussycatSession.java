package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.model.OWLOntology;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.Collection;

/**
 * A Pussycat session maintains the loaded data required by Pussycat in order to render an informative display to the
 * user(s).  One Pussycat session may be shared across multiple user http sessions.
 *
 * @author Tony Burdett
 * @date 27/02/12
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
     * Returns the data this session has currently loaded, as an OWL Ontology.  How this OWL ontology is populated
     * depends on underlying implementations: implementations may choose to query for data from a remote service, or may
     * load this ontology directly.
     *
     * @return the data that is loaded into this session
     */
    OWLOntology getLoadedData();

    /**
     * Returns the {@link uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus} that controls interactions between the
     * configured renderlets for this pussycat session
     *
     * @return the renderlet nexus configured for this session
     */
    RenderletNexus getRenderletNexus();

    /**
     * Returns a collection of all available renderlets.  Whether available renderlets are instantiated on demand or
     * returned from a cache depends on the underlying implementation.
     *
     * @return a collection of the renderlets that are available to this system
     */
    Collection<Renderlet> getAvailableRenderlets();

    /**
     * An enum that encapsulates strategies for dealing with {@link PussycatSession}s.  {@link #JOIN} indicates that new
     * HttpSessions should join an existing serverside PussycatSession, if one is available.  {@link #CREATE} indicates
     * that a new serverside PussycatSession should always be created.
     */
    public enum Strategy {
        JOIN,
        CREATE
    }
}
