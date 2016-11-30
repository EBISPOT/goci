package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Study;

/**
 * Created by emma on 26/05/2016.
 *
 * @author emma
 *         <p>
 *         Ancestrality builder used during testing
 */
public class AncestryBuilder {

    private Ancestry ancestry = new Ancestry();

    public AncestryBuilder setId(Long id) {
        ancestry.setId(id);
        return this;
    }

    public AncestryBuilder setType(String type) {
        ancestry.setType(type);
        return this;
    }

    public AncestryBuilder setNumberOfIndividuals(Integer numberOfIndividuals) {
        ancestry.setNumberOfIndividuals(numberOfIndividuals);
        return this;
    }

    public AncestryBuilder setAncestralGroup(String ancestralGroup) {
        ancestry.setAncestralGroup(ancestralGroup);
        return this;
    }

    public AncestryBuilder setCountryOfOrigin(String countryOfOrigin) {
        ancestry.setCountryOfOrigin(countryOfOrigin);
        return this;
    }

    public AncestryBuilder setCountryOfRecruitment(String countryOfRecruitment) {
        ancestry.setCountryOfRecruitment(countryOfRecruitment);
        return this;
    }

    public AncestryBuilder setDescription(String description) {
        ancestry.setDescription(description);
        return this;
    }

    public AncestryBuilder setPreviouslyReported(String previouslyReported) {
        ancestry.setPreviouslyReported(previouslyReported);
        return this;
    }

    public AncestryBuilder setSampleSizesMatch(String sampleSizesMatch) {
        ancestry.setSampleSizesMatch(sampleSizesMatch);
        return this;
    }

    public AncestryBuilder setNotes(String notes) {
        ancestry.setNotes(notes);
        return this;
    }

    public AncestryBuilder setStudy(Study study) {
        ancestry.setStudy(study);
        return this;
    }

    public Ancestry build() {
        return ancestry;
    }

}