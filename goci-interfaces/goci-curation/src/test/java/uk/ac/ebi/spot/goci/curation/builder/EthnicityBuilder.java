package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.Study;

/**
 * Created by emma on 26/05/2016.
 *
 * @author emma
 *         <p>
 *         Ethnicity builder used during testing
 */
public class EthnicityBuilder {

    private Ethnicity ethnicity = new Ethnicity();

    public EthnicityBuilder setId(Long id) {
        ethnicity.setId(id);
        return this;
    }

    public EthnicityBuilder setType(String type) {
        ethnicity.setType(type);
        return this;
    }

    public EthnicityBuilder setNumberOfIndividuals(Integer numberOfIndividuals) {
        ethnicity.setNumberOfIndividuals(numberOfIndividuals);
        return this;
    }

    public EthnicityBuilder setEthnicGroup(String ethnicGroup) {
        ethnicity.setEthnicGroup(ethnicGroup);
        return this;
    }

    public EthnicityBuilder setCountryOfOrigin(String countryOfOrigin) {
        ethnicity.setCountryOfOrigin(countryOfOrigin);
        return this;
    }

    public EthnicityBuilder setCountryOfRecruitment(String countryOfRecruitment) {
        ethnicity.setCountryOfRecruitment(countryOfRecruitment);
        return this;
    }

    public EthnicityBuilder setDescription(String description) {
        ethnicity.setDescription(description);
        return this;
    }

    public EthnicityBuilder setPreviouslyReported(String previouslyReported) {
        ethnicity.setPreviouslyReported(previouslyReported);
        return this;
    }

    public EthnicityBuilder setSampleSizesMatch(String sampleSizesMatch) {
        ethnicity.setSampleSizesMatch(sampleSizesMatch);
        return this;
    }

    public EthnicityBuilder setNotes(String notes) {
        ethnicity.setNotes(notes);
        return this;
    }

    public EthnicityBuilder setStudy(Study study) {
        ethnicity.setStudy(study);
        return this;
    }

    public Ethnicity build() {
        return ethnicity;
    }

}