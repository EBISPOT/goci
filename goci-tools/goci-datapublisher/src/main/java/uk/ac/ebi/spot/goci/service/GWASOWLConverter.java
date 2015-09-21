package uk.ac.ebi.spot.goci.service;

import org.semanticweb.owlapi.model.OWLOntology;
import uk.ac.ebi.spot.goci.exception.OWLConversionException;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;

import uk.ac.ebi.spot.goci.model.Association;

import java.util.Collection;

/**
 * A service that will convert GWAS model objects into entities in a given ontology
 *
 * @author Tony Burdett
 * Date 24/01/12
 */
public interface GWASOWLConverter {
    /**
     * Creates a new empty ontology that can be used to do the data conversion.  This ontology will import the GWAS
     * diagram ontology that declares the schema.
     *
     * @return the newly created, empty, ontology
     * @throws uk.ac.ebi.spot.goci.exception.OWLConversionException
     *          if something went wrong whilst creating the ontology
     */
    OWLOntology createConversionOntology() throws OWLConversionException;

    /**
     * Converts the provided studies into ontology entities and adds them to the supplied ontology ontology
     *
     * @param studies  the studies retrieved from the GWAS catalog
     * @param ontology the ontology to write the studies to
     * @throws uk.ac.ebi.spot.goci.exception.OWLConversionException
     *          if something went wrong whilst adding data to the ontology
     */
    void addStudiesToOntology(Collection<Study> studies, OWLOntology ontology) throws OWLConversionException;

    /**
     * Converts the provided SNPs into ontology entities and adds them to the supplied ontology ontology
     *
     * @param snps     the SNPs retrieved from the GWAS catalog
     * @param ontology the ontology to write the studies to
     * @throws uk.ac.ebi.spot.goci.exception.OWLConversionException
     *          if something went wrong whilst adding data to the ontology
     */
    void addSNPsToOntology(Collection<SingleNucleotidePolymorphism> snps, OWLOntology ontology)
            throws OWLConversionException;

    /**
     * Converts the provided trait assocations into ontology entities and adds them to the supplied ontology ontology
     *
     * @param associations the trait associations retrieved from the GWAS catalog
     * @param ontology     the ontology to write the studies to
     * @throws uk.ac.ebi.spot.goci.exception.OWLConversionException
     *          if something went wrong whilst adding data to the ontology
     */
    void addAssociationsToOntology(Collection<Association> associations, OWLOntology ontology)
            throws OWLConversionException;
}
