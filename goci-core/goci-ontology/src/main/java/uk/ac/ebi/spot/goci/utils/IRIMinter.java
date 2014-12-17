package uk.ac.ebi.spot.goci.utils;

import org.semanticweb.owlapi.model.IRI;

/**
 * Mints an IRI for a type of object given a base IRI and an object
 *
 * @author Tony Burdett Date 26/01/12
 */
public interface IRIMinter<T> {
    /**
     * Mints an IRI for the given object, t, by creating a unique identifier for t and appending it to the base IRI. The
     * unique ID is derived from the object type and the unique ID of the object. Generally, IRIs will be of the form
     * {base}/{ObjectType}/{ObjectUniqueID}.
     *
     * @param base the base IRI, usually of the ontology t will be added to
     * @param t    the object to mint an IRI for
     * @return the newly minted IRI
     */
    IRI mint(String base, T t);

    /**
     * Mints an IRI for the given object, t, by creating a unique identifier for t (using the additional prefix) and
     * appending it to the base IRI.  Generally, IRIs will be of the form {base}/{prefix}/{ObjectUniqueID}.
     *
     * @param base   the base IRI, usually of the ontology t will be added to
     * @param prefix a custom prefix to use whilst naming the entity
     * @param t      the object to mint an IRI for
     * @return the newly minted IRI
     */
    IRI mint(String base, String prefix, T t);

    /**
     * Mints an IRI for the given object, t, by creating a unique identifier for t (using the additional prefix) and
     * appending it to the base IRI.  IRIs will be of the form {base}/{prefix}/{ObjectUniqueID} if isStable == true and
     * {base}/{prefix}#{ObjectUniqueID} if isStable == false.
     *
     * @param base   the base IRI, usually of the ontology t will be added to
     * @param prefix a custom prefix to use whilst naming the entity
     * @param t      the object to mint an IRI for
     * @return the newly minted IRI
     */
    IRI mint(String base, String prefix, T t, boolean isStable);

    /**
     * Mints a custom IRI from the supplied arguments.  IRIs will be of the form {base}/{objectName}.
     *
     * @param base       the base IRI, usually of the ontology t will be added to
     * @param objectName the name of the object to use in the IRI
     * @return the newly minted IRI
     */
    IRI mint(String base, String objectName);

    /**
     * Mints a custom IRI from the supplied arguments.  IRIs will be of the form {base}/{prefix}/{objectName}.
     *
     * @param base       the base IRI, usually of the ontology t will be added to
     * @param prefix     a custom prefix to use whilst naming the entity
     * @param objectName the name of the object to use in the IRI
     * @return the newly minted IRI
     */
    IRI mint(String base, String prefix, String objectName);

    /**
     * Mints a custom IRI from the supplied arguments.  IRIs will be of the form {base}/{prefix}/{objectName} if
     * isStable == true, and {base}/{prefix}#{objectName} if isStable == false.
     *
     * @param base       the base IRI, usually of the ontology t will be added to
     * @param prefix     a custom prefix to use whilst naming the entity
     * @param objectName the name of the object to use in the IRI
     * @param isStable   whether the IRI being minted is stable across versions or may change
     * @return the newly minted IRI
     */
    IRI mint(String base, String prefix, String objectName, boolean isStable);
}
