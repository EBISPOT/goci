package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.ColourMapper;
import uk.ac.ebi.fgpt.goci.pussycat.layout.LayoutUtils;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.utils.OntologyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    private Map<IRI, String> traitClassToLabelMap;

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
        getLog().trace("Trait: " + trait);
        try {
            // collect all the bands for which we need to render this trait, and list all associations in that band
            Map<OWLNamedIndividual, Set<OWLNamedIndividual>> bandToAssociationMap =
                    new HashMap<OWLNamedIndividual, Set<OWLNamedIndividual>>();

            for (OWLNamedIndividual association : getAssociationsForTrait(reasoner, trait)) {
                try {
                    // get the band for this association
                    OWLNamedIndividual band = getBandForAssociation(reasoner, association);
                    if (!bandToAssociationMap.containsKey(band)) {
                        bandToAssociationMap.put(band, new HashSet<OWLNamedIndividual>());
                    }
                    bandToAssociationMap.get(band).add(association);
                }
                catch (DataIntegrityViolationException e) {
                    getLog().error("Unable to render trait '" + trait + "' for association '" + association + "'", e);
                }
            }

            for (OWLNamedIndividual band : bandToAssociationMap.keySet()) {
                try {
                    // get the location of any traits previously rendered in this band
                    List<SVGArea> locations = getLocationsOfOtherTraitsinBand(nexus, reasoner, band);

                    StringBuilder svg = new StringBuilder();
                    svg.append("<circle ");

                    // also grab the location of an association (should all be the same for the same band)
                    OWLNamedIndividual association = bandToAssociationMap.get(band).iterator().next();
                    SVGArea associationLocation = nexus.getLocationOfRenderedEntity(association);
                    if (associationLocation.getTransform() != null) {
                        svg.append("transform='").append(associationLocation.getTransform()).append("' ");
                    }

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

                    String colour = getTraitColour(reasoner, trait);

                    svg.append("fill='").append(colour).append("' ");
                    svg.append("stroke='black' ");
                    svg.append("stroke-width='0.5' ");

                    String traitName = getTraitLabel(reasoner, trait);
                    svg.append("gwasname='").append(traitName).append("' ");

                    String traitAttribute = getTraitAttribute(reasoner, trait);
                    getLog().trace("Setting CSS class for trait '" + trait + "' to " + traitAttribute);
                    svg.append("class='gwas-trait ").append(traitAttribute).append("'");
                    svg.append("fading='false' ");

                    StringBuilder associationAttribute = new StringBuilder();
                    Iterator<OWLNamedIndividual> associationIt = bandToAssociationMap.get(band).iterator();
                    while (associationIt.hasNext()) {
                        associationAttribute.append(getTraitAssociationAttribute(reasoner, associationIt.next()));
                        if (associationIt.hasNext()) {
                            associationAttribute.append(",");
                        }
                    }
                    getLog().trace(
                            "Setting gwasassociation attribute for trait '" + trait + "' to " +
                            associationAttribute.toString());
                    svg.append("gwasassociation='").append(associationAttribute.toString()).append("' ");
                    svg.append("/>");

                    SVGArea currentArea = new SVGArea(cx, cy, 2 * radius, 2 * radius, 0);

                    // this area is a conjunction of trait + band, so store as a List<OWLNamedIndividual> with 2 elements
                    RenderingEvent<List<OWLNamedIndividual>> event =
                            new RenderingEvent<List<OWLNamedIndividual>>(Arrays.asList(trait, band),
                                                                         svg.toString(),
                                                                         currentArea,
                                                                         this);
                    nexus.renderingEventOccurred(event);
                }
                catch (DataIntegrityViolationException e) {
                    getLog().error("Cannot render trait '" + trait + "' in band '" + band + "'");
                }
            }
        }
        catch (DataIntegrityViolationException e) {
            getLog().error("Cannot render trait '" + trait + "'", e);
        }
    }

    protected Set<OWLNamedIndividual> getAssociationsForTrait(OWLReasoner reasoner, OWLNamedIndividual trait)
            throws DataIntegrityViolationException {
        return LayoutUtils.getCachingInstance().getAssociationsForTrait(reasoner, trait);
    }

    protected OWLNamedIndividual getBandForAssociation(OWLReasoner reasoner, OWLNamedIndividual association)
            throws DataIntegrityViolationException {
        OWLNamedIndividual bandIndividual =
                LayoutUtils.getCachingInstance().getCytogeneticBandForAssociation(reasoner, association);
        getLog().trace("Band for association '" + association + "' is '" + bandIndividual + "'");
        return bandIndividual;
    }

    protected List<SVGArea> getLocationsOfOtherTraitsinBand(RenderletNexus nexus,
                                                            OWLReasoner reasoner,
                                                            OWLNamedIndividual band)
            throws DataIntegrityViolationException {
        Set<OWLNamedIndividual> allTraits =
                LayoutUtils.getCachingInstance()
                        .getTraitsLocatedInCytogeneticBand(reasoner, band);
        getLog().trace("Identified " + allTraits.size() + " traits in band '" + band + "'");

        List<SVGArea> locations = new ArrayList<SVGArea>();

        // fetch the location of all trait + band pairs that have been rendered so far
        for (OWLNamedIndividual nextTrait : allTraits) {
            SVGArea location = nexus.getLocationOfRenderedEntity(Arrays.asList(nextTrait, band));
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

        getLog().trace("Sorted locations for " + allTraits.size() + " traits - " +
                       locations.size() + " have been rendered");

        return locations;
    }

    protected String getTraitAttribute(OWLReasoner reasoner, OWLNamedIndividual trait)
            throws DataIntegrityViolationException {
        // this gets the first, asserted, non-experimental factor class and then exits
        Set<OWLClass> allTypes = reasoner.getTypes(trait, true).getFlattened();
        if (allTypes.size() > 0) {
            for (OWLClass typeClass : allTypes) {
                if (!OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI.equals(typeClass.getIRI().toString())) {
                    return OntologyUtils.getShortForm(typeClass);
                }
            }
        }
        // if we got to here, we found no trait type
        throw new DataIntegrityViolationException("Could not identify any valid type (i.e. subclass of " +
                                                  "<" + OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI +
                                                  ">) for trait '" + trait + "'");
    }

    protected String getTraitAssociationAttribute(OWLReasoner reasoner, OWLNamedIndividual association)
            throws DataIntegrityViolationException {
        return OntologyUtils.getShortForm(association);
    }

    private String getTraitLabel(OWLReasoner reasoner, OWLNamedIndividual individual) {
        IRI traitClass = getTraitClassIRI(reasoner, individual);
        if (traitClassToLabelMap == null) {
            initTraitClassToLabelMap(reasoner);
        }
        return traitClassToLabelMap.get(traitClass);
    }

    private String getTraitColour(OWLReasoner reasoner, OWLNamedIndividual trait) {
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

    /**
     * Returns the IRI of the asserted type of the supplied individual.  If the given individual has exactly one
     * asserted type that is a subclass of EFO_0000001 (Experimental Factor), the IRI of that type will be returned.  If
     * there are zero types that fit this definition, this method will log an error and return null.  If there are more
     * than one asserted types fitting this definition, only the first encountered type is returned.
     *
     * @param reasoner   the reasoner in which to lookup the asserted types
     * @param individual the individual to acquire the type of
     * @return the IRI of the class for which the individual is an asserted type
     */
    private IRI getTraitClassIRI(OWLReasoner reasoner, OWLNamedIndividual individual) {
        IRI traitClass = null;

        // this gets the first direct inferred non-experimental factor class and then exits
        Set<OWLClassExpression> allTypes = individual.getTypes(reasoner.getRootOntology());
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

    private void initTraitClassToLabelMap(OWLReasoner reasoner) {
        traitClassToLabelMap = new HashMap<IRI, String>();

        Set<OWLOntology> ontologies = reasoner.getRootOntology().getImportsClosure();
        for (OWLOntology ontology : ontologies) {
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

                if (className != null) {
                    getLog().trace("Mapped '" + clsIri + "' -> " + className);
                    traitClassToLabelMap.put(clsIri, className);
                }
            }
        }
    }
}



