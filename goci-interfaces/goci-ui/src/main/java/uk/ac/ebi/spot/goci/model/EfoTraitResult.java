package uk.ac.ebi.spot.goci.model;

/**
 * Created by xinhe on 21/04/2017.
 */
public class EfoTraitResult extends SearchResult{

//    private String facet = "trait";

    private String efoId;
    private String included;

    public String getIncluded() {
        return included;
    }

    public void setIncluded(String included) {
        this.included = included;
    }

    public String getEfoId() {
        return efoId;
    }

    public void setEfoId(String efoId) {
        this.efoId = efoId;
    }
}
