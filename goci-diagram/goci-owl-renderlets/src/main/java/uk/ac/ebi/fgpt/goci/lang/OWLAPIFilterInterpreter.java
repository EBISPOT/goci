package uk.ac.ebi.fgpt.goci.lang;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;

/**
 * Interprets {@link Filter} objects into OWLAPI class expressions
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
