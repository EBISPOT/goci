package uk.ac.ebi.spot.goci.ontology.owl;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 20/01/15
 */
public interface Relationship<S extends OWLClassExpression, P extends OWLProperty, O extends OWLClassExpression> {
    S getSubject();

    P getPredicate();

    O getObject();

    String getPredicateLabel();

    String getObjectLabel();
}
