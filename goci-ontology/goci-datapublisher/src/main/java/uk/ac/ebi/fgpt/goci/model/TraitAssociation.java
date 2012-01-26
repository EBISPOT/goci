package uk.ac.ebi.fgpt.goci.model;

import org.semanticweb.owlapi.model.OWLClass;

/**
 * A model object that links a SNP to it's associated trait.  The p-value of this association as identified in the
 * source publication is present, and the trait is described here with an OWL class derived from <a
 * href="http://www.ebi.ac.uk/efo>EFO</a>
 *
 * @author Tony Burdett
 * @date 24/01/12
 */
public interface TraitAssociation {
    /**
     * Gets the raw ID of the study that identified this assocation
     *
     * @return as study ID that identified this association
     */
    String getStudyID();

    /**
     * Gets the SNP identified in this assocation
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
     * Gets the label declared in the GWAS catalog in cases where it could not be mapped to an ontology class.  If
     * {@link #getAssociatedTrait()} returns "Experimental Factor", this should return a value.
     *
     * @return the trait name as asserted in the underlying GWAS catalog data, probably taken directly from the source
     *         publication
     */
    String getUnmappedGWASLabel();
}
