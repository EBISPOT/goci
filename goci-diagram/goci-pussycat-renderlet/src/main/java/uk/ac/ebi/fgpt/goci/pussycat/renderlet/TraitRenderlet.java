package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.w3c.dom.Element;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 06/03/12
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */

@ServiceProvider
public class TraitRenderlet implements Renderlet<OWLOntology, OWLIndividual> {

    /*
    * TraitRenderlet should retrieve the appropriate RGB colour for the trait being rendered from a hardcoded resource file
    *
    *
    * */

    private String traitName;

    @Override
    public String getName() {
        if(traitName == null){
            return "GWAS trait";
        }
        else{
            return traitName;
        }
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
    public boolean canRender(RenderletNexus nexus, Object renderingContext, Object renderingEntity) {
        boolean renderable = false;
        if (renderingContext instanceof OWLOntology){
            OWLOntology ontology = (OWLOntology)renderingContext;

            if (renderingEntity instanceof OWLNamedIndividual){
                OWLNamedIndividual individual = (OWLNamedIndividual)renderingEntity;

                if(nexus.getLocationOfRenderedEntity(individual)==null){
                    OWLClassExpression[] allTypes = nexus.getReasoner().getTypes(individual,false).getFlattened().toArray(new OWLClassExpression[0]);

                    for(int i = 0; i < allTypes.length; i++){
                        OWLClass typeClass = allTypes[i].asOWLClass();

                        if(typeClass.getIRI().equals(IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI))){
                            renderable = true;
                            break;
                        }
                    }
               }

            }
        }

        return renderable;
    }

    @Override
    public void render(RenderletNexus nexus, OWLOntology renderingContext, OWLIndividual renderingEntity) {
        OWLNamedIndividual gwasTrait = (OWLNamedIndividual)renderingEntity;
        setTraitName(gwasTrait, renderingContext, nexus);
        OWLNamedIndividual association = getAssociation(nexus, gwasTrait, renderingContext);

        SVGArea associationSVG = nexus.getLocationOfRenderedEntity(association);
        Element assocG = nexus.getRenderingEvent(association).getRenderedSVG();

        String location = assocG.getAttribute("transform");


        Element trait = nexus.createSVGElement("circle");
        trait.setAttribute("transform",location);

        String bandName = getSNPLocation(association,renderingContext);

        double alength =  associationSVG.getWidth();
        double radius = 0.2*alength;
        double ax = associationSVG.getX();
        double cx;

        if(nexus.getAssociations(bandName).size() == 1){
            cx = ax+alength+radius;
        }
        else{
            int position = nexus.getAssociations(bandName).size();
            cx = ax+alength+((position+(position-1))*radius);
        }

        double cy = associationSVG.getY();
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

         trait.setAttribute("id",getName());
         trait.setAttribute("title",getName());

         nexus.addSVGElement(trait);

     }

    public void setTraitName(OWLNamedIndividual individual, OWLOntology ontology, RenderletNexus nexus){

/*       OWLOntologyManager manager = new OntologyConfiguration().getOWLOntologyManager();

        System.out.println("Manager handles: "  + manager.getOntologies().size() + " ontologies");
        OWLReasoner reasoner = nexus.getReasoner();

        Set<OWLClass> allTraits = reasoner.getTypes(individual,false).getFlattened();
        OWLClass leaf = null;
        int largest = 0;

        for(OWLClass current : allTraits){
            int parents = reasoner.getSuperClasses(current, false).getFlattened().size();

            if(parents > largest){
                largest = parents;
                leaf = current;
            }
        }

        OWLDataFactory dataFactory = manager.getOWLDataFactory();

        OWLAnnotationProperty label = dataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        System.out.println(leaf);

        for(OWLAxiom axiom: ontology.getReferencingAxioms(leaf)){
             System.out.println(axiom);
        }

        for (OWLAnnotation annotation : dataFactory.getOWLClass(leaf.getIRI()).getAnnotations(ontology, label)) {
            System.out.println("Magic! I made it this far...");
            if (annotation.getValue() instanceof OWLLiteral) {
                System.out.println("... but apparently annotation isn't an OWLLiteral");
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                traitName = val.getLiteral();
                System.out.println(traitName);
            }
        }                               */
    }

    public OWLNamedIndividual getAssociation(RenderletNexus nexus, OWLNamedIndividual trait, OWLOntology ontology){
        OWLReasoner reasoner = nexus.getReasoner();

        OWLDataFactory dataFactory = OWLManager.createOWLOntologyManager().getOWLDataFactory();
        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));

        OWLObjectPropertyExpression has_about = is_about.getInverseProperty();
        OWLNamedIndividual association = (OWLNamedIndividual)reasoner.getObjectPropertyValues(trait,has_about).getFlattened().toArray(new OWLIndividual[0])[0];

        return association;
    }

    public String getSNPLocation(OWLNamedIndividual association, OWLOntology ontology){
        String bandName = null;
        OWLDataFactory dataFactory = OWLManager.createOWLOntologyManager().getOWLDataFactory();

//get all the is_about individuals of this trait-assocation
        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        OWLIndividual[] related = association.getObjectPropertyValues(is_about,ontology).toArray(new OWLIndividual[0]);

        for(int k = 0; k < related.length; k++){
            OWLClassExpression[] allTypes = related[k].getTypes(ontology).toArray(new OWLClassExpression[0]);

//find the association that is of type SNP
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
        return bandName;
    }

    protected String getColour(int i){
        String[] colours = {"blue", "green", "red", "yellow", "purple", "orange"};
        return colours[i];
    }
}



