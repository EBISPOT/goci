package uk.ac.ebi.spot.goci.model;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/02/15
 */
public abstract class EmbeddableDocument<O> extends OntologyEnabledDocument<O> {
    public EmbeddableDocument(O object) {
        super(object);
    }
}
