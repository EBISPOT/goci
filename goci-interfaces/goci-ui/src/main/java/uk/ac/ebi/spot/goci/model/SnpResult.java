package uk.ac.ebi.spot.goci.model;

/**
 * Created by Laurent on 19/08/2016.
 */
public class SnpResult extends SearchResult {
    private String facet = "association";

    private String rsId;

    public String getRsId() { return rsId; }

    public void setRsId(String rsId) {
        this.rsId = rsId;
    }



}
