package uk.ac.ebi.spot.goci.curation.service;

/**
 * Created by emma on 19/01/15.
 *
 * @author emma
 *         <p>
 *         Service class to deal with processing string containing country of recruitment value,
 *         these appear as separate tags in HTML view
 */
public class CountryOfRecruitment {

    private String[] recruitmentCountryValues;

    public CountryOfRecruitment() {
    }

    public CountryOfRecruitment(String[] recruitmentCountryValues) {
        this.recruitmentCountryValues = recruitmentCountryValues;
    }

    public String[] getRecruitmentCountryValues() {
        return recruitmentCountryValues;
    }

    public void setRecruitmentCountryValues(String[] recruitmentCountryValues) {
        this.recruitmentCountryValues = recruitmentCountryValues;
    }
}
