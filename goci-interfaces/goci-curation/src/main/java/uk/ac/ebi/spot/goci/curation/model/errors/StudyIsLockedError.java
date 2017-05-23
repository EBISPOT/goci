package uk.ac.ebi.spot.goci.curation.model.errors;

/**
 * Created by xinhe on 12/04/2017.
 */
public class StudyIsLockedError extends Error {

    public StudyIsLockedError() {
        super("Published Study is locked.");
    }

    public StudyIsLockedError(Long studyId) {
        super("Published study " + studyId + " is locked.");
    }
}
