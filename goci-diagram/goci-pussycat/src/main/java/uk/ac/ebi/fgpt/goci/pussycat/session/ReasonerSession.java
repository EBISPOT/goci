package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;

/**
 * A cacheable reasoner session that can be used to load reasoners for the GWAS ontology given a GWASOWLPublisher.
 *
 * @author Tony Burdett
 * @date 05/03/12
 */
public interface ReasonerSession {
    /**
     * Acquire a reasoner that serves up a reasoned view of the GWAS OWL ontology
     *
     * @return a reasoner over the GWAS OWL ontology
     * @throws OWLConversionException uif the GWAS data could not be loaded
     */
    OWLReasoner getReasoner() throws OWLConversionException;
}
