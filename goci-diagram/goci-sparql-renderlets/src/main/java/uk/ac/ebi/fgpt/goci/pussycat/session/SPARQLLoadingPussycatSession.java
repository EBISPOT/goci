package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.Collection;

/**
 * Created by dwelter on 22/05/14.
 */
public class SPARQLLoadingPussycatSession extends AbstractPussycatSession {
    @Override
    public Collection<Renderlet> getAvailableRenderlets() {
        return null;
    }

    @Override
    public String performRendering(OWLClassExpression classExpression, RenderletNexus renderletNexus) throws PussycatSessionNotReadyException {
        return null;
    }

    @Override
    public boolean clearRendering() {
        return false;
    }

    @Override
    public OWLReasoner getReasoner() throws OWLConversionException, PussycatSessionNotReadyException {
        return null;
    }


}
