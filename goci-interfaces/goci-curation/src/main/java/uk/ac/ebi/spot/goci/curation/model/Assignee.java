package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 21/10/2015.
 *
 * @author emma
 *         <p>
 *         DTO used to store the curator that user wishes to assign a study to. This is used in the
 *         StudyController and the studies.html file as a thymeleaf object.
 */
public class Assignee {

    private Long curatorId;

    private String uri;

    public Assignee() {
    }

    public Assignee(Long curatorId, String uri) {
        this.curatorId = curatorId;
        this.uri = uri;
    }

    public Long getCuratorId() {
        return curatorId;
    }

    public void setCuratorId(Long curatorId) {
        this.curatorId = curatorId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
