package uk.ac.ebi.spot.goci.ontology.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.springframework.core.io.Resource;
import uk.ac.ebi.spot.goci.Initializable;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * An abstract implementation of an ontology loader.  Implementations should extend this class with the {@link
 * #loadOntology()}.  This class is provided as a general caching mechanism to allow an ontology to be loaded and
 * processed once before being converted into ZOOMA annotations
 *
 * @author Tony Burdett
 * @date 03/06/13
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public abstract class AbstractOntologyLoader extends Initializable implements OntologyLoader {
    private URI ontologyURI;
    private String ontologyName;
    private Resource ontologyResource;
    private Map<IRI, IRI> ontologyImportMappings;

    private Collection<URI> synonymURIs;
    private URI exclusionClassURI;
    private URI exclusionAnnotationURI;

    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    private IRI ontologyIRI;
    private OWLOntology ontology;

    private Map<IRI, String> ontologyAccessions;
    private Map<IRI, String> ontologyLabels;
    private Map<IRI, Set<String>> ontologyParentLabels;
    private Map<IRI, Set<String>> ontologyChildLabels;
    private Map<IRI, Set<String>> ontologySynonyms;
    private Map<IRI, Set<Relationship<OWLClass, OWLObjectProperty, OWLClass>>> ontologyRelationships;

    private OWLAnnotationProperty rdfsLabelAnnotationProperty;
    private Collection<OWLAnnotationProperty> synonymAnnotationProperties;

    /**
     * Returns the URI of the ontology to load
     *
     * @return the URI of the ontology to load
     */
    public URI getOntologyURI() {
        return ontologyURI;
    }

    /**
     * Sets the URI of the ontology to load.  If accompanied with an <code>ontologyResource</code> property, this
     * ontology will be loaded from the location specified by the resource
     *
     * @param ontologyURI the URI of the target ontology to load
     */
    public void setOntologyURI(URI ontologyURI) {
        this.ontologyURI = ontologyURI;
    }

    /**
     * Sets the short name of the ontology
     *
     * @param ontologyName a short form name of the ontology
     */
    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }

    /**
     * Returns the short name of the ontology
     *
     * @return the short name of the ontology
     */
    public String getOntologyName() {
        return ontologyName;
    }

    /**
     * Returns the location from which the ontology (specified by the <code>ontologyURI</code> property) will be loaded
     * from
     *
     * @return a spring Resource representing this ontology
     */
    public Resource getOntologyResource() {
        return ontologyResource;
    }

    /**
     * Sets the location from which to load the ontology, if required. Setting this property creates a mapper that
     * prompts the OWL API to load the ontology from the supplied location, instead of attempting to resolve to the URL
     * corresponding to the ontology IRI. This property is optional.
     *
     * @param ontologyResource the resource at which EFO can be found, using spring configuration syntax (URLs,
     *                         classpath:...)
     */
    public void setOntologyResource(Resource ontologyResource) {
        this.ontologyResource = ontologyResource;
    }

    /**
     * Returns a series of mappings between two IRIs to describe where to load any imported ontologies, declared by the
     * ontology being loaded, should be acquired from.  In the returned map, each key is a logical name of an imported
     * ontology, and each value is the physical location the ontology should be loaded from.  In other words, if this
     * <code>OntologyLoader</code> loads an ontology <code>http://www.test.com/ontology_A</code> and ontology A
     * declares
     * <pre><owl:imports rdf:resource="http://www.test.com/ontology_B" /></pre>, if no import mappings are set then
     * ontology B will be loaded from <code>http://www.test.com/ontology_B</code>.  Declaring a mapping
     * {http://www.test.com/ontology_B, file://tmp/ontologyB.owl}, though, will cause ontology B to be loaded from a
     * local copy of the file.
     *
     * @return the ontology import mappings, logical IRI -> physical location IRI
     */
    public Map<IRI, IRI> getOntologyImportMappings() {
        return ontologyImportMappings;
    }

    /**
     * Sets a series of mappings between two IRIs to describe where to load any imported ontologies, declared by the
     * ontology being loaded, should be acquired from.  In the supplied map argument, each key should be a logical name
     * of an imported ontology, and each value should be the physical location the ontology should be loaded from.
     *
     * @param ontologyImportMappings the ontology import mappings, logical IRI -> physical location IRI
     * @see #getOntologyImportMappings()
     */
    public void setOntologyImportMappings(Map<IRI, IRI> ontologyImportMappings) {
        this.ontologyImportMappings = ontologyImportMappings;
    }

    /**
     * Gets the URI used to denote synonym annotations in this ontology.  As there is no convention for this (i.e. no
     * rdfs:synonym), ontologies tend to define their own.
     *
     * @return the synonym annotation URI
     */
    public Collection<URI> getSynonymURIs() {
        return synonymURIs;
    }

    /**
     * Sets the URI used to denote synonym annotations in this ontology. The specific property in use in the given
     * ontology should be specified here.
     *
     * @param synonymURI the URI representing synonym annotations
     */
    public void setSynonymURIs(Collection<URI> synonymURI) {
        this.synonymURIs = synonymURI;
    }

    /**
     * Gets the URI used to denote a class which represents the superclass of all classes to exclude in this ontology.
     * When this ontology is loaded, all subclasses of the class with this URI will be excluded.  This is to support the
     * case where an ontology has declared an "Obsolete" class and favours moving classes under this heirarchy as
     * opposed to deleting classes.
     *
     * @return the URI representing the class in the hierarchy that denotes classes to exclude during loading
     */
    public URI getExclusionClassURI() {
        return exclusionClassURI;
    }

    /**
     * Sets the URI used to denote classes in this ontology that should be excluded from loading.  Any subclasses of the
     * exclusion class should be ignored.
     *
     * @param exclusionClassURI the URI representing the class in the hierarchy that denotes classes to exclude during
     *                          loading
     */
    public void setExclusionClassURI(URI exclusionClassURI) {
        this.exclusionClassURI = exclusionClassURI;
    }

    /**
     * Gets the URI of an annotation property that is used to exclude classes during loading.  This is to support the
     * case where an ontology used an annotation property to act as a flag indicating that classes should not be shown
     * or else are deprecated.  Any classes with an annotation with this URI will be excluded from loading
     *
     * @return the URI representing the annotation that denotes an exclusion flag
     */
    public URI getExclusionAnnotationURI() {
        return exclusionAnnotationURI;
    }

    /**
     * Sets the URI of the annotation used to flag that a class should be excluded from loading.
     *
     * @param exclusionAnnotationURI the URI representing the annotation that denotes an exclusion flag
     */
    public void setExclusionAnnotationURI(URI exclusionAnnotationURI) {
        this.exclusionAnnotationURI = exclusionAnnotationURI;
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLDataFactory getFactory() {
        return factory;
    }

    @Override public IRI getOntologyIRI() {
        return lazyGet(() -> ontologyIRI);
    }

    @Override public OWLOntology getOntology() {
        return lazyGet(() -> ontology);
    }

    @Override public Map<IRI, String> getOntologyClassAccessions() {
        return lazyGet(() -> ontologyAccessions);
    }

    @Override public Map<IRI, String> getOntologyClassLabels() {
        return lazyGet(() -> ontologyLabels);
    }

    @Override public Map<IRI, Set<String>> getOntologyClassParentLabels() {
        return lazyGet(() -> ontologyParentLabels);
    }

    @Override public Map<IRI, Set<String>> getOntologyClassChildLabels() {
        return lazyGet(() -> ontologyChildLabels);
    }

    @Override public Map<IRI, Set<String>> getOntologyClassSynonyms() {
        return lazyGet(() -> ontologySynonyms);
    }

    @Override
    public Map<IRI, Set<Relationship<OWLClass, OWLObjectProperty, OWLClass>>> getOntologyClassRelationships() {
        return lazyGet(() -> ontologyRelationships);
    }

    @Override public String getAccession(IRI ontologyClassIRI) {
        return getOntologyClassAccessions().get(ontologyClassIRI);
    }

    @Override public String getLabel(IRI ontologyClassIRI) {
        return getOntologyClassLabels().get(ontologyClassIRI);
    }

    @Override public Set<String> getParentLabels(IRI ontologyClassIRI) {
        if (getOntologyClassParentLabels().containsKey(ontologyClassIRI)) {
            return getOntologyClassParentLabels().get(ontologyClassIRI);
        }
        else {
            return Collections.emptySet();
        }
    }

    @Override public Set<String> getSynonyms(IRI ontologyClassIRI) {
        if (getOntologyClassSynonyms().containsKey(ontologyClassIRI)) {
            return getOntologyClassSynonyms().get(ontologyClassIRI);
        }
        else {
            return Collections.emptySet();
        }
    }

    @Override public Set<String> getChildLabels(IRI ontologyClassIRI) {
        if (getOntologyClassChildLabels().containsKey(ontologyClassIRI)) {
            return getOntologyClassChildLabels().get(ontologyClassIRI);
        }
        else {
            return Collections.emptySet();
        }
    }

    @Override public Set<Relationship<OWLClass, OWLObjectProperty, OWLClass>> getRelationships(IRI ontologyClassIRI) {
        if (getOntologyClassRelationships().containsKey(ontologyClassIRI)) {
            return getOntologyClassRelationships().get(ontologyClassIRI);
        }
        else {
            return Collections.emptySet();
        }
    }

    private <G> G lazyGet(Callable<G> callable) {
        try {
            initOrWait();
            return callable.call();
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(getClass().getSimpleName() + " failed to initialize", e);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to lazily instantiate collection for query", e);
        }
    }

    @Override protected void doInitialization() throws Exception {
        // init owl fields
        this.manager = OWLManager.createOWLOntologyManager();
        if (getOntologyResource() != null) {
            getLog().info("Mapping ontology IRI from " + getOntologyURI() + " to " + getOntologyResource().getURI());
            this.manager.addIRIMapper(new SimpleIRIMapper(IRI.create(getOntologyURI()),
                                                          IRI.create(getOntologyResource().getURI())));
        }
        if (getOntologyImportMappings() != null) {
            for (IRI from : getOntologyImportMappings().keySet()) {
                IRI to = getOntologyImportMappings().get(from);
                getLog().info("Mapping imported ontology IRI from " + from + " to " + to);
                this.manager.addIRIMapper(new SimpleIRIMapper(from, to));
            }
        }
        this.factory = manager.getOWLDataFactory();

        // init cache fields
        this.ontologyAccessions = new HashMap<>();
        this.ontologyLabels = new HashMap<>();
        this.ontologyParentLabels = new HashMap<>();
        this.ontologyChildLabels = new HashMap<>();
        this.ontologySynonyms = new HashMap<>();
        this.ontologyRelationships = new HashMap<>();

        // init other fields (label, synonym annotation properties)
        this.rdfsLabelAnnotationProperty = getFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        this.synonymAnnotationProperties = getSynonymURIs().stream()
                .map(ap -> getFactory().getOWLAnnotationProperty(IRI.create(ap)))
                .collect(Collectors.toSet());

        // load the ontology
        this.ontology = loadOntology();
    }

    @Override protected void doTermination() throws Exception {
        // nothing to do
    }

    protected Set<String> getStringLiteralAnnotationValues(OWLOntology ontology,
                                                           OWLClass ontologyClass,
                                                           OWLAnnotationProperty annotationProperty) {
        Set<String> vals = new HashSet<>();
        Collection<OWLAnnotation> annotations = ontologyClass.getAnnotations(ontology, annotationProperty);
        annotations
                .stream()
                .filter(annotation -> annotation.getValue() instanceof OWLLiteral)
                .forEach(annotation -> {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    vals.add(val.getLiteral());
                });
        return vals;
    }

    protected void setOntologyIRI(IRI ontologyIRI) {
        this.ontologyIRI = ontologyIRI;
    }

    protected void addClassAccession(IRI clsIri, String accession) {
        this.ontologyAccessions.put(clsIri, accession);
    }

    protected void addClassLabel(IRI clsIri, String label) {
        this.ontologyLabels.put(clsIri, label);
    }

    protected void addClassParentLabels(IRI clsIri, Set<String> classParentLabels) {
        this.ontologyParentLabels.put(clsIri, classParentLabels);
    }

    protected void addClassChildLabels(IRI clsIri, Set<String> classChildLabels) {
        this.ontologyChildLabels.put(clsIri, classChildLabels);
    }

    protected void addSynonyms(IRI clsIri, Set<String> synonyms) {
        this.ontologySynonyms.put(clsIri, synonyms);
    }

    protected void addRelationship(IRI clsIri, Set<Relationship<OWLClass, OWLObjectProperty, OWLClass>> relationships) {
        this.ontologyRelationships.put(clsIri, relationships);
    }

    /**
     * Extracts and loads into memory all the class labels and corresponding IRIs.  This class makes the assumption that
     * one primary label per class exists. If any classes contain multiple rdfs:labels, these classes are ignored.
     * <p>
     * Once loaded, this method must set the IRI of the ontology, and should add class labels, class types (however you
     * chose to implement the concept of a "type") and synonyms, where they exist.
     * <p>
     * Implementations do not need to concern themselves with resolving imports or physical/logical mappings as this is
     * done in initialisation at the abstract level.  Subclasses can simply do <code>OWLOntology ontology =
     * getManager().loadOntology(IRI.create(getOntologyURI()));</code> as a basic implementation before populating the
     * various required caches
     */
    protected OWLOntology loadOntology() throws OWLOntologyCreationException {
        try {
            getLog().debug("Loading ontology...");
            OWLOntology ontology = getManager().loadOntology(IRI.create(getOntologyURI()));
            IRI ontologyIRI = ontology.getOntologyID().getOntologyIRI();
            if (ontologyIRI == null) {
                throw new OWLOntologyCreationException("Failed to load ontology from " + getOntologyURI() + ": " +
                                                               "no IRI present for this ontology");
            }
            else {
                setOntologyIRI(ontologyIRI);
                if (getOntologyName() == null) {
                    URI ontologyURI = ontologyIRI.toURI();
                    String name = ontologyURI.getFragment() != null ? ontologyURI.getFragment() : ontologyURI.getPath();
                    if (name == null) {
                        getLog().warn("Can't shorten the name for " + ontologyIRI.toString());
                        name = ontologyURI.toString();
                    }
                    setOntologyName(name);
                }
                getLog().debug("Successfully loaded ontology " + ontologyIRI);
                getLog().debug("Computing indexes...");
                return indexOntology(ontology);
            }
        }
        finally {
            getLog().debug("Done loading/indexing");
        }
    }

    protected Set<OWLClass> removeExcludedClasses(OWLOntology ontology,
                                                  Set<OWLClass> allClasses,
                                                  SubclassCollector subclassCollector) {
        // remove excluded classes from allClasses by subclass
        if (getExclusionClassURI() != null) {
            OWLClass excludeClass = getFactory().getOWLClass(IRI.create(getExclusionClassURI()));
            subclassCollector.collect(excludeClass).forEach(allClasses::remove);
        }

        // remove excluded classes from allClasses by annotation property
        if (getExclusionAnnotationURI() != null) {
            OWLAnnotationProperty excludeAnnotation =
                    getFactory().getOWLAnnotationProperty(IRI.create(getExclusionAnnotationURI()));
            Iterator<OWLClass> allClassesIt = allClasses.iterator();
            while (allClassesIt.hasNext()) {
                OWLClass owlClass = allClassesIt.next();
                Collection<OWLAnnotation> annotations = owlClass.getAnnotations(ontology, excludeAnnotation);
                if (!annotations.isEmpty()) {
                    allClassesIt.remove();
                }
            }
        }

        // and return
        return allClasses;
    }

    protected Optional<String> evaluateAccessionValue(OWLOntology ontology, OWLClass ontologyClass) {
        URI uri = ontologyClass.getIRI().toURI();
        getLog().trace("Attempting to extract fragment name of URI '" + uri + "'");
        String termURI = uri.toString();

        // we want the "final part" of the URI...
        String fragmentName;
        if (uri.getFragment() != null) {
            // a uri with a non-null fragment, so use this...
            getLog().trace("Extracting fragment name using URI fragment (" + uri.getFragment() + ")");
            return Optional.of(uri.getFragment());
        }
        else if (uri.getPath() != null) {
            // no fragment, but there is a path so try and extract the final part...
            if (uri.getPath().contains("/")) {
                getLog().trace("Extracting fragment name using final part of the path of the URI");
                return Optional.of(uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1));
            }
            else {
                // no final path part, so just return whole path
                getLog().trace("Extracting fragment name using the path of the URI");
                return Optional.of(uri.getPath());
            }
        }
        else {
            // no fragment, path is null, we've run out of rules so don't shorten
            getLog().trace("No rules to shorten this URI could be found (" + termURI + ")");
            return Optional.empty();
        }
    }

    protected Optional<String> evaluateLabelAnnotationValue(OWLOntology ontology, OWLClass ontologyClass) {
        // get label annotations
        Set<String> labels = getStringLiteralAnnotationValues(ontology, ontologyClass, rdfsLabelAnnotationProperty);
        if (labels.isEmpty()) {
            getLog().warn("OWLClass " + ontologyClass + " contains no label. " +
                                  "No labels for this class will be loaded.");
        }
        else {
            if (labels.size() > 1) {
                getLog().warn("OWLClass " + ontologyClass + " contains more than one label " +
                                      "(including '" + labels.iterator().next() + "'). " +
                                      "No labels for this class will be loaded.");
            }
            else {
                return Optional.of(labels.iterator().next());
            }
        }
        return Optional.empty();
    }

    protected Set<String> evaluateSynonymAnnotationValues(OWLOntology ontology, OWLClass ontologyClass) {
        // get all synonym annotations
        Set<String> synonyms = new HashSet<>();
        for (OWLAnnotationProperty synonymAnnotationProperty : synonymAnnotationProperties) {
            Set<String> synonymVals =
                    getStringLiteralAnnotationValues(ontology, ontologyClass, synonymAnnotationProperty);
            if (synonymVals.isEmpty()) {
                getLog().trace("OWLClass " + ontologyClass + " contains no synonyms. " +
                                       "No synonyms for this class will be loaded.");
            }
            else {
                synonyms.addAll(synonymVals);
            }
        }
        return synonyms;
    }

    protected abstract OWLOntology indexOntology(OWLOntology ontology) throws OWLOntologyCreationException;

    /**
     * A functional interface that represents how to collect a set of OWLClasses that are the subclasses of the supplied
     * class
     */
    @FunctionalInterface
    protected interface SubclassCollector {
        Set<OWLClass> collect(OWLClass superclass);
    }
}
