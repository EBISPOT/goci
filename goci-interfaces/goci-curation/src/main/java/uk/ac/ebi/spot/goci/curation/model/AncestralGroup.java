package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 03/02/15.
 *
 * @author emma Service class to deal with processing string containing ancestral group, these appear as separate tags in
 *         HTML view
 */
public class AncestralGroup {

    private String[] ancestralGroupValues;


    public AncestralGroup() {

    }

    public AncestralGroup(String[] ancestralGroupValues) {
        this.ancestralGroupValues = ancestralGroupValues;
    }

    public String[] getAncestralGroupValues() {
        return ancestralGroupValues;
    }

    public void setAncestralGroupValues(String[] ancestralGroupValues) {
        this.ancestralGroupValues = ancestralGroupValues;
    }
}
