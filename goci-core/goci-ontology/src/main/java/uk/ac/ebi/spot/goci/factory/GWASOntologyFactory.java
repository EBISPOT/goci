package uk.ac.ebi.spot.goci.factory;

import org.semanticweb.owlapi.model.OWLOntology;

/**
 * An abstract factory interface used to define the logic for loading the GWAS ontology into
 *
 * @author Tony Burdett
 * Date 24/01/12
 */
public interface GWASOntologyFactory {
    /**
     * Loads a new copy of the GWAS ontology.
     *
     * @return returns a newly loaded copy of the GWAS ontology
     */
    OWLOntology loadOntology();
}
