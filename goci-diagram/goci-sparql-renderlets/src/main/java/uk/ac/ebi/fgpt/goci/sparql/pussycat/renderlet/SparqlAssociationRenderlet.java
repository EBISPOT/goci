package uk.ac.ebi.fgpt.goci.sparql.pussycat.renderlet;

import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.AssociationRenderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.sparql.pussycat.query.SparqlTemplate;

import java.net.URI;
import java.util.Map;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
public class SparqlAssociationRenderlet extends AssociationRenderlet<SparqlTemplate, URI> {
    @Override public boolean canRender(RenderletNexus nexus, Object renderingContext, Object renderingEntity) {
        if (renderingContext instanceof SparqlTemplate && renderingEntity instanceof URI) {
            SparqlTemplate template = (SparqlTemplate) renderingContext;
            URI uri = (URI) renderingEntity;
            return template.ask(uri, URI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
        }
        else {
            return false;
        }
    }

    @Override protected String getAssociationID(SparqlTemplate context, URI association) {
        return association.toString();
    }

    @Override protected BandInformation getBandInformation(SparqlTemplate context, URI association)
            throws DataIntegrityViolationException {
        return null;
    }

    @Override protected int getNumberOfTraitsInSameBand(SparqlTemplate context, URI association)
            throws DataIntegrityViolationException {
        return 0;
    }

    @Override protected int getNumberOfTraitsInPreviousBand(SparqlTemplate context, URI association)
            throws DataIntegrityViolationException {
        return 0;
    }

    @Override protected SVGArea getLocationOfPreviousAssociation(RenderletNexus nexus,
                                                                 SparqlTemplate context,
                                                                 URI association)
            throws DataIntegrityViolationException {
        return null;
    }

    @Override protected Map<BandInformation, BandInformation> sortBandsWithData(SparqlTemplate context) {
        return null;
    }
}
