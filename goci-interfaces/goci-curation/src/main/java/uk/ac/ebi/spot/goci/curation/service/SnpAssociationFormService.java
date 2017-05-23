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
        association.setSnpType(form.getSnpType());
        association.setSnpApproved(form.getSnpApproved());
        association.setStandardError(form.getStandardError());

        // Add collection of EFO traits
        association.setEfoTraits(form.getEfoTraits());

        // Set mantissa and exponent
        association.setPvalueMantissa(form.getPvalueMantissa());
        association.setPvalueExponent(form.getPvalueExponent());

        // Set OR/Beta values
        association.setOrPerCopyNum(form.getOrPerCopyNum());
        association.setOrPerCopyRecip(form.getOrPerCopyRecip());
        association.setBetaNum(form.getBetaNum());
        association.setBetaDirection(form.getBetaDirection());

        // Tidy up common string values
        if (form.getPvalueDescription() != null) {
            association.setPvalueDescription(form.getPvalueDescription().trim());
        }

        if (form.getRange() != null) {
            association.setRange(form.getRange().trim());
        }

        if (form.getDescription() != null) {
            association.setDescription(form.getDescription().trim());
        }

        if (form.getRiskFrequency() != null) {
            association.setRiskFrequency(form.getRiskFrequency().trim());
        }
        if (form.getOrPerCopyRecipRange() != null) {
            association.setOrPerCopyRecipRange(form.getOrPerCopyRecipRange().trim());
        }
        if (form.getBetaUnit() != null) {
            association.setBetaUnit(form.getBetaUnit().trim());
        }
        return association;
    }
}