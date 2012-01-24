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
     * Gets the SNP identified in this assocation
     *
     * @return the associated SNP
     */
    SingleNucleotidePolymorphism getAssociatedSNP();

    /**
     * Gets the EFO class that represents the trait identified in this association
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
}
