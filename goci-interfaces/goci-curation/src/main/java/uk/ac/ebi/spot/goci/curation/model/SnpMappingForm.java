package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.Location;

/**
 * Created by Laurent on 21/05/15.
 */
public class SnpMappingForm {

    private String snp;

    private Location location;

    public SnpMappingForm() {
    }

    public SnpMappingForm(String snp, Location location) {
        this.snp = snp;
        this.location = location;
    }

    public String getSnp() {
        return snp;
    }

    public void setSnp(String snp) {
        this.snp = snp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
