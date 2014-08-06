package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.layout.ColourMapper;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.utils.OntologyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A renderlet that is capable of rendering visualisations of "traits".  A trait is an OWL class from the experimental
 * factor ontology (EFO).  This renderlet retrieves the color encoding for each type of trait from the {@link
 * uk.ac.ebi.fgpt.goci.pussycat.layout.ColourMapper} class
 *
 * @author Dani Welter
 * @date 06/03/12
 */
@ServiceProvider
public class TraitRenderlet implements Renderlet<OWLReasoner, OWLNamedIndividual> {
    private Map<IRI, String> snpToBandNameMap = new HashMap<IRI, String>();
    private Map<IRI, String> traitClassToLabelMap = new HashMap<IRI, String>();
    private Map<IRI, String> traitClassToColorMap = new HashMap<IRI, String>();

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
        if (renderingContext instanceof OWLReasoner) {
            OWLReasoner reasoner = (OWLReasoner) renderingContext;
            if (renderingEntity instanceof OWLNamedIndividual) {
                OWLNamedIndividual individual = (OWLNamedIndividual) renderingEntity;
                if (nexus.getLocationOfRenderedEntity(individual) == null) {
                    Set<OWLClass> allTypes = reasoner.getTypes(individual, false).getFlattened();
                    IRI efIRI = IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI);
                    for (OWLClass typeClass : allTypes) {
                        if (typeClass.getIRI().equals(efIRI)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void render(RenderletNexus nexus, OWLReasoner reasoner, OWLNamedIndividual trait) {
        OWLOntology ontology = reasoner.getRootOntology();
        OWLNamedIndividual association = getAssociationForTrait(reasoner, trait);
        Set<OWLNamedIndividual> allTraits = getAllTraitsForAssociation(reasoner, association);

        if (association != null) {
            SVGArea associationLocation = nexus.getLocationOfRenderedEntity(association);

            String bandName = getAssociationBandName(ontology, association);

//            Element assocG = nexus.getRenderingEvent(association).getRenderedSVG();

//            String location = assocG.getAttribute("transform");

            if (bandName != null) {
                StringBuilder svg = new StringBuilder();
                svg.append("<circle ");

//                trait = builder.createElement("circle");
//                BandInformation band = nexus.getBandLocations().get(bandName);

//                trait.setAttribute("transform", location);

                // retrieve location of last rendered trait in the same association
                List<SVGArea> locations = getSortedLocationsForTraits(nexus, allTraits);
                SVGArea lastLocation = locations.get(locations.size() - 1);

                svg.append("transform='").append(associationLocation.getTransform()).append("' ");

                double alength = associationLocation.getWidth();
                double radius = 0.2 * alength;
                double ax = associationLocation.getX();
                double ay = associationLocation.getY();
                double displacement = associationLocation.getHeight();
                double cx, cy;
                int size = locations.size();

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

                svg.append("cx='").append(Double.toString(cx)).append("' ");
                svg.append("cy='").append(Double.toString(cy)).append("' ");
                svg.append("r='").append(Double.toString(radius)).append("' ");

                String colour = getColour(reasoner, trait);

                svg.append("fill='").append(colour).append("' ");
                svg.append("stroke='black' ");
                svg.append("stroke-width='0.5' ");

                String traitName = getTraitLabel(reasoner, trait);
                svg.append("gwasname='").append(traitName).append("' ");

                IRI iri = getTraitClassIRI(ontology, trait);
                String traitClass = OntologyUtils.getShortForm(iri, ontology);
                getLog().trace("Setting CSS class for trait '" + trait + "' to " + traitClass);
                svg.append("class='gwas-trait ").append(traitClass).append("'");
                svg.append("fading='false' ");

                String assocIRI = OntologyUtils.getShortForm(association);
                getLog().trace("Setting gwasassociation attribute for trait '" + trait + "' to " + assocIRI);
                svg.append("gwasassociation='").append(assocIRI).append("' ");
                svg.append("/>");

                SVGArea currentArea = new SVGArea(cx, cy, 2 * radius, 2 * radius, 0);
                RenderingEvent<OWLIndividual> event =
                        new RenderingEvent<OWLIndividual>(trait, svg.toString(), currentArea, this);
                nexus.renderingEventOccurred(event);
            }
            else {
                getLog().error("Cannot render trait '" + trait + "' - " +
                                       "unable to identify the band for association '" + association + "'");
            }
        }
        else {
            getLog().error("Cannot render trait '" + trait + "' - unable to locate any trait association about it");
        }
    }

    /**
     * Returns the trait association individual that the given trait "has_about".  If there is no known association,
     * this method returns null.
     *
     * @param reasoner the reasoner
     * @param trait    the trait individual to find the corresponding trait association for
     * @return the trait association that is_about this trait
     */
    private OWLNamedIndividual getAssociationForTrait(OWLReasoner reasoner, OWLNamedIndividual trait) {
        OWLDataFactory dataFactory = reasoner.getRootOntology().getOWLOntologyManager().getOWLDataFactory();
        OWLObjectProperty has_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.HAS_ABOUT_IRI));
        if (reasoner.getObjectPropertyValues(trait, has_about).getFlattened().size() != 0) {
            return reasoner.getObjectPropertyValues(trait, has_about).getFlattened().iterator().next();
        }
        else {
            getLog().warn("Trait " + trait + " has no association");
            return null;
        }
    }

    private Set<OWLNamedIndividual> getAllTraitsForAssociation(OWLReasoner reasoner, OWLNamedIndividual association) {
        OWLDataFactory dataFactory = reasoner.getRootOntology().getOWLOntologyManager().getOWLDataFactory();
        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        return reasoner.getObjectPropertyValues(association, is_about).getFlattened();
    }

    private List<SVGArea> getSortedLocationsForTraits(RenderletNexus nexus, Set<OWLNamedIndividual> traits) {
        List<SVGArea> locations = new ArrayList<SVGArea>();

        // fetch the location of all traits that have been rendered so far
        for (OWLNamedIndividual trait : traits) {
            SVGArea location = nexus.getLocationOfRenderedEntity(trait);
            if (location != null) {
                locations.add(location);
            }
        }

        // now sort
        Collections.sort(locations, new Comparator<SVGArea>() {
            @Override public int compare(SVGArea a1, SVGArea a2) {
                Double comp;
                double dY = a2.getY() - a1.getY();
                if (dY == 0) {
                    double dX = a2.getX() - a1.getX();
                    comp = dX > 0 ? Math.ceil(dX) : Math.floor(dX);
                }
                else {
                    comp = dY > 0 ? Math.ceil(dY) : Math.floor(dY);
                }
                return comp.intValue();
            }
        });

        return locations;
    }

    /**
     * Returns the IRI of the asserted type of the supplied individual.  If the given individual has exactly one
     * asserted type that is a subclass of EFO_0000001 (Experimental Factor), the IRI of that type will be returned.  If
     * there are zero types that fit this definition, this method will log an error and return null.  If there are more
     * than one asserted types fitting this definition, only the first encountered type is returned.
     *
     * @param ontology   the ontology in which to lookup the asserted types
     * @param individual the individual to acquire the type of
     * @return the IRI of the class for which the individual is an asserted type
     */
    private IRI getTraitClassIRI(OWLOntology ontology, OWLNamedIndividual individual) {
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

    private String getTraitLabel(OWLReasoner reasoner, OWLNamedIndividual individual) {
        OWLOntology ontology = reasoner.getRootOntology();
        IRI traitClass = getTraitClassIRI(ontology, individual);
        if (!traitClassToLabelMap.containsKey(traitClass)) {
            initTraitClassToLabelMap(ontology);
        }
        return traitClassToLabelMap.get(traitClass);
    }

    private String getAssociationBandName(OWLOntology ontology, OWLNamedIndividual association) {
        OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();

        // get all the is_about individuals of this trait-assocation
        OWLObjectProperty is_about = factory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        Set<OWLIndividual> allRelated = association.getObjectPropertyValues(is_about, ontology);
        if (allRelated.size() > 0) {
            for (OWLIndividual related : allRelated) {
                Set<OWLClassExpression> allTypes = related.getTypes(ontology);

                // find the association that is of type SNP
                for (OWLClassExpression typeClassExpression : allTypes) {
                    OWLClass typeClass = typeClassExpression.asOWLClass();
                    if (IRI.create(OntologyConstants.SNP_CLASS_IRI).equals(typeClass.getIRI())) {
                        OWLNamedIndividual snp = related.asOWLNamedIndividual();
                        if (!snpToBandNameMap.containsKey(snp.getIRI())) {
                            //get the SNP's cytogenetic band
                            OWLObjectProperty
                                    has_band =
                                    factory.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));

                            if (snp.getObjectPropertyValues(has_band, ontology).size() > 0) {
                                OWLIndividual band = snp.getObjectPropertyValues(has_band, ontology).iterator().next();
                                //get the band's name
                                OWLDataProperty
                                        has_name =
                                        factory.getOWLDataProperty(IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));
                                String bandName =
                                        band.getDataPropertyValues(has_name, ontology).iterator().next().getLiteral();
                                snpToBandNameMap.put(snp.getIRI(), bandName);

                            }
                            else {
                                getLog().warn("SNP " + snp + " does not have a cytogentic band");
                            }
                        }
                        String band = snpToBandNameMap.get(snp.getIRI());
                        if (band == null) {
                            getLog().warn("Failed to locate the band for SNP '" + snp + "'");
                        }
                    }
                    // if we got to here without returning, no related individuals are typed as SNPs
                    getLog().warn("Unable to identify a SNP for association '" + association + "'");
                }
            }
        }
        else {
            getLog().warn("Nothing is related to association '" + association + "'");
        }
        return null;
    }

    private String getColour(OWLReasoner reasoner, OWLNamedIndividual trait) {
        String colour;

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

    public void initTraitClassToLabelMap(OWLOntology ontology) {
        Set<OWLClass> allClasses = ontology.getClassesInSignature();
        OWLAnnotationProperty label = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty(
                OWLRDFVocabulary.RDFS_LABEL.getIRI());
        for (OWLClass owlClass : allClasses) {
            IRI clsIri = owlClass.getIRI();
            String className = null;
            for (OWLAnnotation annotation : owlClass.getAnnotations(ontology, label)) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    className = val.getLiteral();
                }
                if (owlClass.getAnnotations(ontology, label).size() != 1) {
                    getLog().debug("More than one label for class " + className);
                }
            }

            if (className == null) {
                getLog().debug("Class without label " + owlClass);
            }
            else {
                traitClassToLabelMap.put(clsIri, className);
            }
        }
    }
}



