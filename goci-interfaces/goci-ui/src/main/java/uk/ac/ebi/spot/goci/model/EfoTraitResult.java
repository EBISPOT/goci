package uk.ac.ebi.spot.goci.model;

/**
 * Created by xinhe on 21/04/2017.
 */
public class EfoTraitResult extends SearchResult{

//    private String facet = "trait";

    private String efoId;
    private String included;
    private String checked;

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

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }
}
