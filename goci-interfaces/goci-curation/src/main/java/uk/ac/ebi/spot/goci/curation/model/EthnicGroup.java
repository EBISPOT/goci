package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 03/02/15.
 *
 * @author emma Service class to deal with processing string containing ethnic group, these appear as separate tags in
 *         HTML view
 */
public class EthnicGroup {

    private String[] ethnicGroupValues;


    public EthnicGroup() {

    }

    public EthnicGroup(String[] ethnicGroupValues) {
        this.ethnicGroupValues = ethnicGroupValues;
    }

    public String[] getEthnicGroupValues() {
        return ethnicGroupValues;
    }

    public void setEthnicGroupValues(String[] ethnicGroupValues) {
        this.ethnicGroupValues = ethnicGroupValues;
    }
}
