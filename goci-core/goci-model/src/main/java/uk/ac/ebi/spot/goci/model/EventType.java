package uk.ac.ebi.spot.goci.model;

/**
 * Created by emma on 28/04/2016.
 *
 * @author emma
 *         <p>
 *         Enum class to hold various events the curation system should track
 */
public enum EventType {
    STUDY_CREATION,
    STUDY_STATUS_CHANGE,
    STUDY_CURATOR_ASSIGNMENT,
    STUDY_FILE_UPLOAD,
    STUDY_UPDATE,
    STUDY_DELETION,
    ASSOCIATION_CREATION,
    ASSOCIATION_UPDATE,
    ASSOCIATION_MAPPING,
    ASSOCIATION_APPROVED,
    ASSOCIATION_UNAPPROVED,
    ASSOCIATION_DELETION
}
