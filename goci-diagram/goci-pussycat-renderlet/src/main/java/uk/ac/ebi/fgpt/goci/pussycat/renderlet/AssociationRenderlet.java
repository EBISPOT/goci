package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.w3c.dom.Element;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGCanvas;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome.ChromosomeRenderlet;

import java.util.Random;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 18/04/12
 * Time: 13:38
 * To change this template use File | Settings | File Templates.
 */


@ServiceProvider
public class AssociationRenderlet implements Renderlet<OWLOntology, OWLNamedIndividual> {

    private String bandName;

    @Override
    public String getName() {

        //this should be name of the trait this is associated with
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getDescription() {
        return ("This is a renderlet displaying " + getDisplayName());
    }

    @Override
    public boolean canRender(RenderletNexus nexus, Object renderingContext, Object owlEntity) {

        boolean renderable = false;
        if (renderingContext instanceof OWLOntology){
            OWLOntology ontology = (OWLOntology)renderingContext;

            if (owlEntity instanceof OWLNamedIndividual){
                OWLNamedIndividual individual = (OWLNamedIndividual)owlEntity;

                if(nexus.getLocationOfRenderedEntity(individual)==null){
                    getSNPLocation(individual, ontology);

//there is no other association in this chromosmal band yet - render
                    if(nexus.getAssociations(bandName) == null){
                        renderable = true;
                    }
                    /*
//there is already another association in this band - can't render the association but need to render the trait as well as add to various nexus lists
else{

}
}
else{
//means this has already been rendered, e.g. if this is a second query - does this case need to be dealt with?*/
                }
            }
        }

        return renderable;

    }

    @Override
    public Element render(RenderletNexus nexus, OWLOntology renderingContext, OWLNamedIndividual renderingEntity) {
        String chromosome;
        Element g = nexus.createSVGElement("g");

        if(bandName.contains("p")){
            chromosome = bandName.split("p")[0];
        }
        else{
            chromosome = bandName.split("q")[0];
        }

        Set<Renderlet> renderlets = nexus.getRenderlets();

        for(Renderlet r : renderlets){
            if(r instanceof ChromosomeRenderlet){
                String comp = r.getName().split(" ")[1];

                if(comp.equals(chromosome)){

                    g.setAttribute("id",renderingEntity.getIRI().toString());
                    g.setAttribute("transform", chromosomeTransform(chromosome));

                    SVGArea band = ((ChromosomeRenderlet) r).getBands().get(bandName);
//print statement to keep track of which band is being processed as I've had trouble with some bands
 //          System.out.println(bandName);

                    double x = band.getX();
                    double y = band.getY();
                    double width = band.getWidth();
                    double  height = band.getHeight();

                    double newY = y+(height/2);
                    double length = 1.75*width;

                    StringBuilder d = new StringBuilder();
                    d.append("m ");
                    d.append(Double.toString(x));
                    d.append(",");
                    d.append(Double.toString(newY));
                    d.append(" ");
                    d.append(Double.toString(length));
                    d.append(",0.0");

                    Element path = nexus.createSVGElement("path");
                    path.setAttribute("d",d.toString());
                    path.setAttribute("style","fill:none;stroke:#211c1d;stroke-width:1.1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none");

                    g.appendChild(path);

                    SVGArea currentArea = new SVGArea(x,newY,length,0,0);
                    RenderingEvent event = new RenderingEvent(renderingEntity, g, currentArea, this);
                    nexus.renderingEventOccurred(event);
                    nexus.setAssociation(bandName, renderingEntity);
                    
                    
//THIS PART IS ONLY TEMPORARY - CREATE A MORE ELEGANT SOLUTION SOON!
                    
                    Element trait = nexus.createSVGElement("circle");

                    double radius = 0.4*width;
                    double cx = x+length+radius;
                    double cy = newY;
                    trait.setAttribute("cx", Double.toString(cx));
                    trait.setAttribute("cy", Double.toString(cy));
                    trait.setAttribute("r", Double.toString(radius));
          //          trait.setAttribute("style", "fill:blue;stroke:#211c1d;stroke-width:0.8;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none");


                    Random generator = new Random();
                    int index = generator.nextInt(5);

                    String colour = getColour(index);
                    
                    trait.setAttribute("fill",colour);
                    trait.setAttribute("stroke","black");
                    trait.setAttribute("stroke-width", "0.5");
                    
                    String name = getTrait(renderingEntity,renderingContext);
                    trait.setAttribute("id",name);
                    trait.setAttribute("title",name);
                    g.appendChild(trait);
                    
                    
                    break;
                }
            }
        }


        return g;
    }


    public String getTrait(OWLNamedIndividual individual, OWLOntology ontology){
        String name = "GWAS trait";
  /*      OWLDataFactory dataFactory = OWLManager.createOWLOntologyManager().getOWLDataFactory();

//get all the is_about individuals of this trait-assocation
        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        OWLNamedIndividual[] related = individual.getObjectPropertyValues(is_about,ontology).toArray(new OWLNamedIndividual[0]);
        OWLClassExpression[] allTraits = null;
        
        System.out.println("No of is-about axioms: " + related.length);

        for(int k = 0; k < related.length; k++){
            OWLClassExpression[] allTypes = related[k].getTypes(ontology).toArray(new OWLClassExpression[0]);


//find the individual that is of type "experimental factor"
            for(int i = 0; i < allTypes.length; i++){
                OWLClass typeClass = allTypes[i].asOWLClass();

                if(typeClass.getIRI().equals(IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI))){
                    System.out.println("Found the trait   " + allTypes.length);
                    allTraits = allTypes;
                }
            }
        }

        OWLClass leaf = null;

        if(allTraits.length > 0){
            leaf = allTraits[0].asOWLClass();
            int largest = leaf.getSuperClasses(ontology).size();
            System.out.println(largest);
            for(int j = 1; j < allTraits.length; j++){
                OWLClass current = allTraits[j].asOWLClass();
                int parents = current.getSuperClasses(ontology).size();
    
                if(parents > largest){
                    System.out.println(parents + "\t" + largest);
                    largest = parents;
                    leaf = current;
                    System.out.println(largest);
                }
            }
        }

        OWLAnnotationProperty label = dataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        for (OWLAnnotation annotation : leaf.getAnnotations(ontology, label)) {
            if (annotation.getValue() instanceof OWLLiteral) {
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                name = val.getLiteral();
                System.out.println(name);
            }
        }            */
          return name;
    }

    public void getSNPLocation(OWLNamedIndividual individual, OWLOntology ontology){
        bandName = null;
        OWLDataFactory dataFactory = OWLManager.createOWLOntologyManager().getOWLDataFactory();

//get all the is_about individuals of this trait-assocation
        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        OWLIndividual[] related = individual.getObjectPropertyValues(is_about,ontology).toArray(new OWLIndividual[0]);

        for(int k = 0; k < related.length; k++){
            OWLClassExpression[] allTypes = related[k].getTypes(ontology).toArray(new OWLClassExpression[0]);

//find the individual that is of type SNP
            for(int i = 0; i < allTypes.length; i++){
                OWLClass typeClass = allTypes[i].asOWLClass();

                if(typeClass.getIRI().equals(IRI.create(OntologyConstants.SNP_CLASS_IRI))){
                    OWLNamedIndividual SNP = (OWLNamedIndividual)related[k];

//get the SNP's cytogenetic band
                    OWLObjectProperty has_band = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));
                    OWLIndividual band = SNP.getObjectPropertyValues(has_band,ontology).toArray(new OWLIndividual[0])[0];

//get the band's name
                    OWLDataProperty has_name = dataFactory.getOWLDataProperty(IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));
                    bandName = band.getDataPropertyValues(has_name,ontology).toArray(new OWLLiteral[0])[0].getLiteral();

                }
            }
        }
    }

    public String chromosomeTransform(String chromosome){
        int position;
        if(chromosome.equals("X")){
            position = 22;
        }
        else if(chromosome.equals("Y")){
            position = 23;
        }
        else {
            position = Integer.parseInt(chromosome)-1;
        }
        int height = SVGCanvas.canvasHeight;
        int width = SVGCanvas.canvasWidth;

        double chromWidth = (double)width/12;
        double xCoordinate;
        double yCoordinate = 0;

        if (position < 12){
            xCoordinate = position * chromWidth;
        }
        else{
            xCoordinate = (position-12) * chromWidth;
            yCoordinate = (double)height/2;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("translate(");
        builder.append(Double.toString(xCoordinate));
        builder.append(",");
        builder.append(Double.toString(yCoordinate));
        builder.append(")");

        return builder.toString();
    }
    
    protected String getColour(int i){
        String[] colours = {"blue", "green", "red", "yellow", "purple", "orange"};
        return colours[i];
    }
}
