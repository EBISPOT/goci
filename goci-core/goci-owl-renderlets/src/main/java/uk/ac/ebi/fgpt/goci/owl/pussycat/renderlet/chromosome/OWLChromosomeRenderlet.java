package uk.ac.ebi.fgpt.goci.owl.pussycat.renderlet.chromosome;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome.ChromosomeRenderlet;

/**
 * Abstract renderlet capable of rendering OWL classes representing Chromosomes into SVG
 *
 * @author Dani Welter
 * @date 29/02/12
 */
public abstract class OWLChromosomeRenderlet extends ChromosomeRenderlet<OWLReasoner, OWLClass> {
    public boolean canRender(RenderletNexus nexus, Object renderingContext, Object owlEntity) {
        boolean renderable = false;
        if (renderingContext instanceof OWLReasoner) {
            if (owlEntity instanceof OWLClass) {
                OWLClass thisClass = (OWLClass) owlEntity;
                if (thisClass.getIRI().toURI().equals(getChromosomeURI())) {
                    renderable = true;
                }
            }
        }
        return renderable;
    }
}

