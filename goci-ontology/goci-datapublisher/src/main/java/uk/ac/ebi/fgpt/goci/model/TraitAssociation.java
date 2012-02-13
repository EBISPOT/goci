package uk.ac.ebi.fgpt.goci.model;

import org.semanticweb.owlapi.model.OWLClass;

/**
 * A model object that links a SNP to its associated trait.  Each TraitAssociation must declare a link between exactly
 * one SNP and one Trait - neither of these fields must ever be null.  The p-value of this association as identified in
 * the source publication is present, and the trait is described here with an OWL class derived from <a
 * href="http://www.ebi.ac.uk/efo>EFO</a>
 *
 * @author Tony Burdett
 * @date 24/01/12
 */
public interface TraitAssociation {
    /**
     * Gets the PubMed ID of the publication that identifies this assocation
     *
     * @return the PubMed ID of the study that identified this association
     */
    String getPubMedID();

    /**
     * Returns the reference ID of the SNP identified in this association.  You may need to use this if there is missing
     * information about the SNP: in such cases, {@link #getAssociatedSNP()} is likely to throw an {@link
     * uk.ac.ebi.fgpt.goci.exception.ObjectMappingException} and yet in order for this association to be declared the
     * SNP ID must be known.  In these cases, uses this method to fetch the known ID.
     *
     * @return the RSID of an incompletely specified SNP
     */
    String getAssociatedSNPReferenceId();

    /**
     * Gets the SNP identified in this assocation.  This must never be null: by contract, a TraitAssociation instance
     * must declare a one to one link between a SNP and a trait.
     *
     * @return the associated SNP
     */
    SingleNucleotidePolymorphism getAssociatedSNP();

    /**
     * Gets the EFO class that represents the trait identified in this association.  In cases where the trait is linked
     * to the "Experimental Factor" class from EFO  ('http://www.ebi.ac.uk/efo/EFO_0000001'), you can use {@link
     * #getUnmappedGWASLabel()} to return the trait name that was asserted in the GWAS catalog.
     *
     * @return the associated trait (as an EFO class)
     */
    OWLClass getAssociatedTrait();

    /**
     * Gets the p-value representing the strength of this assocation
     *
     * @return the association p-value
     */
    float getPValue();

    /**
     * Gets the label declared in the GWAS catalog in cases where it could not be mapped to an ontology class.
     * Generally, you will only need to use this method in cases where {@link #getAssociatedTrait()} throws an {@link
     * uk.ac.ebi.fgpt.goci.exception.MissingOntologyTermException}, but if you are interested in the precise trait named
     * used in the GWAS database you can get it using this method.
     *
     * @return the trait name as asserted in the underlying GWAS catalog data, probably taken directly from the source
     *         publication
     */
    String getUnmappedGWASLabel();
}
