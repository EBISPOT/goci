package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.layout.Association;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGBuilder;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome.ChromosomeRenderlet;
import uk.ac.ebi.fgpt.goci.utils.OntologyUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private OWLOntologyManager manager;
    private OWLReasoner reasoner;

    public DefaultRenderletNexus() {
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
        entityLocations.put(evt.getRenderedEntity(), evt.getSvgArea());
        renderedEntities.put(evt.getRenderedEntity(), evt);
    }

    public <O> SVGArea getLocationOfEntity(O entity) {
        return entityLocations.get(entity);
    }

    public <O> RenderingEvent getRenderingEvent(O renderedEntity) {
        return renderedEntities.get(renderedEntity);
    }


    @Override
    public String getSVG(OWLClassExpression classExpression) {
        diagramLogger.info("Rendering diagram for OWLClassExpression " + classExpression);
//check if the chromosomes have already been rendered, otherwise render them
        SVGBuilder svgBuilder = new SVGBuilder();

        // get the ontology loaded into the reasoner
        OWLOntology ontology = reasoner.getRootOntology();

        OWLClass ta = manager.getOWLDataFactory().getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
        diagramLogger.info("There are " + ta.getIndividuals(ontology).size() + " potential trait associations that could be rendered");

        getLog().trace("There are " + renderlets.size() + " registered renderlets");

        long start, end;
        start = System.currentTimeMillis();
        getLog().debug("Obtaining OWL individuals from reasoner");
        Set<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression, false).getFlattened();
        end = System.currentTimeMillis();
        double time = ((double) (end - start)) / 1000;
        getLog().info("OWL individuals for OWLClassExpression " + classExpression + " acquired in " + time + " s.");

        getLog().debug("There are " + individuals.size() + " owl individuals that satisfy the expression " +
                classExpression);

        if(renderedEntities.size() == 0){
//if the map is empty but OWLClassExpression isn't OWLThing, do a dummy rendering to rebuild the map
            if(!classExpression.isOWLThing()){
                getLog().debug("Empty maps and a subset to rendered - rebuild maps via dummy OWLThing rendering");
                OWLClassExpression everything = manager.getOWLDataFactory().getOWLThing();
                Set<OWLNamedIndividual> allIndividuals = reasoner.getInstances(everything,false).getFlattened();
                renderSVGFromScratch(svgBuilder, ontology, allIndividuals);
                getLog().trace("Rendering OWLClassExpression");
                svgBuilder = new SVGBuilder();
                renderFromExistingSVG(svgBuilder, ontology, individuals);
            }
            else {
                renderSVGFromScratch(svgBuilder, ontology, individuals);
                getLog().debug("Rendering complete");
            }
        }
        else{
            renderFromExistingSVG(svgBuilder, ontology, individuals);
            getLog().debug("Rendering complete");
        }

        return svgBuilder.getSVG();

    }

    public void renderChromosomes(SVGBuilder builder) {
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
                        getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                        builder.addElement(r.render(this, ontology, chrom, null));
                    }
                }
            }
        }
    }

    public void renderFromExistingSVG(SVGBuilder builder, OWLOntology ontology, Set<OWLNamedIndividual> individuals){
        getLog().trace("Rendering from existing SVG");
        OWLClass chromosome = ontology.getOWLOntologyManager()
                .getOWLDataFactory()
                .getOWLClass(IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI));
        NodeSet<OWLClass> all = reasoner.getSubClasses(chromosome, true);
        Set<OWLClass> allChroms = all.getFlattened();

        getLog().trace("There are " + allChroms.size() + " chromosomes");

        for(OWLClass chrom : allChroms){
            getLog().trace("Trying to render chromosome " + chrom);
            if(getRenderingEvent(chrom) != null){
                Element element = getRenderingEvent(chrom).getRenderedSVG();
                builder.addElement(element);
                getLog().trace("Rendering successful");
            }
            else {
                getLog().trace("Trying to render a chromosome that doesn't exist");
            }
        }
        getLog().trace("Chromsomes rendered");

        HashMap<String, ArrayList<OWLNamedIndividual>> currentBands = new HashMap<String, ArrayList<OWLNamedIndividual>>();

        Set<String> allBands = bandLocations.keySet();

        getLog().trace("Building the local band map");
        for(String band : allBands){
            ArrayList<OWLNamedIndividual> renderedAssociations = bandLocations.get(band).getRenderedAssociations();
            ArrayList<OWLNamedIndividual> toRender = new ArrayList<OWLNamedIndividual>();

            for(OWLNamedIndividual association : individuals){
                if(renderedAssociations.contains(association)){
                    toRender.add(association);
                }
            }
            if(toRender.size() != 0){
                currentBands.put(band,toRender);
            }
        }

        Set<String> bands = currentBands.keySet();

        getLog().trace("Starting to gather SVG elements");
        for(String band : bands){
            getLog().trace("Processing band " + band);
            int index = 1;
            Element associationSVG = null;
            ArrayList<Element> traits = new ArrayList<Element>();
            getLog().trace("There are " +currentBands.get(band).size() + " associations");
            for(OWLNamedIndividual association : currentBands.get(band)){
                if(index == 1){
                    getLog().trace("First association");
                    associationSVG = getRenderingEvent(association).getRenderedSVG();
                    associationSVG.setAttribute("class", "gwas-trait");
                    index++;
                }

                String assocIRI = OntologyUtils.getShortForm(association.getIRI(), ontology);
                OWLNamedIndividual trait = getTrait(association, ontology);
                if(trait != null){
                    getLog().debug("Rendering trait " + trait);
                    String name = getTraitName(trait,ontology,association);
                    boolean queued = false;

//if the SVG for this trait has already been queued for this band...
                    for(Element rendered : traits){
                        if(rendered.getAttribute("gwasname").equals(name)){
                            getLog().debug("SVG for this trait name is already queued for rendering");
//                            String mouseclick = rendered.getAttribute("onclick");
//                            rendered.setAttribute("onclick", mouseclick + "," + assocIRI);

                            String existing = rendered.getAttribute("gwasassociation");
                            rendered.setAttribute("gwasassociation", existing + "," + assocIRI);

                            queued = true;
                        }
                    }


                    if(!queued){
//scenario 1: this trait individual was rendered in the full rendering
                        if(getRenderingEvent(trait) != null){
                            Element traitSVG = getRenderingEvent(trait).getRenderedSVG();
//                            traitSVG.setAttribute("onclick", "showSummary(" + assocIRI);
                            traitSVG.setAttribute("gwasassociation", assocIRI);
                            traits.add(traitSVG);
                        }
//scenario 2: this trait individual was not rendered in the full rendering --> find a trait of the same name that was
                        else{
                            getLog().debug("Trait " + trait + " was not explicitly rendered in the full diagram");
                            if(bandLocations.get(band).getRenderedTraits().contains(name)){
                                OWLNamedIndividual rep = bandLocations.get(band).getRenderedTrait(name);
                                if(getRenderingEvent(rep) != null){
                                    Element traitSVG = getRenderingEvent(rep).getRenderedSVG();
//                                    traitSVG.setAttribute("onclick", "showSummary(" + assocIRI);
                                    traitSVG.setAttribute("gwasassociation", assocIRI);
                                    traits.add(traitSVG);
                                }

                            }
                        }
                    }
                    IRI traitIRI = getTraitClass(trait, ontology);
                    String traitClass = OntologyUtils.getShortForm(traitIRI, ontology);

                    if(associationSVG != null){
                        String existingClass = associationSVG.getAttribute("class");
                        associationSVG.setAttribute("class", existingClass + " " + traitClass);
                    }
                    else{
                        getLog().debug("Could not add CSS class element for association " + association);
                    }

                }
            }
            builder.addElement(associationSVG);

            for(Element svg : traits){
//                String onclick = svg.getAttribute("onclick");
//                svg.setAttribute("onclick", onclick + "')");

                builder.addElement(svg);
            }
        }
    }

    public void renderSVGFromScratch(SVGBuilder svgBuilder, OWLOntology ontology, Set<OWLNamedIndividual> individuals){
        renderChromosomes(svgBuilder);

        int assocCount=0;
        int dotCount=0;

        ArrayList<String> renderingOrder =  buildTraitMap(individuals, ontology);
        getLog().debug("Starting rendering of trait associations");
        for(String band : renderingOrder){
            ArrayList<Association> assocs = bandLocations.get(band).getAssociations();

            Element associationSVG = null;
            ArrayList<Element> traits = new ArrayList<Element>();

            for(Association assoc : assocs){
                OWLNamedIndividual ind = assoc.getAssociation();
                if(individuals.contains(ind)){
                    for (Renderlet ra : renderlets) {
                        if (ra.canRender(this, ontology, ind)) {
                            getLog().trace("Dispatching render() request to renderlet '" + ra.getName() + "'");

                            Element current =  ra.render(this, ontology, ind, svgBuilder);

                            if(current != null){
                                associationSVG = current;
                            }

                            OWLNamedIndividual trait = getTrait(ind, ontology);
                            if(trait != null){
                                getLog().trace("Trait: " + trait);

                                if(!allTraits.contains(trait))       {
                                    getLog().debug("This is probably a trait association!" + trait);
                                }

                                else{
                                    String traitName = getTraitName(trait, ontology, ind);
    //                                if(traitName.contains("'")){
    //                                    traitName = traitName.replace("'", "\\'");
    //                                }

                                    if(bandLocations.get(band).getRenderedTraits().contains(traitName)){
                                        getLog().trace("Trait " + traitName + " already rendered at band " + band);
                                        String assocIRI = OntologyUtils.getShortForm(ind.getIRI(), ontology);
                                        for(Element rendered : traits){
                                            if(rendered.getAttribute("gwasname") != null) {
                                                if(rendered.getAttribute("gwasname").equals(traitName)){
    //                                            String mouseclick = rendered.getAttribute("onclick");
    //                                            rendered.setAttribute("onclick", mouseclick + "," + assocIRI);
                                                    String existing = rendered.getAttribute("gwasassociation");
                                                    rendered.setAttribute("gwasassociation", existing + "," + assocIRI);
                                                }
                                            }
                                        }
                                    }
                                    else{
                                        getLog().trace("Trait " + traitName + " was not previously rendered at band " + band);
                                        for (Renderlet rt : getRenderlets()){
                                            if (rt.canRender(this, ontology, trait)) {
                                                getLog().trace("Dispatching render() request to renderlet '" + rt.getName() + "'");
                                                Element newTrait = rt.render(this, ontology, trait, svgBuilder);
                                                if(newTrait != null){
                                                    traits.add(newTrait);
                                                    IRI traitIRI = getTraitClass(trait, ontology);
                                                    String traitClass = OntologyUtils.getShortForm(traitIRI, ontology);

                                                    if(associationSVG != null){
                                                        String existingClass = associationSVG.getAttribute("class");
                                                        associationSVG.setAttribute("class", existingClass + " " + traitClass);
                                                    }
                                                    else{
                                                        getLog().debug("Could not add CSS class element for association " + ind);
                                                    }
                                                }
                                                else{
                                                    getLog().debug("No SVG was returned for trait " + trait);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else{
                                getLog().debug("The trait for association " + ind + " is null");
                            }
                        }
                    }

                }
            }
            svgBuilder.addElement(associationSVG);

            for(Association assoc : assocs){
                getRenderingEvent(assoc.getAssociation()).updateRenderedSVG(associationSVG);
            }
            diagramLogger.info("Number of trait associations for band " + band + ": " + traits.size());

            for(Element trait : traits){
                String name = trait.getAttribute("gwasname");
                String[] subs = trait.getAttribute("gwasassociation").split(",");

                diagramLogger.info("Trait " + name + " contains " + subs.length + " associations");
                dotCount = dotCount+1;
                assocCount = assocCount + subs.length;

                svgBuilder.addElement(trait);
            }
        }

        diagramLogger.info("There are " + dotCount + " dots representing " + assocCount + " associations on this diagram");
    }

    public boolean checkType(OWLNamedIndividual individual, OWLOntology ontology, IRI typeIRI) {
        boolean type = false;
        OWLClassExpression[] allTypes = individual.getTypes(ontology).toArray(new OWLClassExpression[0]);

        for (int i = 0; i < allTypes.length; i++) {
            OWLClass typeClass = allTypes[i].asOWLClass();

            if (typeIRI.equals(typeClass.getIRI())) {
                type = true;
                break;
            }
        }
        return type;
    }

    public ArrayList<String> buildTraitMap(Set<OWLNamedIndividual> individuals, OWLOntology ontology) {

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

                if(bandLocations.containsKey(bandName)){

                    BandInformation info = bandLocations.get(bandName);

    /*band - SNP - association via 2 reasoner queries --> 2s for 12-study-test ontology (compared to 5 minutes via chained DL query*/
                    OWLObjectProperty located_in = df.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));
                    OWLObjectPropertyExpression location_of = located_in.getInverseProperty();

                    OWLObjectProperty is_about = df.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
                    OWLObjectPropertyExpression has_about = is_about.getInverseProperty();

                     Set<OWLNamedIndividual> snps = reasoner.getObjectPropertyValues(individual,location_of).getFlattened();

                    for(OWLNamedIndividual snp : snps){
                        Set<OWLNamedIndividual> assocs = reasoner.getObjectPropertyValues(snp,has_about).getFlattened();

                        for(OWLNamedIndividual ind : assocs){
                            Date pubDate = null;
                            float pval=0;
                            OWLNamedIndividual trait = getTrait(ind,ontology);
                            String name = null;

                            if(trait != null){
                                name = getTraitName(trait, ontology, ind);
                                if(!info.getTraitNames().contains(name)){
                                    info.setTraitName(name);
                                }
                            }
                            else{
                                getLog().debug("Trait for association " + ind + " is null");
                            }

                            OWLDataProperty has_pval = df.getOWLDataProperty(IRI.create(OntologyConstants.HAS_P_VALUE_PROPERTY_IRI));
                            Set<OWLLiteral> pvals = ind.getDataPropertyValues(has_pval,ontology);

                            for(OWLLiteral p : pvals){
                                pval = p.parseFloat();
                            }

                            OWLObjectProperty part_of = df.getOWLObjectProperty(IRI.create(OntologyConstants.PART_OF_PROPERTY_IRI));
                            Set<OWLNamedIndividual> studies = reasoner.getObjectPropertyValues(ind,part_of).getFlattened();
                            OWLDataProperty has_pub_date = df.getOWLDataProperty(IRI.create(OntologyConstants.HAS_PUBLICATION_DATE_PROPERTY_IRI));

                            for(OWLNamedIndividual study : studies){
                                Set<OWLLiteral> pubdate = study.getDataPropertyValues(has_pub_date, ontology);
                                for(OWLLiteral date : pubdate){
                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+HH:mm");
                                    try {
                                        pubDate = formatter.parse(date.getLiteral().toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }
                                }
                            }

                            info.setAssociation(ind, name, pval, pubDate);
                        }
                    }
                    info.sortByDate();
                }
                else{
                    getLog().debug("Band " + bandName + " is not a renderable cytogenetic band");
                }
             }
        }

        return sortBands(new ArrayList<String>(bandLocations.keySet()));
    }

    public OWLNamedIndividual getTrait(OWLNamedIndividual association, OWLOntology ontology){
        OWLNamedIndividual trait = null;
        OWLObjectProperty is_about = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        OWLIndividual[] related = association.getObjectPropertyValues(is_about,ontology).toArray(new OWLIndividual[0]);

        if(allTraits == null){
            OWLClass ef = manager.getOWLDataFactory().getOWLClass(IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI));
            allTraits = reasoner.getInstances(ef,false).getFlattened();

            Set<OWLNamedIndividual> allAssociations = reasoner.getInstances(manager.getOWLDataFactory().getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI)),false).getFlattened();

            for(OWLNamedIndividual ta : allAssociations){
                if (allTraits.contains(ta)){
                    allTraits.remove(ta);
                    getLog().debug("Removed trait assocation " + ta.toString() + " from EF list");
                }
            }
        }

        for(int i = 0; i < related.length; i++){
            if(allTraits.contains(related[i])){
                trait = (OWLNamedIndividual)related[i];
            }
        }
        return trait;
    }

    public String getTraitName(OWLNamedIndividual trait, OWLOntology ontology, OWLNamedIndividual association){
        String traitName = null;
        OWLDataProperty has_name = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(OntologyConstants.HAS_GWAS_TRAIT_NAME_PROPERTY_IRI));

        if(association.getDataPropertyValues(has_name,ontology).size() != 0){
            OWLLiteral name = association.getDataPropertyValues(has_name,ontology).iterator().next();
            traitName = name.getLiteral();
        }
        else{
            getLog().warn("Trait " + trait + " has no name");

            OWLClassExpression[] allTypes = trait.getTypes(ontology).toArray(new OWLClassExpression[0]);
            for(int j = 0; j < allTypes.length; j++){
                OWLClass typeClass = allTypes[j].asOWLClass();
                IRI typeIRI = typeClass.getIRI();
                traitName = efoLabels.get(typeIRI);
            }
        }
        return traitName;
    }

//sort all cytogenetic bands by chromosome, and for each chromosome, sort the p-arm bands in descending and the q-arm bands in ascending order
    public ArrayList<String> sortBands(ArrayList<String> bands){

        Collections.sort(bands);
        ArrayList<String> sorted = bands;
        int index = 0;

/*"previous" and "next" band are for necessary for determining rendering offset. Bands that do not contain any trait associations don't need to considered*/
        ArrayList<String> renderable = new ArrayList<String>();

        for(String band : bands){
            if(bandLocations.get(band).getAssociations().size() != 0){
                renderable.add(band);
            }
        }

        while(index < renderable.size()){
            String current = renderable.get(index);

            if(index == 0){
                bandLocations.get(current).setPreviousBand(null);
            }

            String next;

            if(index == renderable.size()-1){
                bandLocations.get(current).setNextBand(null);
            }
            else  {
                next = renderable.get(index+1);
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

                        bandLocations.get(current).setNextBand(next);
                        bandLocations.get(next).setPreviousBand(current);
                    }
                    else {
                        bandLocations.get(current).setNextBand(null);
                        bandLocations.get(next).setPreviousBand(null);
                    }
                }
                else{
                    bandLocations.get(current).setNextBand(null);
                    bandLocations.get(next).setPreviousBand(null);
                }
            }


            index++;
        }

        return sorted;
    }

    public IRI getTraitClass(OWLNamedIndividual individual, OWLOntology ontology) {
        IRI traitClass = null;

        // this gets the first, asserted, non-experimental factor class and then exits
        // todo - fix this so that we get the most specific known type
        Set<OWLClassExpression> allTypes = individual.getTypes(ontology);
        if (allTypes.size() > 0) {
            for (OWLClassExpression typeClassExpression : allTypes) {
                OWLClass typeClass = typeClassExpression.asOWLClass();
                traitClass = typeClass.getIRI();
                if (!OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI.equals(traitClass.toString())) {
                    break;
                }
            }
        }
        else {
            getLog().error("Trait " + individual + " has no determinable type");
        }
        return traitClass;
    }
}

