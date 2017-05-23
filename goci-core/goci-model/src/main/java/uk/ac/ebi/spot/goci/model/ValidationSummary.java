package uk.ac.ebi.spot.goci.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 20/04/2016.
 *
 * @author emma
 *         <p>
 *         A class used to represent a summary of a validated submission and its errors
 */
public class ValidationSummary {

    private Collection<AssociationSummary> associationSummaries = new ArrayList<>();

    private Collection<RowValidationSummary> rowValidationSummaries = new ArrayList<>();

    public Collection<AssociationSummary> getAssociationSummaries() {
        return associationSummaries;
    }

    public void setAssociationSummaries(Collection<AssociationSummary> associationSummaries) {
        this.associationSummaries = associationSummaries;
    }

    public Collection<RowValidationSummary> getRowValidationSummaries() {
        return rowValidationSummaries;
    }

    public void setRowValidationSummaries(Collection<RowValidationSummary> rowValidationSummaries) {
        this.rowValidationSummaries = rowValidationSummaries;
    }
}
