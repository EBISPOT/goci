package uk.ac.ebi.spot.goci.model;

/**
 * Created by Cinzia on 21/11/2017.
 */

public class PublicationResult extends SearchResult {

    private String facet = "study";

    private String pumedId;

    public String getPumedId() { return pumedId; }

    public void setPumedId(String pumedId) { this.pumedId = pumedId; }
}

