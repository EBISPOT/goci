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
import uk.ac.ebi.fgpt.goci.utils.OntologyUtils;

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
        boolean renderable = false;
        if (renderingContext instanceof OWLOntology) {
            if (renderingEntity instanceof OWLNamedIndividual) {
                OWLNamedIndividual individual = (OWLNamedIndividual) renderingEntity;

                if (nexus.getLocationOfRenderedEntity(individual) == null) {
                    Set<OWLClass> allTypesSet = nexus.getReasoner()
                                                     .getTypes(individual, false)
                                                     .getFlattened();
                    OWLClass[] allTypes = allTypesSet.toArray(new OWLClass[allTypesSet.size()]);
                    for (OWLClass typeClass : allTypes) {
                        if (typeClass.getIRI().equals(IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI))) {
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
        OWLNamedIndividual gwasTrait = (OWLNamedIndividual) renderingEntity;
        OWLNamedIndividual association = getAssociation(nexus, gwasTrait, renderingContext);

        if (association != null) {
            SVGArea associationSVG = nexus.getLocationOfRenderedEntity(association);
            Element assocG = nexus.getRenderingEvent(association).getRenderedSVG();

            String location = assocG.getAttribute("transform");

            String bandName = getSNPLocation(nexus, association, renderingContext);

            if (bandName != null) {
                Element trait = nexus.createSVGElement("circle");
                trait.setAttribute("transform", location);

                double alength = associationSVG.getWidth();
                double radius = 0.2 * alength;
                double ax = associationSVG.getX();
                double ay = associationSVG.getY();
                double displacement = associationSVG.getHeight();
                double cx, cy;
                int size;

                if (nexus.getRenderedTraits(bandName) == null) {
                    size = 0;
                }
                else {
                    size = nexus.getRenderedTraits(bandName).size();
                }
                int horizontal = size % 6;
                int vertical = size / 6;


//                if((nexus.getRenderedTraits(bandName) == null) || (nexus.getRenderedTraits(bandName).size() < 6 )){
                if (nexus.getRenderedTraits(bandName) == null) {
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
                if (traitName.contains("'")) {
                    traitName = traitName.replace("'", "\\'");
                }

                String mo = "showTooltip('" + traitName + "')";
                trait.setAttribute("onmouseover", mo);
                trait.setAttribute("onmouseout", "hideTooltip()");
                trait.setAttribute("id", traitName);

                IRI iri = getTraitClass(gwasTrait, renderingContext, nexus, association);
                String traitClass = OntologyUtils.getShortForm(iri, renderingContext);
                getLog().trace("Setting CSS class for trait '" + gwasTrait + "' to " + traitClass);
                trait.setAttribute("class", traitClass + " gwas-trait");

                nexus.addSVGElement(trait);
                nexus.setTrait(bandName, traitName);
            }
        }
    }

    private IRI getTraitClass(OWLNamedIndividual individual,
                              OWLOntology ontology,
                              RenderletNexus nexus,
                              OWLNamedIndividual association) {
        IRI traitClass = null;

        // this gets the first non-experimental factor class and then breaks
        // todo - fix this so that we get the most specific known type
        Set<OWLClassExpression> allTypes = individual.getTypes(ontology);
        if (allTypes.size() > 0) {
            for (OWLClassExpression typeClassExpression : allTypes) {
                OWLClass typeClass = typeClassExpression.asOWLClass();
                traitClass = typeClass.getIRI();
                if (!traitClass.toString().equals(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI)) {
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
        String traitName = null;

        IRI traitClass = getTraitClass(individual, ontology, nexus, association);
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
        }

        return traitName;
    }

    public OWLNamedIndividual getAssociation(RenderletNexus nexus, OWLNamedIndividual trait, OWLOntology ontology) {
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
                if (typeClass.getIRI().equals(IRI.create(OntologyConstants.SNP_CLASS_IRI))) {
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
        }


        return colour;
    }
}



