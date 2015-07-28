package uk.ac.ebi.spot.goci.sparql.pussycat.renderlet.chromosome;

import uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.spot.goci.pussycat.renderlet.chromosome.ChromosomeRenderlet;
import uk.ac.ebi.spot.goci.sparql.pussycat.query.SparqlTemplate;

import java.net.URI;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
public abstract class SparqlChromosomeRenderlet extends ChromosomeRenderlet<SparqlTemplate, URI> {
    @Override public boolean canRender(RenderletNexus nexus, Object renderingContext, Object renderingEntity) {
        return renderingContext instanceof SparqlTemplate &&
                renderingEntity instanceof URI &&
                getChromosomeURI().equals(renderingEntity);
//        if (renderingContext instanceof SparqlTemplate && renderingEntity instanceof URI) {
//            SparqlTemplate template = (SparqlTemplate) renderingContext;
//            URI uri = (URI) renderingEntity;
//            return template.ask(uri, URI.create(OntologyConstants.CHROMOSOME_CLASS_IRI));
//        }
//        else {
//            return false;
//        }
    }
}
