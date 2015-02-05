package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 04/02/15.
 *
 * @author emma
 *         <p/>
 *         Model class that takes text entered as INITIAL_SAMPLE_SIZE and passes it between controller and view
 */
public class InitialSampleDescription {

    private String initialSampleDescription;


    public InitialSampleDescription() {
    }


    public InitialSampleDescription(String initialSampleDescription) {
        this.initialSampleDescription = initialSampleDescription;
    }

    public String getInitialSampleDescription() {
        return initialSampleDescription;
    }

    public void setInitialSampleDescription(String initialSampleDescription) {
        this.initialSampleDescription = initialSampleDescription;
    }
}
