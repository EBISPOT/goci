package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.layout.ColourMapper;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;

import java.util.Set;

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

    private Logger log = LoggerFactory.getLogger(getClass());
    protected Logger getLog() {
        return log;
    }

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
        OWLNamedIndividual association = getAssociation(nexus, gwasTrait, renderingContext);

        if(association != null){
            setTraitName(gwasTrait, renderingContext, nexus, association);

            SVGArea associationSVG = nexus.getLocationOfRenderedEntity(association);
            Element assocG = nexus.getRenderingEvent(association).getRenderedSVG();

            String location = assocG.getAttribute("transform");

            String bandName = getSNPLocation(nexus, association,renderingContext);

            if(bandName != null){
                Element trait = nexus.createSVGElement("circle");
                trait.setAttribute("transform",location);

                double alength =  associationSVG.getWidth();
                double radius = 0.2*alength;
                double ax = associationSVG.getX();
                double ay = associationSVG.getY();
                double displacement = associationSVG.getHeight();
                double cx, cy;
                int size;

                if(nexus.getRenderedTraits(bandName) == null){
                    size = 0;
                }
                else{
                    size = nexus.getRenderedTraits(bandName).size();
                }
                int horizontal = size%6;
                int vertical = size/6;


//                if((nexus.getRenderedTraits(bandName) == null) || (nexus.getRenderedTraits(bandName).size() < 6 )){
                if(nexus.getRenderedTraits(bandName) == null){
                    cx = ax+alength+radius;
                }
                else{
                     if(vertical%2 == 0){
                        cx = ax+alength+(((2*horizontal)+1)*radius);
                     }
                     else{
                         cx = ax+alength+(((2*horizontal)+2)*radius);
                     }
                }
                cy=ay + displacement + (vertical*radius);

                trait.setAttribute("cx", Double.toString(cx));
                trait.setAttribute("cy", Double.toString(cy));
                trait.setAttribute("r", Double.toString(radius));

                String colour = getColour(gwasTrait, nexus);

                trait.setAttribute("fill",colour);
                trait.setAttribute("stroke","black");
                trait.setAttribute("stroke-width", "0.5");

                trait.setAttribute("id",getName());

                String display;
                if(getName().contains("'")){
                     display = getName().replace("'", "\\'");
                }
                else{
                    display = getName();
                }

                String mo = "showTooltip('" + display + "')";
                trait.setAttribute("onmouseover",mo);
                trait.setAttribute("onmouseout", "hideTooltip()");

                nexus.addSVGElement(trait);
                nexus.setTrait(bandName, traitName);
            }
        }
     }

    public void setTraitName(OWLNamedIndividual individual, OWLOntology ontology, RenderletNexus nexus, OWLNamedIndividual association){

        OWLClassExpression[] allTypes = individual.getTypes(ontology).toArray(new OWLClassExpression[0]);

        for(int i = 0; i < allTypes.length; i++){
            OWLClass typeClass = allTypes[i].asOWLClass();
            IRI typeIRI = typeClass.getIRI();

            if(typeIRI.toString().equals(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI)){
                OWLDataProperty has_name = nexus.getManager().getOWLDataFactory().getOWLDataProperty(IRI.create(OntologyConstants.HAS_GWAS_TRAIT_NAME_PROPERTY_IRI));

                if(association.getDataPropertyValues(has_name,ontology).size() != 0){
                    OWLLiteral name = association.getDataPropertyValues(has_name,ontology).iterator().next();
                    traitName = name.getLiteral();
                }
                else{
                    getLog().warn("Trait " + individual + " has no name");
                }
            }
            else{
                traitName = nexus.getEfoLabels().get(typeIRI);
            }
          }
    }

    public OWLNamedIndividual getAssociation(RenderletNexus nexus, OWLNamedIndividual trait, OWLOntology ontology){
        OWLReasoner reasoner = nexus.getReasoner();

        OWLDataFactory dataFactory = nexus.getManager().getOWLDataFactory();
        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));

        OWLObjectPropertyExpression has_about = is_about.getInverseProperty();

        OWLNamedIndividual association = null;
        if(reasoner.getObjectPropertyValues(trait,has_about).getFlattened().size() != 0){
            association = reasoner.getObjectPropertyValues(trait,has_about).getFlattened().iterator().next();
        }
        else{
            getLog().warn("Trait " + trait + " has no association");
        }

        return association;
    }

    public String getSNPLocation(RenderletNexus nexus, OWLNamedIndividual association, OWLOntology ontology){
        String bandName = null;
        OWLDataFactory dataFactory = nexus.getManager().getOWLDataFactory();

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

                    if(SNP.getObjectPropertyValues(has_band,ontology).size() != 0){
                        OWLIndividual band = SNP.getObjectPropertyValues(has_band,ontology).iterator().next();
   //get the band's name
                        OWLDataProperty has_name = dataFactory.getOWLDataProperty(IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));
                        bandName = band.getDataPropertyValues(has_name,ontology).iterator().next().getLiteral();
                    }
                    else{
                        getLog().warn("SNP " + SNP + " does not have a cytogentic band");
                    }
                }
            }
        }
        return bandName;
    }

    protected String getColour(OWLNamedIndividual trait, RenderletNexus nexus){
        String colour;

        OWLReasoner reasoner = nexus.getReasoner();
        Set<OWLClass> allTypes = reasoner.getTypes(trait,false).getFlattened();
        Set<String> available = ColourMapper.COLOUR_MAP.keySet();

        OWLClass leaf = null;
        int largest = 0;

        for(OWLClass t : allTypes){
            String iri = t.getIRI().toString();
            int parents = reasoner.getSuperClasses(t, false).getFlattened().size();

            if (parents > largest && available.contains(iri)){
                largest = parents;
                leaf = t;
            }
        }
        if(leaf != null){
            colour = ColourMapper.COLOUR_MAP.get(leaf.getIRI().toString());
        }
        else {
            colour = "magenta";
        }


        return colour;
    }
}



