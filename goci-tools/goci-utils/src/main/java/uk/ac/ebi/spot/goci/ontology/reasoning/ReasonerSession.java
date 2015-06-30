package uk.ac.ebi.spot.goci.ontology.reasoning;

import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * A cacheable reasoner session that can be used to load reasoners for the GWAS ontology given a GWASOWLPublisher.
 *
 * @author Tony Burdett
 * Date 05/03/12
 */
public interface ReasonerSession {
    /**
     * Returns true if this reasoner session is fully initialized and able to respond to a request to {@link
     * #getReasoner()}, and false if the reasoner is not yet ready.
     * <p/>
     * Implementations are free to define their own strategies for initializing reasoners - some may choose to lazily
     * initialize the reasoner, whereas others may prefer up-front initialization to occur in a parallel thread.
     * Regardless of strategy, this method should return true or false immediately, whereas calls to {@link
     * #getReasoner()} must block whilst the reasoner initializes.  This method provides a means for clients to avoid
     * being blocked in cases where the reasoner can be initialized in the background.
     *
     * @return true if the reasoner for this session is fully initialized, false otherwise
     */
    boolean isReasonerInitialized();

    /**
     * Acquire a reasoner that serves up a reasoned view of the GWAS OWL ontology
     *
     * @return a reasoner over the GWAS OWL ontology
     */
    OWLReasoner getReasoner();
}
