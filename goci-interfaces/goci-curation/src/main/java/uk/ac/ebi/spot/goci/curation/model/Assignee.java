package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 21/10/2015.
 *
 * @author emma
 *         <p>
 *         DTO used to store the curator that user wishes to assign a study to. This is used in the
 *         StudyController and the studies.html file as a thymeleaf objec.
 */
public class Assignee {

    private Long curatorId;

    public Assignee() {
    }

    public Assignee(Long curatorId) {
        this.curatorId = curatorId;
    }

    public Long getCuratorId() {
        return curatorId;
    }

    public void setCuratorId(Long curatorId) {
        this.curatorId = curatorId;
    }
}
