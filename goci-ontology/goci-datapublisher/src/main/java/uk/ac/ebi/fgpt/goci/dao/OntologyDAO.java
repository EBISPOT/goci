package uk.ac.ebi.fgpt.goci.dao;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import uk.ac.ebi.fgpt.goci.exception.OntologyIndexingException;
import uk.ac.ebi.fgpt.goci.lang.Initializable;

import java.net.URI;
import java.util.*;

/**
 * Retrieves classes from an ontology file in OWL or OBO formats.  This DAO uses the OWLAPI to load and handle the
 * ontology, and operates over the raw, unclassified version of the ontology.
 *
 * @author Tony Burdett
 * @date 24/01/12
 */
public class OntologyDAO extends Initializable {
    // configurable ontology elements with sensible defaults
    private String efoURI = "http://www.ebi.ac.uk/efo/efo.owl";
    private String efoSynonymAnnotationURI = "http://www.ebi.ac.uk/efo/alternative_term";
    private String efoObsoleteClassURI = "http://www.geneontology.org/formats/oboInOwl#ObsoleteClass";

    private OWLOntology efo;
    private OWLClass obsoleteClass;

    private Map<String, Set<OWLClass>> labelToClassMap;
    private Map<OWLClass, List<String>> classToLabelMap;
    private Map<IRI, OWLClass> iriToClassMap;
    private Map<String, IRI> accessionToIRIMap;

    public String getEfoURI() {
        return efoURI;
    }

    public void setEfoURI(String efoURI) {
        this.efoURI = efoURI;
    }

    public String getEfoSynonymAnnotationURI() {
        return efoSynonymAnnotationURI;
    }

    public void setEfoSynonymAnnotationURI(String efoSynonymAnnotationURI) {
        this.efoSynonymAnnotationURI = efoSynonymAnnotationURI;
    }

    public String getEfoObsoleteClassURI() {
        return efoObsoleteClassURI;
    }

    public void setEfoObsoleteClassURI(String efoObsoleteClassURI) {
        this.efoObsoleteClassURI = efoObsoleteClassURI;
    }

    public void doInitialization() throws OWLOntologyCreationException {
        // set property to make sure we can parse all of EFO
        System.setProperty("entityExpansionLimit", "128000");
        getLog().info("Loading EFO from " + getEfoURI() + "...");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IRI iri = IRI.create(getEfoURI());
        efo = manager.loadOntologyFromOntologyDocument(iri);

        getLog().info("Loaded " + efo.getOntologyID().getOntologyIRI() + " ok, creating indexes...");
        labelToClassMap = new HashMap<String, Set<OWLClass>>();
        classToLabelMap = new HashMap<OWLClass, List<String>>();
        iriToClassMap = new HashMap<IRI, OWLClass>();
        accessionToIRIMap = new HashMap<String, IRI>();

        // get obsolete class
        for (OWLClass nextClass : efo.getClassesInSignature()) {
            if (nextClass.getIRI().toURI().toString().equals(getEfoObsoleteClassURI())) {
                obsoleteClass = nextClass;
                break;
            }
        }
        if (obsoleteClass == null) {
            String message =
                    "Unable to recover the relevant OWLClasses from EFO - ObsoleteClass was not found";
            throw new OntologyIndexingException(message);
        }

        // loop over classes
        for (OWLClass owlClass : efo.getClassesInSignature()) {
            // check this isn't an obsolete class
            if (!isObsolete(owlClass)) {
                // get class names, and enter them in the maps
                List<String> classNames = getClassNames(owlClass);

                classToLabelMap.put(owlClass, classNames);
                for (String name : getClassNames(owlClass)) {
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

    public OWLClass getOWLClassByURI(URI uri) {
        try {
            waitUntilReady();
            IRI clsIRI = IRI.create(uri);
            return iriToClassMap.get(clsIRI);
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
        List<String> results = new ArrayList<String>();
        results.addAll(0, getClassRDFSLabels(owlClass));
        results.addAll(getClassSynonyms(owlClass));

        return results;
    }

    /**
     * Recovers all string values of the rdfs:label annotation attribute on the supplied class.  This is computed over
     * the inferred hierarchy, so labels of any equivalent classes will also be returned.
     *
     * @param owlClass the class to recover labels for
     * @return the literal values of the rdfs:label annotation
     */
    public synchronized Set<String> getClassRDFSLabels(OWLClass owlClass) {
        Set<String> classNames = new HashSet<String>();

        // get label annotation property
        OWLAnnotationProperty labelAnnotationProperty =
                efo.getOWLOntologyManager().getOWLDataFactory()
                        .getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        // get all label annotations
        Set<OWLAnnotation> labelAnnotations = owlClass.getAnnotations(
                efo, labelAnnotationProperty);

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
     * @param owlClass the class to retrieve the synonyms of
     * @return a set of strings containing all aliases of the supplied class
     */
    private synchronized Set<String> getClassSynonyms(OWLClass owlClass) {
        Set<String> classSynonyms = new HashSet<String>();

        // get synonym annotation property
        OWLAnnotationProperty synonymAnnotationProperty =
                efo.getOWLOntologyManager().getOWLDataFactory()
                        .getOWLAnnotationProperty(IRI.create(getEfoSynonymAnnotationURI()));

        // get all synonym annotations
        Set<OWLAnnotation> synonymAnnotations = owlClass.getAnnotations(
                efo, synonymAnnotationProperty);

        for (OWLAnnotation synonymAnnotation : synonymAnnotations) {
            OWLAnnotationValue synonymAnnotationValue = synonymAnnotation.getValue();
            if (synonymAnnotationValue instanceof OWLLiteral) {
                classSynonyms.add(((OWLLiteral) synonymAnnotationValue).getLiteral());
            }
        }

        return classSynonyms;
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
        if (labelToClassMap.containsKey(searchString)) {
            return labelToClassMap.get(searchString);
        }
        else {
            return Collections.emptySet();
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
     * Returns true if this ontology term is obsolete in EFO, false otherwise.  In EFO, a term is defined to be obsolete
     * if and only if it is a subclass of ObsoleteTerm.
     *
     * @param owlClass the owlClass to check for obsolesence
     * @return true if obsoleted, false otherwise
     */
    private synchronized boolean isObsolete(OWLClass owlClass) {
        Set<OWLClassExpression> superclasses = owlClass.getSuperClasses(efo);
        for (OWLClassExpression oce : superclasses) {
            if (!oce.isAnonymous() && oce.asOWLClass().getIRI().toURI().equals(obsoleteClass.getIRI().toURI())) {
                return true;
            }
        }
        // if no superclasses are obsolete, this class isn't obsolete
        return false;
    }
}