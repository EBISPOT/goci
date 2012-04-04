package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGBuilder;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome.ChromosomeRenderlet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A factory that is capable of producing {@link RenderletNexus} instances.
 *
 * @author Tony Burdett
 * @date 01/03/12
 */
public class RenderletNexusFactory {
    public static RenderletNexus createDefaultRenderletNexus() {
        return new DefaultRenderletNexus();
    }

    private static final class DefaultRenderletNexus implements RenderletNexus {

        private Logger log = LoggerFactory.getLogger(getClass());
        protected Logger getLog() {
            return log;
        }

        private Set<Renderlet> renderlets;
        private Map<Object, SVGArea> renderedEntityLocations;
        private int canvasWidth, canvasHeight;
        private Document doc;
        private SVGBuilder svgBuilder;

        private DefaultRenderletNexus() {
            this.renderlets = new HashSet<Renderlet>();
            this.renderedEntityLocations = new HashMap<Object, SVGArea>();
            this.canvasHeight = 700;
            this.canvasWidth = 1600;
            this.svgBuilder = new SVGBuilder(canvasWidth, canvasHeight);
        }

        public boolean register(Renderlet renderlet) {
            return renderlets.add(renderlet);
        }

        public <O> void renderingEventOccurred(RenderingEvent<O> evt) {
            renderedEntityLocations.put(evt.getRenderedEntity(), evt.getSvgArea());
        }

        public <O> SVGArea getLocationOfRenderedEntity(O renderedEntity) {
            return renderedEntityLocations.get(renderedEntity);
        }

        public int getCanvasWidth(){
            return canvasWidth;
        }

        public int getCanvasHeight(){
            return canvasHeight;
        }

        @Override
        public String getSVG(OWLClassExpression classExpression, OWLReasoner reasoner) {

            renderChromosomes(reasoner);

            // get the ontology loaded into the reasoner
            OWLOntology ontology = reasoner.getRootOntology();

            Set<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression, false).getFlattened();
            getLog().debug("There are " + individuals.size() + " owl individuals that satisfy the expression " +
                    classExpression);


            for (OWLNamedIndividual individual : individuals) {
                // render each individual with a renderlet that can render it
                for (Renderlet r : renderlets) {
                    if (r.canRender(this, ontology, individual)) {
                        getLog().debug("Dispatching render() request to renderlet '" + r.getName() + "'");
                        svgBuilder.addElement(r.render(this, ontology, individual));

                    }
                }
            }

            return svgBuilder.getSVG();

            /*
            * first, check if chromosomes have been rendered.
            * If not, render chromosomes
            * then render classExpression
            * */
        }

        public void renderChromosomes(OWLReasoner reasoner){
            OWLOntology ontology = reasoner.getRootOntology();

            for (Renderlet r : renderlets) {
                if(r instanceof ChromosomeRenderlet){
                    OWLClass chromosome = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI));
                    NodeSet<OWLClass> all = reasoner.getSubClasses(chromosome, true);
                    Set<OWLClass> allChroms = all.getFlattened();

                    for(OWLClass chrom : allChroms){
                        if (r.canRender(this, ontology, chrom)) {
                            getLog().debug("Dispatching render() request to renderlet '" + r.getName() + "'");
                            svgBuilder.addElement(r.render(this, ontology, chrom));
                        }
                    }
                }
            }
        }

    }
}
