package uk.ac.ebi.spot.goci.curation.builder;


import uk.ac.ebi.spot.goci.model.CurationStatus;

/**
 * Created by emma on 15/02/2016.
 *
 * @author emma
 *         <p>
 *         Status builder used in testing
 */
public class CurationStatusBuilder {

    private CurationStatus curationStatus = new CurationStatus();

    public CurationStatusBuilder setId(Long id) {
        curationStatus.setId(id);
        return this;
    }

    public CurationStatusBuilder setStatus(String status) {
        curationStatus.setStatus(status);
        return this;
    }

    public CurationStatus build() {
        return curationStatus;
    }
}