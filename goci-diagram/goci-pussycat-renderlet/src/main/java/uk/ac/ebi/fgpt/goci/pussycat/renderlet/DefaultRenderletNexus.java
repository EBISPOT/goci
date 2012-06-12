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
    private HashMap<String, ArrayList<String>> renderedTraits;
    private Map<Object, RenderingEvent> renderedEntities;
    private SVGBuilder svgBuilder;
    private Map<IRI, String> efoLabels;
    private Map<String, BandInformation> traitLocations;
    private Set<OWLNamedIndividual> allTraits;
    private List<String> renderabledBands;

    private OWLOntologyManager manager;
    private OWLReasoner reasoner;

    public DefaultRenderletNexus() {
        this.renderlets = new HashSet<Renderlet>();
        this.renderedEntityLocations = new HashMap<Object, SVGArea>();
        this.renderedEntities = new HashMap<Object, RenderingEvent>();
        this.renderedAssociations = new HashMap<String, ArrayList<Object>>();
        this.svgBuilder = new SVGBuilder();
        this.renderedTraits = new HashMap<String, ArrayList<String>>();
        this.traitLocations = new HashMap<String, BandInformation>();
        this.renderabledBands = new ArrayList<String>();
    }

    public Map<String, BandInformation> getTraitLocations(){
        return traitLocations;
    }

    public void setRenderableBand(String bandName){
        renderabledBands.add(bandName);
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


    @Override
    public void setTrait(String location, String trait) {
        if (renderedTraits.containsKey(location)){
            renderedTraits.get(location).add(trait);
        }
        else{
            ArrayList<String> list = new ArrayList<String>();
            list.add(trait);
            renderedTraits.put(location, list);
        }
    }

    @Override
    public ArrayList<String> getRenderedTraits(String location) {
        if(renderedTraits.containsKey(location)){
            return renderedTraits.get(location);
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

        ArrayList<String> renderingOrder = new ArrayList<String>();
        if(traitLocations.size() == 0){
            renderingOrder =  buildTraitMap(individuals, ontology);
        }

        for(String band : renderingOrder){
            ArrayList<OWLNamedIndividual> assocs = traitLocations.get(band).getAssociations();

            for(OWLNamedIndividual ind : assocs){
                if(individuals.contains(ind)){
                    for (Renderlet r : renderlets) {
                        if (r.canRender(this, ontology, ind)) {
                            getLog().debug("Dispatching render() request to renderlet '" + r.getName() + "'");
                            r.render(this, ontology, ind);
                        }
                    }

                }
            }
        }

 /*       for (OWLNamedIndividual individual : individuals) {

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
        }         */

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

    public ArrayList<String> buildTraitMap(Set<OWLNamedIndividual> individuals, OWLOntology ontology){

        for (OWLNamedIndividual individual : individuals) {
            boolean isBand =
                    checkType(individual, ontology, IRI.create(OntologyConstants.CYTOGENIC_REGION_CLASS_IRI));

            if (isBand) {
                OWLDataFactory df = manager.getOWLDataFactory();
                OWLDataProperty has_name = df.getOWLDataProperty(IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));
                String bandName = null;

                if(individual.getDataPropertyValues(has_name,ontology).size() != 0){
                    OWLLiteral cytoband = individual.getDataPropertyValues(has_name,ontology).iterator().next();
                    bandName = cytoband.getLiteral();
                }

                if(renderabledBands.contains(bandName)){

                    BandInformation info = new BandInformation(bandName);

    /*band - SNP - association via chained DL query --> 5 minutes for 12-study-test ontology*/
    //                OWLObjectProperty located_in = df.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));
    //                OWLObjectHasValue inBand = df.getOWLObjectHasValue(located_in, individual);
    //                OWLClass snp = df.getOWLClass(IRI.create(OntologyConstants.SNP_CLASS_IRI));
    //
    //                OWLObjectIntersectionOf snp_band = df.getOWLObjectIntersectionOf(snp, inBand);
    //                OWLObjectProperty is_about = df.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
    //
    //                OWLObjectSomeValuesFrom some_snps = df.getOWLObjectSomeValuesFrom(is_about, snp_band);
    //
    //                OWLClass association = df.getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
    //
    //                OWLObjectIntersectionOf assocs = df.getOWLObjectIntersectionOf(association, some_snps);
    //
    //                if(reasoner.getInstances(assocs,false).getFlattened().size() != 0){
    //                    Set<OWLNamedIndividual> set = reasoner.getInstances(assocs,false).getFlattened();
    //                    for(OWLNamedIndividual ind : set){
    //                        associations.add(ind.getIRI().toString());
    //                    }
    //
    //                }
    /*band - SNP - association via 2 reasoner queries --> 2s for 12-study-test ontology*/
                    OWLObjectProperty located_in = df.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));
                    OWLObjectPropertyExpression location_of = located_in.getInverseProperty();

                    OWLObjectProperty is_about = df.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
                    OWLObjectPropertyExpression has_about = is_about.getInverseProperty();

                     Set<OWLNamedIndividual> snps = reasoner.getObjectPropertyValues(individual,location_of).getFlattened();

                    for(OWLNamedIndividual snp : snps){
                        Set<OWLNamedIndividual> assocs = reasoner.getObjectPropertyValues(snp,has_about).getFlattened();

                        for(OWLNamedIndividual ind : assocs){
                            info.setAssociation(ind);
                            String name = getTraitName(ind, ontology, df);
                            if(!info.getTraitNames().contains(name)){
                                info.setTraitName(name);
                            }
                        }
                    }

                    traitLocations.put(bandName, info);
                }
                else{
                    getLog().debug("Band " + bandName + " is not a renderable cytogenetic band");
                }
             }
        }

        return sortBands(new ArrayList<String>(traitLocations.keySet()));
    }

    public String getTraitName(OWLNamedIndividual association, OWLOntology ontology, OWLDataFactory dataFactory){
        String traitName = null;

        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        OWLIndividual[] related = association.getObjectPropertyValues(is_about,ontology).toArray(new OWLIndividual[0]);

        if(allTraits == null){
            OWLClass ef = dataFactory.getOWLClass(IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI));
            allTraits = reasoner.getInstances(ef,false).getFlattened();
        }

        for(int i = 0; i < related.length; i++){
            if(allTraits.contains(related[i])){
                OWLNamedIndividual trait = (OWLNamedIndividual)related[i];
                OWLClassExpression[] allTypes = trait.getTypes(ontology).toArray(new OWLClassExpression[0]);

                for(int j = 0; j < allTypes.length; j++){
                    OWLClass typeClass = allTypes[j].asOWLClass();
                    IRI typeIRI = typeClass.getIRI();
                    if(typeIRI.toString().equals(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI)){
                        OWLDataProperty has_name = dataFactory.getOWLDataProperty(IRI.create(OntologyConstants.HAS_GWAS_TRAIT_NAME_PROPERTY_IRI));

                        if(association.getDataPropertyValues(has_name,ontology).size() != 0){
                            OWLLiteral name = association.getDataPropertyValues(has_name,ontology).iterator().next();
                            traitName = name.getLiteral();
                        }
                        else{
                            getLog().warn("Trait " + trait + " has no name");
                        }
                    }
                    else{
                        traitName = efoLabels.get(typeIRI);
                    }
                }
            }
        }
        return traitName;
    }

//sort all cytogenetic bands by chromosome, and for each chromosome, sort the p-arm bands in descending and the q-arm bands in ascending order
    public ArrayList<String> sortBands(ArrayList<String> bands){

        Collections.sort(bands);
        ArrayList<String> sorted = bands;
        int index = 0;
 /*       ArrayList<String> ps = new ArrayList<String>();
        ArrayList<String> qs = new ArrayList<String>();       */

        while(index < bands.size()){
            String current = bands.get(index);

            if(index == 0){
                traitLocations.get(current).setPreviousBand(null);
            }

            String next;

            if(index == bands.size()-1){
                traitLocations.get(current).setNextBand(null);
            }
            else  {
                next = bands.get(index+1);
    //if both bands are on the same arm, check that they're on the same chromosome
                if((current.contains("p") && next.contains("p")) || (current.contains("q") && next.contains("q"))){
                    String chrom1, chrom2;
                    if(current.contains("p")){
                        chrom1 = current.split("p")[0];
                    }
                    else{
                        chrom1 = current.split("q")[0];
                    }
                    if(next.contains("p")){
                        chrom2 = next.split("p")[0];
                    }
                    else{
                        chrom2 = next.split("q")[0];
                    }

                    if(chrom1.equals(chrom2)){
                        traitLocations.get(current).setNextBand(next);
                        traitLocations.get(next).setPreviousBand(current);
                    }
                    else {
                        traitLocations.get(current).setNextBand(null);
                        traitLocations.get(next).setPreviousBand(null);
                    }
                }
                else{
                    traitLocations.get(current).setNextBand(null);
                    traitLocations.get(next).setPreviousBand(null);
                }
            }

            index++;
        }

  /*      for(int i = 0; i < bands.size(); i++){
            if(traitLocations.get(bands.get(i)).getPreviousBand() == null){
                System.out.print("bottom \t");
            }
            else{
                System.out.print(traitLocations.get(bands.get(i)).getPreviousBand() + "\t");
            }
            System.out.print(bands.get(i) + "\t");

            if(traitLocations.get(bands.get(i)).getNextBand() == null){
                System.out.println("top");
            }
            else{
                System.out.println(traitLocations.get(bands.get(i)).getNextBand());
            }


        }             */

        return sorted;
    }
}

