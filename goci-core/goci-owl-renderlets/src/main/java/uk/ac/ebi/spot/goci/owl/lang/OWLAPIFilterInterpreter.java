package uk.ac.ebi.spot.goci.owl.lang;

import org.semanticweb.owlapi.model.OWLClassExpression;
import uk.ac.ebi.spot.goci.lang.Filter;
import uk.ac.ebi.spot.goci.lang.OntologyConfiguration;

/**
 * Interprets {@link uk.ac.ebi.spot.goci.lang.Filter} objects into OWLAPI class expressions
 *
 * @author Tony Burdett
 * @date 04/06/14
 */
public class OWLAPIFilterInterpreter {
    private final OntologyConfiguration ontologyConfiguration;

    public OWLAPIFilterInterpreter(OntologyConfiguration ontologyConfiguration) {
        this.ontologyConfiguration = ontologyConfiguration;
    }

    public OntologyConfiguration getOntologyConfiguration() {
        return ontologyConfiguration;
    }

    public OWLClassExpression interpretFilters(Filter... filters) {
        // todo - correctly interpret the supplied filter to the relevant class expression
        return getOntologyConfiguration().getOWLDataFactory().getOWLThing();
    }
}
