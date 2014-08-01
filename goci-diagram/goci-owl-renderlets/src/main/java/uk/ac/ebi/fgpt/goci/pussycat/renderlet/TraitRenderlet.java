package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.layout.ColourMapper;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.utils.OntologyUtils;

import java.util.Set;

/**
 * A renderlet that is capable of rendering visualisations of "traits".  A trait is an OWL class from the experimental
 * factor ontology (EFO)
 *
 * @author Dani Welter
 * @date 06/03/12
 */
@ServiceProvider
public class TraitRenderlet implements Renderlet<OWLReasoner, OWLIndividual> {

    /*
    * TraitRenderlet should retrieve the appropriate RGB colour for the trait being rendered from a hardcoded
    * resource file
    *
    *
    * */

//    private String traitName;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override
    public String getName() {
        return "GWAS trait renderlet";
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
        if (renderingContext instanceof OWLOntology) {
            if (renderingEntity instanceof OWLNamedIndividual) {
                // done all the checks we can without inferring types, don't bother with this as it's tooooo slooooowww
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(RenderletNexus nexus, OWLReasoner reasoner, OWLIndividual individual) {
        OWLNamedIndividual gwasTrait = (OWLNamedIndividual) renderingEntity;
        OWLNamedIndividual association = getAssociation(nexus, gwasTrait);

        Element trait = null;

        if (association != null) {
            SVGArea associationSVG = nexus.getLocationOfEntity(association);

            if (nexus.getRenderingEvent(association) != null) {
                Element assocG = nexus.getRenderingEvent(association).getRenderedSVG();

                String location = assocG.getAttribute("transform");
                String bandName = getSNPLocation(nexus, association, renderingContext);

                if (bandName != null) {
                    trait = builder.createElement("circle");
                    BandInformation band = nexus.getBandLocations().get(bandName);

                    trait.setAttribute("transform", location);

                    double alength = associationSVG.getWidth();
                    double radius = 0.2 * alength;
                    double ax = associationSVG.getX();
                    double ay = associationSVG.getY();
                    double displacement = associationSVG.getHeight();
                    double cx, cy;
                    int size = band.getRenderedTraits().size();

                    int horizontal = size % 6;
                    int vertical = size / 6;

                    if (size == 0) {
                        cx = ax + alength + radius;
                    }
                    else {
                        if (vertical % 2 == 0) {
                            cx = ax + alength + (((2 * horizontal) + 1) * radius);
                        }
                        else {
                            cx = ax + alength + (((2 * horizontal) + 2) * radius);
                        }
                    }
                    cy = ay + displacement + (vertical * radius);

                    trait.setAttribute("cx", Double.toString(cx));
                    trait.setAttribute("cy", Double.toString(cy));
                    trait.setAttribute("r", Double.toString(radius));

                    String colour = getColour(gwasTrait, nexus);

                    trait.setAttribute("fill", colour);
                    trait.setAttribute("stroke", "black");
                    trait.setAttribute("stroke-width", "0.5");

                    String traitName = getTraitName(gwasTrait, renderingContext, nexus, association);
//                    if(traitName.contains("'")){
//                        traitName = traitName.replace("'", "\\'");
//                    }
//                    String mo = "showTooltip('" + traitName + "')";
//                    trait.setAttribute("onmouseover", mo);
//                    trait.setAttribute("onmouseout", "hideTooltip()");
//                    trait.setAttribute("id", traitName);

                    trait.setAttribute("gwasname", traitName);

                    IRI iri = getTraitClass(gwasTrait, renderingContext);
                    String traitClass = OntologyUtils.getShortForm(iri, renderingContext);
                    getLog().trace("Setting CSS class for trait '" + gwasTrait + "' to " + traitClass);
                    trait.setAttribute("class", "gwas-trait " + traitClass);
                    trait.setAttribute("fading", "false");

                    String assocIRI = OntologyUtils.getShortForm(association.getIRI(), renderingContext);
                    getLog().trace("Setting gwasassociation attribute for trait '" + gwasTrait + "' to " + assocIRI);
//                    String summaryFunction = "showSummary('" + assocIRI;
//                    trait.setAttribute("onclick", summaryFunction);

                    trait.setAttribute("gwasassociation", assocIRI);

                    SVGArea currentArea = new SVGArea(cx, cy, 2 * radius, 2 * radius, 0);
                    RenderingEvent<OWLIndividual> event = new RenderingEvent<OWLIndividual>(
                            renderingEntity, trait, currentArea, this);
                    nexus.renderingEventOccurred(event);
                    band.setRenderedTrait(traitName, gwasTrait);
                }
            }
        }
        return trait;
    }

    private IRI getTraitClass(OWLNamedIndividual individual,
                              OWLOntology ontology) {
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

    private String getTraitName(OWLNamedIndividual individual,
                                OWLOntology ontology,
                                RenderletNexus nexus,
                                OWLNamedIndividual association) {
        String traitName;

  /*      IRI traitClass = getTraitClass(individual, ontology);
        if (traitClass.toString().equals(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI)) {
            OWLDataProperty has_name = nexus.getManager().getOWLDataFactory().getOWLDataProperty(
                    IRI.create(OntologyConstants.HAS_GWAS_TRAIT_NAME_PROPERTY_IRI));
            if (association.getDataPropertyValues(has_name, ontology).size() != 0) {
                OWLLiteral name = association.getDataPropertyValues(has_name, ontology).iterator().next();
                traitName = name.getLiteral();
            }
            else {
                getLog().warn("Trait " + individual + " has no name");
            }
        }
        else {
            traitName = nexus.getEfoLabels().get(traitClass);
        }      */

//        OWLDataProperty has_name = nexus.getManager().getOWLDataFactory().getOWLDataProperty(IRI.create(OntologyConstants.HAS_GWAS_TRAIT_NAME_PROPERTY_IRI));
//
//        if(association.getDataPropertyValues(has_name,ontology).size() != 0){
//            OWLLiteral name = association.getDataPropertyValues(has_name,ontology).iterator().next();
//            traitName = name.getLiteral();
//        }
//        else{
//            getLog().warn("Trait " + individual + " has no name");
        IRI traitClass = getTraitClass(individual, ontology);
        traitName = nexus.getEfoLabels().get(traitClass);
//        }

        return traitName;
    }

    public OWLNamedIndividual getAssociation(RenderletNexus nexus, OWLNamedIndividual trait) {
        OWLReasoner reasoner = nexus.getReasoner();

        OWLDataFactory dataFactory = nexus.getManager().getOWLDataFactory();
        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));

        OWLObjectPropertyExpression has_about = is_about.getInverseProperty();

        OWLNamedIndividual association = null;
        if (reasoner.getObjectPropertyValues(trait, has_about).getFlattened().size() != 0) {
            association = reasoner.getObjectPropertyValues(trait, has_about).getFlattened().iterator().next();
        }
        else {
            getLog().warn("Trait " + trait + " has no association");
        }

        return association;
    }

    public String getSNPLocation(RenderletNexus nexus, OWLNamedIndividual association, OWLOntology ontology) {
        String bandName = null;
        OWLDataFactory dataFactory = nexus.getManager().getOWLDataFactory();

//get all the is_about individuals of this trait-assocation
        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        Set<OWLIndividual> allRelated = association.getObjectPropertyValues(is_about, ontology);

        for (OWLIndividual related : allRelated) {
            Set<OWLClassExpression> allTypes = related.getTypes(ontology);

//find the association that is of type SNP
            for (OWLClassExpression typeClassExpression : allTypes) {
                OWLClass typeClass = typeClassExpression.asOWLClass();
                if (IRI.create(OntologyConstants.SNP_CLASS_IRI).equals(typeClass.getIRI())) {
                    OWLNamedIndividual SNP = related.asOWLNamedIndividual();

//get the SNP's cytogenetic band
                    OWLObjectProperty
                            has_band =
                            dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));

                    if (SNP.getObjectPropertyValues(has_band, ontology).size() != 0) {
                        OWLIndividual band = SNP.getObjectPropertyValues(has_band, ontology).iterator().next();
                        //get the band's name
                        OWLDataProperty
                                has_name =
                                dataFactory.getOWLDataProperty(IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));
                        bandName = band.getDataPropertyValues(has_name, ontology).iterator().next().getLiteral();
                    }
                    else {
                        getLog().warn("SNP " + SNP + " does not have a cytogentic band");
                    }
                }
            }
        }
        return bandName;
    }

    protected String getColour(OWLNamedIndividual trait, RenderletNexus nexus) {
        String colour;

        OWLReasoner reasoner = nexus.getReasoner();
        Set<OWLClass> allTypes = reasoner.getTypes(trait, false).getFlattened();
        Set<String> available = ColourMapper.COLOUR_MAP.keySet();

        OWLClass leaf = null;
        int largest = 0;

        if (allTypes.size() == 2) {
            colour = "#FFFFFF";
            getLog().debug("Trait " + trait + " is not mapped");
        }
        else {
            for (OWLClass t : allTypes) {
                String iri = t.getIRI().toString();
                int parents = reasoner.getSuperClasses(t, false).getFlattened().size();

                if (parents > largest && available.contains(iri)) {
                    largest = parents;
                    leaf = t;
                }
            }
            if (leaf != null) {
                colour = ColourMapper.COLOUR_MAP.get(leaf.getIRI().toString());
            }
            else {
                colour = "magenta";
                getLog().error("Could not identify a suitable colour category for trait " + trait);
            }
        }


        return colour;
    }
}



