package uk.ac.ebi.spot.goci.service;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.spot.goci.exception.OWLConversionException;

import java.io.File;

/**
 * A service that is capable of publishing GWAS data, represented in OWL.  This is accomplished by mining the GWAS
 * catalog database and converting relevant concepts into OWL entities which are then written to an ontology.
 *
 * @author Tony Burdett Date 24/01/12
 */
public interface GWASOWLPublisher {
    /**
     * Fetches data from the GWAS catalog, converts it and writes it to an OWLOntology, and returns all data
     *
     * @return an ontology containing all the data in the GWAS catalog
     * @throws uk.ac.ebi.spot.goci.exception.OWLConversionException if conversion or publishing of the data to OWL
     *                                                              failed
     */
    OWLOntology publishGWASData() throws OWLConversionException;

    /**
     * Publishes the inferred view of the GWAS catalog data from the asserted ontology.
     *
     * @param ontology the ontology to reason over and then publish
     * @return the reasoner that results from inferring over this ontology
     * @throws OWLConversionException if the inferreed view failed to calculate
     */
    OWLReasoner publishGWASDataInferredView(OWLOntology ontology) throws OWLConversionException;

    /**
     * Saves the supplied ontology to the given location
     *
     * @param ontology   the ontology to save
     * @param outputFile the location this ontology should be written to
     * @throws OWLConversionException if something went wrong whilst saving
     */
    void saveGWASData(OWLOntology ontology, File outputFile) throws OWLConversionException;

    /**
     * Saves the supplied inferred view of the reasoner to the given location
     *
     * @param reasoner   the inferred ontology to save
     * @param outputFile the location this ontology should be written to
     * @throws OWLConversionException if something went wrong whilst saving
     */
    void saveGWASDataInferredView(OWLReasoner reasoner, File outputFile) throws OWLConversionException;
}
