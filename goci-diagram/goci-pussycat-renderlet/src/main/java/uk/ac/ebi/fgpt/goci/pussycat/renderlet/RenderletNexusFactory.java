package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGBuilder;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGCanvas;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome.ChromosomeRenderlet;

import java.util.*;

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
        private List<RenderingEvent> renderedEntities;
        private int canvasWidth, canvasHeight;
        private SVGBuilder svgBuilder;

        private DefaultRenderletNexus() {
            this.renderlets = new HashSet<Renderlet>();
            this.renderedEntityLocations = new HashMap<Object, SVGArea>();
            this.renderedEntities = new ArrayList<RenderingEvent>();
            this.canvasHeight = SVGCanvas.canvasHeight;
            this.canvasWidth = SVGCanvas.canvasWidth;
            this.svgBuilder = new SVGBuilder(canvasWidth, canvasHeight);
        }

        public boolean register(Renderlet renderlet) {
            return renderlets.add(renderlet);
        }

        public <O> void renderingEventOccurred(RenderingEvent<O> evt) {
            renderedEntityLocations.put(evt.getRenderedEntity(), evt.getSvgArea());
            renderedEntities.add(evt);
        }

        public <O> SVGArea getLocationOfRenderedEntity(O renderedEntity) {
            return renderedEntityLocations.get(renderedEntity);
        }


        @Override
        public String getSVG(OWLClassExpression classExpression, OWLReasoner reasoner) {

//check if the chromosomes have already been rendered, otherwise render them
            boolean check = false;
            int i = 0;

            while(!check && (i < renderedEntities.size())){
                Renderlet rendered = renderedEntities.get(i).getRenderingRenderlet();

                if (rendered instanceof ChromosomeRenderlet){
                    check = true;
                }
                else{
                    i++;
                }
            }

            if(! check){
                renderChromosomes(reasoner);
            }

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
            
  /*          for(int q = 0; q < renderedEntities.size(); q++){
                Renderlet chrom = renderedEntities.get(q).getRenderingRenderlet();
                
                if(chrom instanceof ChromosomeRenderlet){
                   Object[] bands = ((ChromosomeRenderlet)chrom).getBands().keySet().toArray();
                    
                    System.out.println("Bands from chromosome: " + chrom.getName());
                    for(int s = 0; s < bands.length; s++){
                        System.out.print(bands[s] + "\t");
                    }
                    System.out.print("\n");
                    
                }

            }         */

            return svgBuilder.getSVG();

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
