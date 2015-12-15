package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 22/10/2015.
 *
 * @author emma
 *         <p>
 *         DTO used to store the status that user wishes to assign a study to. This is used in the StudyController and
 *         the studies.html file as a thymeleaf object.
 */
public class StatusAssignment {

    private Long statusId;

    private String uri;

    public StatusAssignment() {
    }

    public StatusAssignment(Long statusId, String uri) {
        this.statusId = statusId;
        this.uri = uri;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
