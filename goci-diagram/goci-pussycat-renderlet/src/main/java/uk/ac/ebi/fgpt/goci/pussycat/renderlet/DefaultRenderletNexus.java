package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGBuilder;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome.ChromosomeRenderlet;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 30/04/12
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class DefaultRenderletNexus implements RenderletNexus {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    private Set<Renderlet> renderlets;
    private Map<Object, SVGArea> renderedEntityLocations;
    private Map<String, ArrayList<Object>> renderedAssociations;
    private Map<Object, RenderingEvent> renderedEntities;
    private SVGBuilder svgBuilder;
    private Map<IRI, String> efoLabels;

    private OWLOntologyManager manager;
    private OWLReasoner reasoner;

    public DefaultRenderletNexus() {
        this.renderlets = new HashSet<Renderlet>();
        this.renderedEntityLocations = new HashMap<Object, SVGArea>();
        this.renderedEntities = new HashMap<Object, RenderingEvent>();
        this.renderedAssociations = new HashMap<String, ArrayList<Object>>();
        this.svgBuilder = new SVGBuilder();
    }


    @Override
    public void setOWLOntologyManager(OWLOntologyManager manager) {
        this.manager = manager;
    }

    @Override
    public OWLOntologyManager getManager() {
        return manager;
    }

    @Override
    public void setReasoner(OWLReasoner reasoner) {
        this.reasoner = reasoner;
    }

    @Override
    public OWLReasoner getReasoner() {
        return reasoner;
    }

    @Override
    public void setEfoLabels(Map<IRI, String> efoLabels) {
        this.efoLabels = efoLabels;
    }

    @Override
    public Map<IRI, String> getEfoLabels() {
        return efoLabels;
    }


    public boolean register(Renderlet renderlet) {
        getLog().debug("Registering renderlet '" + renderlet.getName() + "' " +
                "(" + renderlet.getDescription() + ") " +
                "[" + renderlet.getClass().getSimpleName() + "]");
        getLog().debug("Renderlets now: " + (renderlets.size() + 1));
        return renderlets.add(renderlet);
    }

    public Set<Renderlet> getRenderlets() {
        return renderlets;
    }

    public <O> void renderingEventOccurred(RenderingEvent<O> evt) {
        renderedEntityLocations.put(evt.getRenderedEntity(), evt.getSvgArea());
        renderedEntities.put(evt.getRenderedEntity(), evt);
    }

    public <O> SVGArea getLocationOfRenderedEntity(O renderedEntity) {
        return renderedEntityLocations.get(renderedEntity);
    }

    public <O> RenderingEvent getRenderingEvent(O renderedEntity) {
        return renderedEntities.get(renderedEntity);
    }

    public <O> void setAssociation(String band, O renderedEntity) {
        if (renderedAssociations.containsKey(band)) {
            renderedAssociations.get(band).add(renderedEntity);
        }
        else {
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(renderedEntity);
            renderedAssociations.put(band, list);
        }
    }

    public ArrayList<Object> getAssociations(String band) {
        if (renderedAssociations.containsKey(band)) {
            return renderedAssociations.get(band);
        }
        else {
            return null;
        }
    }

    public void addSVGElement(Element element) {
        svgBuilder.addElement(element);
    }

    public Element createSVGElement(String type) {
        return svgBuilder.createElement(type);
    }

    @Override
    public String getSVG(OWLClassExpression classExpression) {
//check if the chromosomes have already been rendered, otherwise render them
        getLog().debug("There are " + renderlets.size() + " registered renderlets");
        boolean check = false;

        Set<Object> keys = renderedEntities.keySet();

        for (Object key : keys) {

            if (renderedEntities.get(key).getRenderingRenderlet() instanceof ChromosomeRenderlet) {
                check = true;
                break;
            }
        }

        if (!check) {
            renderChromosomes();
        }

        // get the ontology loaded into the reasoner
        OWLOntology ontology = reasoner.getRootOntology();

        Set<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression, false).getFlattened();
        getLog().debug("There are " + individuals.size() + " owl individuals that satisfy the expression " +
                classExpression);

        for (OWLNamedIndividual individual : individuals) {

            boolean isAssociation =
                    checkType(individual, ontology, IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));

            if (isAssociation) {
                // render each individual with a renderlet that can render it
                for (Renderlet r : renderlets) {
                    if (r.canRender(this, ontology, individual)) {
                        getLog().debug("Dispatching render() request to renderlet '" + r.getName() + "'");
                        r.render(this, ontology, individual);
                    }
                }
            }
        }

        return svgBuilder.getSVG();

    }

    public void renderChromosomes() {
        OWLOntology ontology = reasoner.getRootOntology();

        for (Renderlet r : renderlets) {
            if (r instanceof ChromosomeRenderlet) {
                OWLClass chromosome = ontology.getOWLOntologyManager()
                        .getOWLDataFactory()
                        .getOWLClass(IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI));
                NodeSet<OWLClass> all = reasoner.getSubClasses(chromosome, true);
                Set<OWLClass> allChroms = all.getFlattened();

                for (OWLClass chrom : allChroms) {
                    if (r.canRender(this, ontology, chrom)) {
                        getLog().debug("Dispatching render() request to renderlet '" + r.getName() + "'");
                        r.render(this, ontology, chrom);
                    }
                }
            }
        }
    }

    public boolean checkType(OWLNamedIndividual individual, OWLOntology ontology, IRI typeIRI) {
        boolean type = false;

        OWLClassExpression[] allTypes = individual.getTypes(ontology).toArray(new OWLClassExpression[0]);

        for (int i = 0; i < allTypes.length; i++) {
            OWLClass typeClass = allTypes[i].asOWLClass();

            if (typeClass.getIRI().equals(typeIRI)) {
                type = true;
                break;
            }
        }

        return type;
    }
}

