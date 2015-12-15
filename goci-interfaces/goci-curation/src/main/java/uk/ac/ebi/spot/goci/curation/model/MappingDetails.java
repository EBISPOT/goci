package uk.ac.ebi.spot.goci.curation.model;

import java.util.Date;

/**
 * Created by emma on 25/09/2015.
 * <p>
 * Data transfer object to return mapping information to interface
 */
public class MappingDetails {

    private Date mappingDate;

    private String performer;

    // Constructor
    public MappingDetails() {
    }

    public Date getMappingDate() {
        return mappingDate;
    }

    public void setMappingDate(Date mappingDate) {
        this.mappingDate = mappingDate;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }
}
