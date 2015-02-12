package uk.ac.ebi.fgpt.goci.dao;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.springframework.core.io.Resource;
import uk.ac.ebi.fgpt.goci.exception.OntologyIndexingException;
import uk.ac.ebi.fgpt.goci.lang.Initializable;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Retrieves classes from an ontology file in OWL or OBO formats.  This DAO uses the OWLAPI to load and handle the
 * ontology, and operates over the raw, unclassified version of the ontology.
 *
 * @author Tony Burdett Date 24/01/12
 */
public class OntologyDAO extends Initializable {
    // configurable ontology elements with sensible defaults
    private Resource ontologyResource;

//    private String ontologyURI = "file:///Users/catherineleroy/Documents/github_project/ExperimentalFactorOntology/ExFactorInOWL/releasecandidate/my_efo_merged.owl";
    private String ontologyURI = "http://www.ebi.ac.uk/efo";
    private String ontologySynonymAnnotationURI = "http://www.ebi.ac.uk/efo/alternative_term";
    private String ontologyObsoleteClassURI = "http://www.geneontology.org/formats/oboInOwl#ObsoleteClass";

    private OWLOntology ontology;
    private OWLClass obsoleteClass;

    private Map<String, Set<OWLClass>> labelToClassMap;
    private Map<OWLClass, List<String>> classToLabelMap;
    private Map<IRI, OWLClass> iriToClassMap;
    private Map<String, IRI> accessionToIRIMap;
    private OntologyConfiguration ontologyConfiguration;

    public Resource getOntologyResource() {
        return ontologyResource;
    }

    public void setOntologyResource(Resource ontologyResource) {
        this.ontologyResource = ontologyResource;
    }

    public String getOntologyURI() {
        return ontologyURI;
    }

    public void setOntologyURI(String ontologyURI) {
        this.ontologyURI = ontologyURI;
    }

    public String getOntologySynonymAnnotationURI() {
        return ontologySynonymAnnotationURI;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntologySynonymAnnotationURI(String ontologySynonymAnnotationURI) {
        this.ontologySynonymAnnotationURI = ontologySynonymAnnotationURI;
    }

    public String getOntologyObsoleteClassURI() {
        return ontologyObsoleteClassURI;
    }

    public void setOntologyObsoleteClassURI(String ontologyObsoleteClassURI) {
        this.ontologyObsoleteClassURI = ontologyObsoleteClassURI;
    }

    public OntologyConfiguration getOntologyConfiguration() {
        return ontologyConfiguration;
    }

    public void setOntologyConfiguration(OntologyConfiguration ontologyConfiguration) {
        this.ontologyConfiguration = ontologyConfiguration;
    }

    protected void doInitialization() throws OWLOntologyCreationException {
        try {
            // set property to make sure we can parse all of the ontology
            System.setProperty("entityExpansionLimit", "100000000");

            OWLOntologyManager manager;
            if (getOntologyConfiguration() != null) {
                manager = getOntologyConfiguration().getOWLOntologyManager();
//                ontology = manager.getOntology(IRI.create("http://www.ebi.ac.uk/efo"));
                ontology = manager.getOntology(IRI.create(getOntologyURI()));
                if (ontology == null) {
                    getLog().info("Loading Ontology '" + getOntologyURI() + "'...");
                    ontology = manager.loadOntologyFromOntologyDocument(IRI.create(getOntologyURI()));
                    getLog().info("Loaded " + ontology.getOntologyID().getOntologyIRI() + " ok");
                }
                else {
                    getLog().info("Ontology '" + getOntologyURI() + "' is already loaded");
                }
            }
            else {
                manager = OWLManager.createOWLOntologyManager();
                if (getOntologyResource() != null) {
                    getLog().info("Loading Ontology from " + getOntologyResource().getURI().toString() + "...");
                    try {
                        ontology = manager.loadOntologyFromOntologyDocument(getOntologyResource().getInputStream());
                    }
                    catch (OWLOntologyAlreadyExistsException e) {
                        getLog().info("Ontology '" + e.getOntologyID().getOntologyIRI()
                                              + "' is already loaded");
                        ontology = manager.getOntology(e.getOntologyID().getOntologyIRI());
                    }
                }
                else {
                    ontology = manager.getOntology(IRI.create(getOntologyURI()));
                    if (ontology == null) {
                        getLog().info("Loading Ontology '" + getOntologyURI() + "'...");
                        ontology = manager.loadOntologyFromOntologyDocument(IRI.create(getOntologyURI()));
                        getLog().info("Loaded " + ontology.getOntologyID().getOntologyIRI() + " ok");
                    }
                    else {
                        getLog().info("Ontology '" + getOntologyURI() + "' is already loaded");
                    }
                }
            }

            getLog().info("Creating indexes for " + ontology.getOntologyID().getOntologyIRI() + "...");
            labelToClassMap = new HashMap<String, Set<OWLClass>>();
            classToLabelMap = new HashMap<OWLClass, List<String>>();
            iriToClassMap = new HashMap<IRI, OWLClass>();
            accessionToIRIMap = new HashMap<String, IRI>();

            // get obsolete class
            for (OWLClass nextClass : ontology.getClassesInSignature()) {
                if (nextClass.getIRI().toURI().toString().equals(getOntologyObsoleteClassURI())) {
                    obsoleteClass = nextClass;
                    break;
                }
            }
            if (obsoleteClass == null) {
                getLog().warn("ObsoleteClass was not found: obsoleted classes may appear in search results");
            }

            // loop over classes
            for (OWLClass owlClass : ontology.getClassesInSignature()) {
                // check this isn't an obsolete class
                if (!isObsolete(ontology, obsoleteClass, owlClass)) {
                    // get class names, and enter them in the maps
                    List<String> classNames = getClassNames(ontology, owlClass);

                    classToLabelMap.put(owlClass, classNames);
                    for (String name : getClassNames(ontology, owlClass)) {
                        name = normalizeSearchString(name);
                        if (!labelToClassMap.containsKey(name)) {
                            labelToClassMap.put(name, new HashSet<OWLClass>());
                        }
                        labelToClassMap.get(name).add(owlClass);
                    }

                    // get IRI, and enter it into the map
                    iriToClassMap.put(owlClass.getIRI(), owlClass);
                }
            }
            getLog().info("...ontology indexing complete");
        }
        catch (IOException e) {
            throw new OWLOntologyCreationException(
                    "Unable to load ontology from supplied resource '" + getOntologyResource().toString() + "'", e);
        }
    }


    /**
     * Fetches terms that can be matched against EFO.  By default, this excludes anything that is based on units (i.e.
     * falls within the set of things that might be a member of the class UO:0000000).  Searches are performed by exact
     * text matching on term label or synonyms.
     *
     * @param label the label of the OWL class to search for
     * @return the collection of ontology terms retrieved
     */
    public synchronized Collection<OWLClass> getOWLClassesByLabel(String label) {
        try {
            waitUntilReady();
            String searchString = normalizeSearchString(label);
            return matchSearchString(searchString);
        }
        catch (InterruptedException e) {
            throw new OntologyIndexingException(
                    "Unexpectedly interrupted whilst waiting for indexing to complete", e);
        }
    }

    /**
     * Fetches an owl class by the URI of that class.  The string passed to this method should be a properly formatted
     * string representation of the URI for this class.
     *
     * @param str the URI of the class
     * @return the OWL class with this URI, if found, or null if not present
     */
    public OWLClass getOWLClassByURI(String str) {
        return getOWLClassByURI(URI.create(str));
    }

    /**
     * Fetches an owl class by URI.  Internally, this method converts the given URI to an IRI - this method is a
     * convenience method that is shorthand for <code>getOWLClassByIRI(IRI.create(uri));</code>
     *
     * @param uri the URI of the class
     * @return the OWL class with this URI, if found, or null if not present
     */
    public OWLClass getOWLClassByURI(URI uri) {
        return getOWLClassByIRI(IRI.create(uri));
    }

    /**
     * Searches the index of owl class looking for a class with an IRI matching the one supplied.  This method will
     * return null if the supplied IRI does not match that of any class in the loaded ontology.
     *
     * @param iri the IRI of the class to retrieve
     * @return the OWL class with this IRI, if found, or null if absent
     */
    public OWLClass getOWLClassByIRI(IRI iri) {
        try {
            waitUntilReady();
            if (iriToClassMap.containsKey(iri)) {
                return iriToClassMap.get(iri);
            }
            else {
                throw new IllegalArgumentException("There is no OWLClass with IRI '" + iri.toString() + "' " +
                                                           "in the ontology '" + getOntologyURI() + "'");
            }
        }
        catch (InterruptedException e) {
            throw new OntologyIndexingException(
                    "Unexpectedly interrupted whilst waiting for indexing to complete", e);
        }
    }

    /**
     * Retrieve all possible names for the supplied class.  Class "names" include any labels attached to this class, or
     * any synonyms supplied.
     *
     * @param owlClass the owl class to derive names for
     * @return the list of strings representing labels or synonyms for this class
     */
    public synchronized List<String> getClassNames(OWLClass owlClass) {
        try {
            waitUntilReady();
            return getClassNames(ontology, owlClass);
        }
        catch (InterruptedException e) {
            throw new OntologyIndexingException(
                    "Unexpectedly interrupted whilst waiting for indexing to complete", e);
        }
    }

    /**
     * Recovers all string values of the rdfs:label annotation attribute on the supplied class.  This is computed over
     * the inferred hierarchy, so labels of any equivalent classes will also be returned.
     *
     * @param owlClass the class to recover labels for
     * @return the literal values of the rdfs:label annotation
     */
    public synchronized Set<String> getClassRDFSLabels(OWLClass owlClass) {
        try {
            waitUntilReady();
            return getClassRDFSLabels(ontology, owlClass);
        }
        catch (InterruptedException e) {
            throw new OntologyIndexingException(
                    "Unexpectedly interrupted whilst waiting for indexing to complete", e);
        }
    }

    /**
     * Recovers all synonyms for the supplied owl class, based on the literal value of the efo synonym annotation.  The
     * actual URI for this annotation is recovered from zooma-uris.properties, but at the time of writing was
     * 'http://www.ebi.ac.uk/efo/alternative_term'.  This class uses the
     *
     * @param owlClass the class to retrieve the synonyms of
     * @return a set of strings containing all aliases of the supplied class
     */
    public synchronized Set<String> getClassSynonyms(OWLClass owlClass) {
        try {
            waitUntilReady();
            return getClassSynonyms(ontology, owlClass);
        }
        catch (InterruptedException e) {
            throw new OntologyIndexingException(
                    "Unexpectedly interrupted whilst waiting for indexing to complete", e);
        }
    }

    /**
     * "Normalizes" a string into a searchable string.  This implementation is fairly basic, it simply involves
     * lowercasing the entire string, and stripping trailing and internal whitespace.  You can extend this to provide
     * more advanced normalization (for example, converting between English and American spellings).
     *
     * @param string the string to normalize
     * @return the searchable string
     */
    private String normalizeSearchString(String string) {
        // remove any trailing whitespace
        string = string.trim();
        // remove internal whitespace
        string = string.replaceAll("\\s", "");
        // lowercase everything
        string = string.toLowerCase();

        return string;
    }

    /**
     * Searches the classes known to this retriever for any that may match the given search string.  This implementation
     * uses very simple logic, simply looking for exact matches in class labels and synonyms.  Other implementions, with
     * more advanced searching, can obviously be implemented.
     *
     * @param searchString the search string - this will usually be the class name/synonym
     * @return a set of matching classes.
     */
    private Set<OWLClass> matchSearchString(String searchString) {
        try {
            waitUntilReady();
            if (labelToClassMap.containsKey(searchString)) {
                return labelToClassMap.get(searchString);
            }
            else {
                return Collections.emptySet();
            }
        }
        catch (InterruptedException e) {
            throw new OntologyIndexingException(
                    "Unexpectedly interrupted whilst waiting for indexing to complete", e);
        }
    }

    /**
     * Retrieve all possible names for the supplied class.  Class "names" include any labels attached to this class, or
     * any synonyms supplied.
     *
     * @param owlOntology the ontology to search
     * @param owlClass    the owl class to derive names for
     * @return the list of strings representing labels or synonyms for this class
     */
    private List<String> getClassNames(OWLOntology owlOntology, OWLClass owlClass) {
        List<String> results = new ArrayList<String>();
        results.addAll(0, getClassRDFSLabels(owlOntology, owlClass));
        results.addAll(getClassSynonyms(owlOntology, owlClass));

        return results;
    }

    /**
     * Recovers all string values of the rdfs:label annotation attribute on the supplied class.  This is computed over
     * the inferred hierarchy, so labels of any equivalent classes will also be returned.
     *
     * @param owlOntology the ontology to search
     * @param owlClass    the class to recover labels for
     * @return the literal values of the rdfs:label annotation
     */
    private Set<String> getClassRDFSLabels(OWLOntology owlOntology, OWLClass owlClass) {
        Set<String> classNames = new HashSet<String>();

        // get label annotation property
        OWLAnnotationProperty labelAnnotationProperty =
                owlOntology.getOWLOntologyManager().getOWLDataFactory()
                        .getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        // get all label annotations
        Set<OWLAnnotation> labelAnnotations = owlClass.getAnnotations(
                owlOntology, labelAnnotationProperty);

        for (OWLAnnotation labelAnnotation : labelAnnotations) {
            OWLAnnotationValue labelAnnotationValue = labelAnnotation.getValue();
            if (labelAnnotationValue instanceof OWLLiteral) {
                classNames.add(((OWLLiteral) labelAnnotationValue).getLiteral());
            }
        }
        return classNames;
    }

    /**
     * Recovers all synonyms for the supplied owl class, based on the literal value of the efo synonym annotation.  The
     * actual URI for this annotation is recovered from zooma-uris.properties, but at the time of writing was
     * 'http://www.ebi.ac.uk/efo/alternative_term'.  This class uses the
     *
     * @param owlOntology the ontology to search
     * @param owlClass    the class to retrieve the synonyms of
     * @return a set of strings containing all aliases of the supplied class
     */
    private Set<String> getClassSynonyms(OWLOntology owlOntology, OWLClass owlClass) {
        Set<String> classSynonyms = new HashSet<String>();

        // get synonym annotation property
        OWLAnnotationProperty synonymAnnotationProperty =
                owlOntology.getOWLOntologyManager().getOWLDataFactory()
                        .getOWLAnnotationProperty(IRI.create(getOntologySynonymAnnotationURI()));

        // get all synonym annotations
        Set<OWLAnnotation> synonymAnnotations = owlClass.getAnnotations(
                owlOntology, synonymAnnotationProperty);

        for (OWLAnnotation synonymAnnotation : synonymAnnotations) {
            OWLAnnotationValue synonymAnnotationValue = synonymAnnotation.getValue();
            if (synonymAnnotationValue instanceof OWLLiteral) {
                classSynonyms.add(((OWLLiteral) synonymAnnotationValue).getLiteral());
            }
        }

        return classSynonyms;
    }

    /**
     * Returns true if this ontology term is obsolete in EFO, false otherwise.  In EFO, a term is defined to be obsolete
     * if and only if it is a subclass of ObsoleteTerm.
     *
     * @param owlOntology   the ontology to search
     * @param obsoleteClass the owlClass that represents the "obsolete" superclass
     * @param owlClass      the owlClass to check for obsolesence
     * @return true if obsoleted, false otherwise
     */
    private boolean isObsolete(OWLOntology owlOntology, OWLClass obsoleteClass, OWLClass owlClass) {
        if (obsoleteClass != null) {
            Set<OWLClassExpression> superclasses = owlClass.getSuperClasses(owlOntology);
            for (OWLClassExpression oce : superclasses) {
                if (!oce.isAnonymous() &&
                        oce.asOWLClass().getIRI().toURI().equals(obsoleteClass.getIRI().toURI())) {
                    return true;
                }
            }
        }
        // if no superclasses are obsolete, or if the obsolete class wasn't found, this class isn't obsolete
        return false;
    }
}