package uk.ac.ebi.fgpt.goci.service;

import org.semanticweb.owlapi.model.OWLOntology;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;

/**
 * A service that is capable of publishing GWAS data, represented in OWL.  This is accomplished by mining the GWAS
 * catalog database and converting relevant concepts into OWL entities which are then written to an ontology.
 *
 * @author Tony Burdett
 * @date 24/01/12
 */
public interface GWASOWLPublisher {
    /**
     * Fetches data from the GWAS catalog, converts it and writes it to an OWLOntology, and returns all data
     *
     * @return an ontology containing all the data in the GWAS catalog
     * @throws uk.ac.ebi.fgpt.goci.exception.OWLConversionException
     *          if conversion or publishing of the data to OWL failed
     */
    OWLOntology publishGWASData() throws OWLConversionException;
}
