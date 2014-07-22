package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGBuilder;
import uk.ac.ebi.fgpt.goci.pussycat.service.QueryManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by dwelter on 27/05/14.
 */
public class SPARQLRenderletNexus implements RenderletNexus<String>{


    private Logger log = LoggerFactory.getLogger(getClass());
    private Logger diagramLogger = LoggerFactory.getLogger("diagram.log");

    protected Logger getLog() {
        return log;
    }

    private Set<Renderlet> renderlets;
    private Map<Object, SVGArea> entityLocations;
    private Map<Object, RenderingEvent> renderedEntities;
    private Map<IRI, String> efoLabels;
    private Map<String, BandInformation> bandLocations;
    private Set<OWLNamedIndividual> allTraits;
    private QueryManager queryManager;


    public SPARQLRenderletNexus(){
        this.renderlets = new HashSet<Renderlet>();
        this.entityLocations = new HashMap<Object, SVGArea>();
        this.renderedEntities = new HashMap<Object, RenderingEvent>();
        this.bandLocations = new HashMap<String, BandInformation>();
    }

    public Map<String, BandInformation> getBandLocations(){
        return bandLocations;
    }

    public void setBandLocation(String band, BandInformation info){
        bandLocations.put(band, info);
    }


    public void setQueryManager(QueryManager queryManager){
        this.queryManager = queryManager;
    }

    public QueryManager getQueryManager(){
        return queryManager;
    }


//    public JenaQueryExecutionService getQueryService() {
//        return queryService;
//    }
//
//    public void setQueryService(JenaQueryExecutionService queryService) {
//        this.queryService = queryService;
//    }
//


    @Override
    public boolean register(Renderlet renderlet) {
        getLog().debug("Registering renderlet '" + renderlet.getName() + "' " +
                "(" + renderlet.getDescription() + ") " +
                "[" + renderlet.getClass().getSimpleName() + "]");
        getLog().debug("Renderlets now: " + (renderlets.size() + 1));

        return renderlets.add(renderlet);    }

    @Override
    public void setEfoLabels(Map<IRI, String> efoLabels) {
        this.efoLabels = efoLabels;

    }

    @Override
    public Map<IRI, String> getEfoLabels() {
        return efoLabels;
    }

    @Override
    public <O> void renderingEventOccurred(RenderingEvent<O> evt) {
        entityLocations.put(evt.getRenderedEntity(), evt.getSVGArea());
        renderedEntities.put(evt.getRenderedEntity(), evt);

    }

    @Override
    public <O> SVGArea getLocationOfEntity(O renderedEntity) {
        return entityLocations.get(renderedEntity);
    }


    @Override
    public Set<Renderlet> getRenderlets() {
        return renderlets;
    }

    @Override
    public <O> RenderingEvent getRenderingEvent(O renderedEntity) {
        return renderedEntities.get(renderedEntity);
    }




    @Override
    public String getSVG(String renderableSelection) {
        diagramLogger.info("Rendering diagram for selection " + renderableSelection);
////check if the chromosomes have already been rendered, otherwise render them
        SVGBuilder svgBuilder = new SVGBuilder();
//
//
//
//
///****        SPARQL CONVERSION ****/
//        OWLClass ta = manager.getOWLDataFactory().getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
//        diagramLogger.info("There are " + ta.getIndividuals(ontology).size() + " potential trait associations that could be rendered");
//
            getLog().trace("There are " + renderlets.size() + " registered renderlets");
//
//        long start, end;
//        start = System.currentTimeMillis();
//        getLog().debug("Obtaining OWL individuals from reasoner");
//
///****        SPARQL CONVERSION ****/
//        Set<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression, false).getFlattened();
//        end = System.currentTimeMillis();
//        double time = ((double) (end - start)) / 1000;
//        getLog().info("OWL individuals for OWLClassExpression " + classExpression + " acquired in " + time + " s.");
//
//        getLog().debug("There are " + individuals.size() + " owl individuals that satisfy the expression " +
//                classExpression);
//
//        if(renderedEntities.size() == 0){
////if the map is empty but OWLClassExpression isn't OWLThing, do a dummy rendering to rebuild the map
//            if(!classExpression.isOWLThing()){
//                getLog().debug("Empty maps and a subset to rendered - rebuild maps via dummy OWLThing rendering");
///****        SPARQL CONVERSION ****/
//                OWLClassExpression everything = manager.getOWLDataFactory().getOWLThing();
//                Set<OWLNamedIndividual> allIndividuals = reasoner.getInstances(everything,false).getFlattened();
//                renderSVGFromScratch(svgBuilder, ontology, allIndividuals);
//                getLog().trace("Rendering OWLClassExpression");
//                svgBuilder = new SVGBuilder();
//                renderFromExistingSVG(svgBuilder, ontology, individuals);
//            }
//            else {
//                renderSVGFromScratch(svgBuilder, ontology, individuals);
//                getLog().debug("Rendering complete");
//            }
//        }
//        else{
//            renderFromExistingSVG(svgBuilder, ontology, individuals);
//            getLog().debug("Rendering complete");
//        }

        return svgBuilder.getSVG();


    }

    public void renderChromosomes(SVGBuilder builder){

    }


    public void renderAssociations(SVGBuilder builder){

    }






}
