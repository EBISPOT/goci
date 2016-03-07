package uk.ac.ebi.spot.goci.curation.service;

import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.model.Association;

/**
 * Created by emma on 26/02/2016.
 *
 * @author emma
 *         <p>
 *         A component that takes a form containing association details and creates an associationobject or vice versa
 */
public interface SnpAssociationFormService {

    /**
     * Create form from association
     *
     * @return Form, which is presented via controller to view
     */
    SnpAssociationForm createForm(Association association);

    /**
     * Set common association attributes
     *
     * @return Form, contains details of association to create
     */
    default Association setCommonAssociationElements(SnpAssociationForm form) {

        Association association = new Association();

        // Set common string, boolean and float association attributes
        association.setPvalueText(form.getPvalueText());
        association.setSnpType(form.getSnpType());
        association.setSnpApproved(form.getSnpApproved());
        association.setRange(form.getRange());
        association.setStandardError(form.getStandardError());
        association.setDescription(form.getDescription());
        association.setRiskFrequency(form.getRiskFrequency());

        // Add collection of EFO traits
        association.setEfoTraits(form.getEfoTraits());

        // Set mantissa and exponent
        association.setPvalueMantissa(form.getPvalueMantissa());
        association.setPvalueExponent(form.getPvalueExponent());

        // Set OR/Beta values
        association.setOrPerCopyNum(form.getOrPerCopyNum());
        association.setOrPerCopyRecip(form.getOrPerCopyRecip());
        association.setOrPerCopyRecipRange(form.getOrPerCopyRecipRange());
        association.setBetaNum(form.getBetaNum());
        association.setBetaDirection(form.getBetaDirection());
        association.setBetaUnit(form.getBetaUnit());
        return association;
    }
}