package uk.ac.ebi.fgpt.goci.service;

import org.semanticweb.owlapi.model.IRI;

/**
 * Mints an IRI for a type of object given a base IRI and an object
 *
 * @author Tony Burdett
 * @date 26/01/12
 */
public interface IRIMinter<T> {
    /**
     * Mints an IRI for the given object, t, by creating a unique identifier for t and appending it to the base IRI
     *
     * @param base the base IRI, usually of the ontology t will be added to
     * @param t the object to mint an IRI for
     * @return the newly minted IRI
     */
    IRI mint(String base, T t);
}
