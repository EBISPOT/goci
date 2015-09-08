package uk.ac.ebi.spot.goci.ontology.owl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
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
     * "accession" - or a user friendly 'short name' or identifier.  This will normally be the URI fragment or path part
     * of a full URI.
     *
     * @return a user friendly representation of the class IRI
     */
    Map<IRI, String> getOntologyClassAccessions();

    /**
     * Returns a mapping between the IRIs that identify classes in the loaded ontology and the corresponding class
     * rdfs:label.
     *
     * @return the class labels in this ontology, indexed by class IRI
     */
    Map<IRI, String> getOntologyClassLabels();

    /**
     * Returns a mapping between the IRIs that identify classes in the loaded ontology and the rdfs:label of each of
     * their known parent classes.
     *
     * @return the parent class labels in this ontology, indexed by class IRI
     */
    Map<IRI, Set<String>> getOntologyClassParentLabels();

    /**
     * Returns a mapping between the IRIs that identify classes in the loaded ontology and the rdfs:label of each of
     * their known child classes.
     *
     * @return the child class labels in this ontology, indexed by class IRI
     */
    Map<IRI, Set<String>> getOntologyClassChildLabels();

    /**
     * Returns a mapping between the IRIs that identify classes in the loaded ontology and the corresponding class
     * synonym.  Synonyms are specified by the synonymURI property.
     *
     * @return the class labels in this ontology, indexed by class IRI
     */
    Map<IRI, Set<String>> getOntologyClassSynonyms();

    /**
     * Returns a mapping between the IRIs that identify classes in the loaded ontology and a set of {@link
     * Relationship}s that describes how this class relates to other classes in the ontology.  Only direct relations are
     * included (i.e. no nested class expressions)
     *
     * @return the relationships in this ontology, indexed by class IRI
     */
    Map<IRI, Set<Relationship<OWLClass, OWLObjectProperty, OWLClass>>> getOntologyClassRelationships();

    /**
     * Returns the class "accession" - or a user friendly 'short name' or identifier.  This will normally be the URI
     * fragment or path part of a full URI.
     *
     * @return a user friendly representation of the class IRI
     */
    String getAccession(IRI ontologyClassIRI);

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
    Set<String> getParentLabels(IRI ontologyClassIRI);

    /**
     * Returns the rdfs:label of each of the known child classes for the given class.
     *
     * @param ontologyClassIRI the IRI of the class to get the child labels of
     * @return a set of strings representing the labels of the children of this class
     */
    Set<String> getChildLabels(IRI ontologyClassIRI);

    /**
     * Returns the synonyms of the class specified.  Synonyms are described by the synonymURI property
     *
     * @param ontologyClassIRI the IRI of the class to get synonyms for
     * @return a set of strings representing the synonyms of this class
     */
    Set<String> getSynonyms(IRI ontologyClassIRI);

    /**
     * Returns a set of {@link Relationship}s that describes how this class relates to other classes in the ontology.
     * Only direct relations are included (i.e. no nested class expressions)
     *
     * @param ontologyClassIRI the IRI of the class to get relationships for
     * @return the relationships for this class
     */
    Set<Relationship<OWLClass, OWLObjectProperty, OWLClass>> getRelationships(IRI ontologyClassIRI);
}
