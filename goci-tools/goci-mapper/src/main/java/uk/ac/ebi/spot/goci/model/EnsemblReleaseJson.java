package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by emma on 21/01/2016.
 * @author emma
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnsemblReleaseJson {

    private  int[] releases;

    public EnsemblReleaseJson() {

    }

    public int[] getReleases() {
        return releases;
    }

    public void setReleases(int[] releases) {
        this.releases = releases;
    }
}
