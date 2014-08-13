package uk.ac.ebi.fgpt.goci.pussycat.layout;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A general utils class that contains methods for discovering the relationships between concepts that are necessary to
 * determine how to layout the diagram
 *
 * @author Tony Burdett
 * @date 13/08/14
 */
public class LayoutUtils {
    private static final LayoutUtils instance = new LayoutUtils();

    public static LayoutUtils getCachingInstance() {
        return instance;
    }

    private Map<List<Object>, Object> requestCache;
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    private LayoutUtils() {
        this.requestCache = new HashMap<List<Object>, Object>();
    }

    public OWLNamedIndividual getCytogeneticBandForAssociation(OWLReasoner reasoner, OWLNamedIndividual association) {
        Object retrieved = checkCache("getCytogeneticBandForAssociation", reasoner, association);
        if (retrieved != null) {
            return (OWLNamedIndividual) retrieved;
        }

        Set<OWLNamedIndividual> results = new HashSet<OWLNamedIndividual>();
        OWLNamedIndividual result;

        OWLOntology ontology = reasoner.getRootOntology();
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLObjectProperty has_about = factory.getOWLObjectProperty(IRI.create(OntologyConstants.HAS_ABOUT_IRI));
        OWLObjectProperty location_of =
                factory.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATION_OF_PROPERTY_IRI));
        OWLClass snpCls = factory.getOWLClass(IRI.create(OntologyConstants.SNP_CLASS_IRI));
        OWLClass bandCls = factory.getOWLClass(IRI.create(OntologyConstants.CYTOGENIC_REGION_CLASS_IRI));

        // get all the is_about SNPs of this trait-assocation
        OWLObjectHasValue hasAboutAssociation = factory.getOWLObjectHasValue(has_about, association);
        OWLObjectIntersectionOf associatedSNP = factory.getOWLObjectIntersectionOf(hasAboutAssociation, snpCls);
        Set<OWLNamedIndividual> snps = reasoner.getInstances(associatedSNP, false).getFlattened();

        // now, for each SNP, get the location
        for (OWLNamedIndividual snp : snps) {
            // get all the located_in bands of this snp
            OWLObjectHasValue locationOfSNP = factory.getOWLObjectHasValue(location_of, snp);
            OWLObjectIntersectionOf locationBand = factory.getOWLObjectIntersectionOf(locationOfSNP, bandCls);
            Set<OWLNamedIndividual> locations = reasoner.getInstances(locationBand, false).getFlattened();
            results.addAll(locations);
        }

        if (results.size() == 0) {
            result = null;
        }
        else {
            if (results.size() > 1) {
                getLog().error("More than one cytogenetic band for association '" + association +
                                       "'; will only use the first");
            }
            result = results.iterator().next();
        }

        return cache(result, "getCytogeneticBandForAssociation", reasoner, association);
    }

    public Set<OWLNamedIndividual> getTraitsLocatedInCytogeneticBand(OWLReasoner reasoner,
                                                                     OWLNamedIndividual cytogeneticBand) {
        Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", reasoner, cytogeneticBand);
        if (retrieved != null) {
            return (Set<OWLNamedIndividual>) retrieved;
        }

        Set<OWLNamedIndividual> results = new HashSet<OWLNamedIndividual>();

        OWLOntology ontology = reasoner.getRootOntology();
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLObjectProperty is_about = factory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        OWLObjectProperty located_in =
                factory.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));
        OWLClass snpCls = factory.getOWLClass(IRI.create(OntologyConstants.SNP_CLASS_IRI));
        OWLClass associationCls = factory.getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));

        // get all the located_in SNPs of this band
        OWLObjectHasValue snpsLocated = factory.getOWLObjectHasValue(located_in, cytogeneticBand);
        OWLObjectIntersectionOf associatedSNP = factory.getOWLObjectIntersectionOf(snpsLocated, snpCls);
        Set<OWLNamedIndividual> snps = reasoner.getInstances(associatedSNP, false).getFlattened();

        // now, for each SNP, get the associations
        for (OWLNamedIndividual snp : snps) {
            // get all the located_in bands of this snp
            OWLObjectHasValue snpAbouts = factory.getOWLObjectHasValue(is_about, snp);
            OWLObjectIntersectionOf snpAssociations = factory.getOWLObjectIntersectionOf(snpAbouts, associationCls);
            Set<OWLNamedIndividual> associations = reasoner.getInstances(snpAssociations, false).getFlattened();
            results.addAll(associations);
        }

        return cache(results, "getTraitsLocatedInCytogeneticBand", reasoner, cytogeneticBand);
    }

    /**
     * Returns the trait association individual that the given trait "has_about".  If there is no known association,
     * this method returns null.
     *
     * @param reasoner the reasoner
     * @param trait    the trait individual to find the corresponding trait association for
     * @return the trait association that is_about this trait
     */
    public OWLNamedIndividual getAssociationForTrait(OWLReasoner reasoner, OWLNamedIndividual trait) {
        Object retrieved = checkCache("getAssociationForTrait", reasoner, trait);
        if (retrieved != null) {
            return (OWLNamedIndividual) retrieved;
        }

        OWLNamedIndividual result;

        OWLDataFactory dataFactory = reasoner.getRootOntology().getOWLOntologyManager().getOWLDataFactory();
        OWLObjectProperty has_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.HAS_ABOUT_IRI));
        if (reasoner.getObjectPropertyValues(trait, has_about).getFlattened().size() != 0) {
            result = reasoner.getObjectPropertyValues(trait, has_about).getFlattened().iterator().next();
        }
        else {
            getLog().warn("Trait " + trait + " has no association");
            result = null;
        }

        return cache(result, "getAssociationForTrait", reasoner, trait);
    }

    public BandInformation getBandInformation(OWLReasoner reasoner, OWLNamedIndividual bandIndividual) {
        Object retrieved = checkCache("getBandInformation", reasoner, bandIndividual);
        if (retrieved != null) {
            return (BandInformation) retrieved;
        }

        BandInformation result;

        OWLOntology ontology = reasoner.getRootOntology();
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        // get the band's name
        String bandName;
        OWLDataProperty has_name =
                factory.getOWLDataProperty(IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));
        Set<OWLLiteral> bandNames = bandIndividual.getDataPropertyValues(has_name, ontology);
        if (bandNames.size() == 1) {
            bandName = bandNames.iterator().next().getLiteral();
            result = new BandInformation(bandName);
        }
        else {
            throw new RuntimeException(
                    "Band OWLIndividual '" + bandIndividual + "' has more than one band name");
        }

        return cache(result, "getBandInformation", reasoner, bandIndividual);
    }

    private Object checkCache(String methodName, Object... arguments) {
        List<Object> key = new ArrayList<Object>();
        key.add(methodName);
        Collections.addAll(key, arguments);
        if (requestCache.containsKey(key)) {
            return requestCache.get(key);
        }
        else {
            return null;
        }
    }

    private <O> O cache(O result, String methodName, Object... arguments) {
        List<Object> key = new ArrayList<Object>();
        key.add(methodName);
        Collections.addAll(key, arguments);
        requestCache.put(key, result);
        return result;
    }
}
