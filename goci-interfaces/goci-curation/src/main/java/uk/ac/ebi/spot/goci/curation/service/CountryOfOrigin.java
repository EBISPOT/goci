package uk.ac.ebi.spot.goci.curation.service;

/**
 * Created by emma on 19/01/15.
 *
 * @author emma
 *         <p>
 *         Service class to deal with processing string containing country of origin value,
 *         these appear as separate tags in HTML view
 */
public class CountryOfOrigin {

    private String[] originCountryValues;

    public CountryOfOrigin() {

    }

    public CountryOfOrigin(String[] originCountryValues) {
        this.originCountryValues = originCountryValues;
    }

    public String[] getOriginCountryValues() {
        return originCountryValues;
    }

    public void setOriginCountryValues(String[] originCountryValues) {
        this.originCountryValues = originCountryValues;
    }
}