package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.curation.model.Assignee;

/**
 * Created by emma on 25/05/2016.
 *
 * @author emma
 *         <p>
 *         Assignee builder
 */
public class AssigneeBuilder {

    private Assignee assignee = new Assignee();

    public AssigneeBuilder setCuratorId(Long curatorId) {
        assignee.setCuratorId(curatorId);
        return this;
    }

    public AssigneeBuilder setUri(String uri) {
        assignee.setUri(uri);
        return this;
    }

    public Assignee build() {
        return assignee;
    }
}
