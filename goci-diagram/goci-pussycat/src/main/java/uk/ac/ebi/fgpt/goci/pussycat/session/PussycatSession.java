package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.Collection;
import java.util.Set;

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
     * Fetches all data that fulfils the given class expression and renders it using any available renderlets.  The
     * RenderletNexus for this session is used to ensure available renderlets know how to arrange their output.
     *
     * @param classExpression an OWL class expression describing the data we wish to render
     * @return a well formatted SVG string that should be returned to the client
     * @throws uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException
     *          if this Pussycat session is not yet initialized and therefore unable to perform any rendering
     */
    String performRendering(OWLClassExpression classExpression) throws PussycatSessionNotReadyException;

    /**
     * Clears any data that is currently rendered in this session
     *
     * @return true if this operation suceeded, otherwise false
     */
    boolean clearRendering();

    /**
     * Returns an OWL reasoner that has been initialized with a classified version of the data this pussycat session
     * works with
     *
     * @return a pre-initialized reasoner
     * @throws OWLConversionException if something went wrong during classification of the ontology
     * @throws uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException
     *                                if the reasoner for this session is not yet fully initialized
     */
    OWLReasoner getReasoner() throws OWLConversionException, PussycatSessionNotReadyException;

    /**
     * Returns the set of OWL individuals that satisfy the given class expression and should therefore be rendered.
     *
     * @param classExpression an class expression to lookup individuals that satisfy it
     * @return a set of individuals satisfying the supplied class expression
     * @throws OWLConversionException is something went wrong querying the underlying reasoner
     * @throws uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException
     *                                if the reasoner for this session is not yet fully initialized
     */
    Set<OWLNamedIndividual> query(OWLClassExpression classExpression)
            throws OWLConversionException, PussycatSessionNotReadyException;
}
