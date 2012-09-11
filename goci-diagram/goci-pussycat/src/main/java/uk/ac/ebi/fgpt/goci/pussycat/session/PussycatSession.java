package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.Collection;

/**
 * A Pussycat session maintains the loaded data required by Pussycat in order to render an informative display to the
 * user(s).  One Pussycat session may be shared across multiple user http sessions.
 *
 * @author Tony Burdett
 * Date 27/02/12
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
     *
     * @param classExpression an OWL class expression describing the data we wish to render
     * @param renderletNexus a renderletNexus that controls interactions between the configured renderlets for this pussycat session
     * @return a well formatted SVG string that should be returned to the client
     * @throws uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException
     *          if this Pussycat session is not yet initialized and therefore unable to perform any rendering
     */
    String performRendering(OWLClassExpression classExpression, RenderletNexus renderletNexus) throws PussycatSessionNotReadyException;

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
}
