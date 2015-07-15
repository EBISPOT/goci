package uk.ac.ebi.spot.goci.pussycat.service;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.OntologyIndexingException;
import uk.ac.ebi.spot.goci.ontology.owl.ReasonedOntologyLoader;

import javax.validation.constraints.NotNull;
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
 * Created by dwelter on 30/06/15.
 */


@Service
public class OntologyService {

    @NotNull
    @Value("${efo.location}")
    private Resource efoResource;


    private ReasonedOntologyLoader ontologyLoader;

    private Map<String, Set<OWLClass>> labelToClassMap;
    private Map<OWLClass, List<String>> classToLabelMap;
    private Map<IRI, OWLClass> iriToClassMap;
    private OWLClass obsoleteClass;


    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public OntologyService(ReasonedOntologyLoader ontologyLoader){
//    public OntologyService(){
//        this.ontologyLoader = new ReasonedOntologyLoader();
        this.ontologyLoader = ontologyLoader;
        ontologyLoader.setOntologyName("efo");
        ontologyLoader.setOntologyURI(URI.create("http://www.ebi.ac.uk/efo"));
        ontologyLoader.setOntologyResource(efoResource);
        ontologyLoader.setExclusionClassURI(URI.create("http://www.geneontology.org/formats/oboInOwl#ObsoleteClass"));
        ontologyLoader.setExclusionAnnotationURI(URI.create("http://www.ebi.ac.uk/efo/organizational_class"));
        ontologyLoader.setSynonymURIs(Collections.singleton(URI.create("http://www.ebi.ac.uk/efo/alternative_term")));
        System.out.println("Initialising the loader");
        ontologyLoader.init();
        System.out.println("Loader ready");
        setObsoleteClass();
        System.out.println("Obsolete class set... ");
        System.out.println("... and it is " + obsoleteClass.getIRI().toString());
        this.labelToClassMap = new HashMap<String, Set<OWLClass>>();
        this.classToLabelMap = new HashMap<OWLClass, List<String>>();
        this.iriToClassMap = new HashMap<IRI, OWLClass>();
        populateClassMaps();
        System.out.println("All done");

    }

    private void populateClassMaps(){
        System.out.println("About to build the class maps");

        OWLOntology ontology = ontologyLoader.getOntology();
        for (OWLClass owlClass : ontology.getClassesInSignature()) {
            // check this isn't an obsolete class
            if (!isObsolete(ontology, obsoleteClass, owlClass)) {
                // get class names, and enter them in the maps
                List<String> classNames = getClassNames(owlClass);

                classToLabelMap.put(owlClass, classNames);
                for (String name : getClassNames(owlClass)) {
                    if(name != null) {
                        name = normalizeSearchString(name);
                        if (!labelToClassMap.containsKey(name)) {
                            labelToClassMap.put(name, new HashSet<OWLClass>());
                        }
                        labelToClassMap.get(name).add(owlClass);
                    }
                    else {
                        System.out.println("Class " + owlClass.getIRI().toString() + " doesn't have a label");
                    }
                }

                // get IRI, and enter it into the map
                iriToClassMap.put(owlClass.getIRI(), owlClass);
            }
        }
    }

    private void setObsoleteClass(){
        for (OWLClass nextClass : ontologyLoader.getOntology().getClassesInSignature()) {
            if (nextClass.getIRI().toURI().toString().equals(ontologyLoader.getExclusionClassURI().toString())) {
                obsoleteClass = nextClass;
                break;
            }
        }
    }

    public ReasonedOntologyLoader getOntologyLoader() {
        return ontologyLoader;
    }


    /**
     * Fetches an owl class by the URI of that class.  The string passed to this method should be a properly formatted
     * string representation of the URI for this class.
     *
     * @param label the URI of the class
     * @return the OWL class with this URI, if found, or null if not present
     */
    public synchronized Collection<OWLClass> getOWLClassesByLabel(String label) {
        try {
            ontologyLoader.waitUntilReady();
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
            if (iriToClassMap.containsKey(iri)) {
                return iriToClassMap.get(iri);
            }
            else {
                throw new IllegalArgumentException("There is no OWLClass with IRI '" + iri.toString() + "' " +
                                                           "in the ontology '" + getOntologyLoader().getOntologyURI() + "'");
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

        List<String> results = new ArrayList<String>();
        results.add(getOntologyLoader().getLabel(owlClass.getIRI()));
        results.addAll(getOntologyLoader().getChildLabels(owlClass.getIRI()));
        return results;
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
            ontologyLoader.waitUntilReady();
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
                    System.out.println(owlClass.getIRI().toString() + " is a child of obsolete class");
                    return true;
                }
            }
        }
        // if no superclasses are obsolete, or if the obsolete class wasn't found, this class isn't obsolete
        return false;
    }



}


