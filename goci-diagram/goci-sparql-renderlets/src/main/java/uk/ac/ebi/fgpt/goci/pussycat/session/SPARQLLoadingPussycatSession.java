package uk.ac.ebi.fgpt.goci.pussycat.session;

import uk.ac.ebi.fgpt.goci.lang.Filter;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.Collection;

/**
 * Created by dwelter on 22/05/14.
 */
public class SPARQLLoadingPussycatSession extends AbstractPussycatSession {
    @Override public String performRendering(RenderletNexus renderletNexus, Filter... filters)
            throws PussycatSessionNotReadyException {
        return null;
    }
}
