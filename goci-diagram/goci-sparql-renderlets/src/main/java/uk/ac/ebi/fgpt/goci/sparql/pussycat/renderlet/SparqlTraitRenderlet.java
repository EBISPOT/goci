package uk.ac.ebi.fgpt.goci.sparql.pussycat.renderlet;

import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.TraitRenderlet;
import uk.ac.ebi.fgpt.goci.sparql.pussycat.query.SparqlTemplate;

import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
public class SparqlTraitRenderlet extends TraitRenderlet<SparqlTemplate, URI> {
    @Override public boolean canRender(RenderletNexus nexus, Object renderingContext, Object renderingEntity) {
        if (renderingContext instanceof SparqlTemplate && renderingEntity instanceof URI) {
            SparqlTemplate template = (SparqlTemplate) renderingContext;
            URI uri = (URI) renderingEntity;
            return template.ask(uri, URI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI));
        }
        else {
            return false;
        }
    }

    @Override protected Set<URI> getAssociationsForTrait(SparqlTemplate reasoner, URI trait)
            throws DataIntegrityViolationException {
        return null;
    }

    @Override protected URI getBandForAssociation(SparqlTemplate reasoner, URI association)
            throws DataIntegrityViolationException {
        return null;
    }

    @Override protected List<SVGArea> getLocationsOfOtherTraitsinBand(RenderletNexus nexus,
                                                                      SparqlTemplate reasoner,
                                                                      URI band) throws DataIntegrityViolationException {
        return null;
    }

    @Override protected String getTraitAttribute(SparqlTemplate reasoner, URI trait)
            throws DataIntegrityViolationException {
        return null;
    }

    @Override protected String getTraitAssociationAttribute(SparqlTemplate reasoner, URI association)
            throws DataIntegrityViolationException {
        return null;
    }

    @Override protected String getTraitLabel(SparqlTemplate reasoner, URI individual) {
        return null;
    }

    @Override protected String getTraitColour(SparqlTemplate reasoner, URI trait) {
        return null;
    }
}
