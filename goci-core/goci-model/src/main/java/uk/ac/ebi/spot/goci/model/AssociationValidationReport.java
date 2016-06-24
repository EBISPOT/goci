package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * Created by emma on 24/06/2016.
 *
 * @author emma
 *         <p>
 *         Model object to hold results of validating an association. An association can have both errors and warnings
 *         but errors must be resolved before saving thus are not stored in database
 */
@Entity
public class AssociationValidationReport {

    @Id
    @GeneratedValue
    private Long id;

    private String warning;

    private String validatedField;

    private Boolean errorCheckedByCurator = false;

    @ManyToOne
    private Association association;

    public AssociationValidationReport() {
    }

    public AssociationValidationReport(String warning,
                                       String validatedField,
                                       Boolean errorCheckedByCurator,
                                       Association association) {
        this.warning = warning;
        this.validatedField = validatedField;
        this.errorCheckedByCurator = errorCheckedByCurator;
        this.association = association;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getValidatedField() {
        return validatedField;
    }

    public void setValidatedField(String validatedField) {
        this.validatedField = validatedField;
    }

    public Boolean getErrorCheckedByCurator() {
        return errorCheckedByCurator;
    }

    public void setErrorCheckedByCurator(Boolean errorCheckedByCurator) {
        this.errorCheckedByCurator = errorCheckedByCurator;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }
}