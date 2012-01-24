package uk.ac.ebi.fgpt.goci.service;

import org.semanticweb.owlapi.model.OWLOntology;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.util.Collection;

/**
 * A service that will convert GWAS model objects into entities in a given ontology
 *
 * @author Tony Burdett
 * @date 24/01/12
 */
public interface GWASOWLConverter {
    /**
     * Converts the provided studies into ontology entities and adds them to the supplied target ontology
     *
     * @param studies the studies retrieved from the GWAS catalog
     * @param target  the ontology to write the studies to
     */
    void addStudiesToOntology(Collection<Study> studies, OWLOntology target);

    /**
     * Converts the provided SNPs into ontology entities and adds them to the supplied target ontology
     *
     * @param snps   the SNPs retrieved from the GWAS catalog
     * @param target the ontology to write the studies to
     */
    void addSNPsToOntology(Collection<SingleNucleotidePolymorphism> snps, OWLOntology target);

    /**
     * Converts the provided trait assocations into ontology entities and adds them to the supplied target ontology
     *
     * @param associations the trait associations retrieved from the GWAS catalog
     * @param target       the ontology to write the studies to
     */
    void addAssociationsToOntology(Collection<TraitAssociation> associations, OWLOntology target);
}
