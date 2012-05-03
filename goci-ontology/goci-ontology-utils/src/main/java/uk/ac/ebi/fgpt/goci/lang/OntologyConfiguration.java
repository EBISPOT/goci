package uk.ac.ebi.fgpt.goci.lang;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A container for ontology mapping configuration.  This simple bean can be used to acquire a data factory and owl
 * manager based on mapping ontology IRIs to physical locations in order to be used across several classes.
 *
 * @author Tony Burdett
 * Date 15/02/12
 */
public class OntologyConfiguration {
    private Resource efoResource;
    private Resource gwasDiagramSchemaResource;

    private OWLOntologyManager manager;
    private OWLDataFactory factory;

    private Map<IRI, String> efoLabels;

    private boolean initialized = false;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public Resource getEfoResource() {
        return efoResource;
    }

    /**
     * Sets the location from which to load EFO, if required.  Setting this property creates a mapper that prompts the
     * OWL API to load EFO from the given location, instead of attempting to resolve to the URL corresponding to the
     * ontology IRI.  This property is optional.
     *
     * @param efoResource the resource at which EFO can be found, using spring configuration syntax (URLs,
     *                    classpath:...)
     */
    public void setEfoResource(Resource efoResource) {
        this.efoResource = efoResource;
    }

    public Resource getGwasDiagramSchemaResource() {
        return gwasDiagramSchemaResource;
    }

    /**
     * Sets the location from which to load the gwas diagram schema, if required.  Setting this property creates a
     * mapper that prompts the OWL API to load the gwas diagram schema from the given location, instead of attempting to
     * resolve to the URL corresponding to the ontology IRI.  This property is optional.
     *
     * @param gwasDiagramSchemaResource the resource at which the gwas diagram schema can be found, using spring
     *                                  configuration syntax (URLs, classpath:...)
     */
    public void setGwasDiagramSchemaResource(Resource gwasDiagramSchemaResource) {
        this.gwasDiagramSchemaResource = gwasDiagramSchemaResource;
    }

    public HashMap<IRI, String> setEfoLabels(){
        HashMap<IRI, String> labels = new HashMap<IRI, String>();

        System.setProperty("entityExpansionLimit", "100000000");

        try {
            getLog().debug("Trying to load EFO");
            OWLOntology efo = manager.loadOntology(IRI.create(OntologyConstants.EFO_ONTOLOGY_SCHEMA_IRI));
            getLog().debug(("Successfully loaded EFO"));
            Set<OWLClass> allClasses =  efo.getClassesInSignature();

            OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

            for(OWLClass efoClass : allClasses){
                IRI clsIri = efoClass.getIRI();
                String className = null;

                for (OWLAnnotation annotation : efoClass.getAnnotations(efo, label)) {
                    if (annotation.getValue() instanceof OWLLiteral) {
                        OWLLiteral val = (OWLLiteral) annotation.getValue();
                        className = val.getLiteral();
                    }
                    if(efoClass.getAnnotations(efo, label).size() != 1){
                        getLog().debug("More than one label for class " + className);
                    }
                }

                if(className == null){
                    getLog().debug("Class without label " + efoClass);
                }
                else{
                    labels.put(clsIri, className);
                }
            }
        }
        catch (OWLOntologyCreationException e) {
            getLog().error("Could not load EFO " + e);
        }
        return labels;
    }

    public void init() throws IOException {
        this.manager = OWLManager.createOWLOntologyManager();
        if (getEfoResource() != null) {
            getLog().info("Mapping EFO to " + getEfoResource().getURI());
            this.manager.addIRIMapper(new SimpleIRIMapper(IRI.create(OntologyConstants.EFO_ONTOLOGY_SCHEMA_IRI),
                                                          IRI.create(getEfoResource().getURI())));
        }
        if (getGwasDiagramSchemaResource() != null) {
            getLog().info("Mapping GWAS schema to " + getGwasDiagramSchemaResource().getURI());
            this.manager.addIRIMapper(new SimpleIRIMapper(IRI.create(OntologyConstants.GWAS_ONTOLOGY_SCHEMA_IRI),
                                                          IRI.create(getGwasDiagramSchemaResource().getURI())));
        }
        this.factory = manager.getOWLDataFactory();
        this.efoLabels = setEfoLabels();

        initialized = true;
    }

    public OWLOntologyManager getOWLOntologyManager() {
        if (initialized) {
            return manager;
        }
        else {
            throw new IllegalStateException("OntologyConfiguration has not been initialized");
        }
    }

    public OWLDataFactory getOWLDataFactory() {
        if (initialized) {
            return factory;
        }
        else {
            throw new IllegalStateException("OntologyConfiguration has not been initialized");
        }
    }

    public Map<IRI, String> getEfoLabels(){
        if (initialized){
            return efoLabels;
        }
        else {
            throw new IllegalStateException("OntologyConfiguration has not been initialized");
        }
    }
}
