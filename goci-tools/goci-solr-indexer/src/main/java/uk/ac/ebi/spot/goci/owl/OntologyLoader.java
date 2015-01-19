package uk.ac.ebi.spot.goci.owl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Map;
import java.util.Set;

/**
 * An ontology loader that provides some abstraction around core concepts in ontologies loaded via the OWLAPI.  This
 * interface provides a mechanism to decouple ontology loading and processing from the activity of generating ZOOMA
 * annotations.  This allows for a variety of ontology loading strategies and implementations, as long as you can
 * extract text to class IRI mappings.
 *
 * @author Tony Burdett
 * @date 03/06/13
 */
public interface OntologyLoader {
    /**
     * Get the ontology IRI.  This returns the IRI of the ontology that was actually loaded, and may be different from
     * the ontologyURI specified if declared differently in the loaded file.
     *
     * @return IRI of the ontology
     */
    IRI getOntologyIRI();

    /**
     * Get the ontology object that was loaded by this ontology loader
     *
     * @return the ontology object itself
     */
    OWLOntology getOntology();

    /**
     * Get the ontology name.  This is a short name for the ontology, for example "efo" for the experimental factor
     * ontology
     *
     * @return IRI of the ontology
     */
    String getOntologyName();

    /**
     * Returns a mapping between the IRIs that identify classes in the loaded ontology and the corresponding class
     * rdfs:label.
     *
     * @return the class labels in this ontology, indexed by class IRI
     */
    Map<IRI, String> getOntologyClassLabels();

    /**
     * Returns a mapping between the IRIs that identify classes in the loaded ontology and the rdfs:label of each of
     * their asserted parent classes.
     *
     * @return the class type labels in this ontology, indexed by class IRI
     */
    Map<IRI, Set<String>> getOntologyClassTypeLabels();

    /**
     * Returns a mapping between the IRIs that identify classes in the loaded ontology and the corresponding class
     * synonym.  Synonyms are specified by the synonymURI property.
     *
     * @return the class labels in this ontology, indexed by class IRI
     */
    Map<IRI, Set<String>> getOntologyClassSynonyms();

    /**
     * Returns the rdfs:label of the given ontology class
     *
     * @param ontologyClassIRI the IRI of the class to get the label for
     * @return a string representing the label of this class
     */
    String getLabel(IRI ontologyClassIRI);

    /**
     * Returns a set of strings representing the the rdfs:label of each of the asserted parent classes for the given
     * class
     *
     * @param ontologyClassIRI the IRI of the class to get the parent labels of
     * @return a set of strings representing the labels of the parents of this class
     */
    Set<String> getTypeLabels(IRI ontologyClassIRI);

    /**
     * Returns the synonyms of the class specified.  Synonyms are described by the synonymURI property
     *
     * @param ontologyClassIRI the IRI of the class to get synonyms for
     * @return a set of strings representing the synonyms of this class
     */
    Set<String> getSynonyms(IRI ontologyClassIRI);
}
