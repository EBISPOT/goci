package uk.ac.ebi.fgpt.goci.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import uk.ac.ebi.fgpt.goci.dao.GociEvent;

/**
 * A basic implementation of a {@link GociStudy}
 *
 * @author Tony Burdett
 * @date 26/10/11
 */
public class UserCreatedGociStudy extends AbstractGociStudy {
    private String id;
    private String pubmedID;
    private String title;
    private String paperAbstract;
    private GociUser owner;
    private State state;
    private Eligibility eligibility;

    public UserCreatedGociStudy(String pubmedID,
                                String title,
                                String paperAbstract) {
        this(pubmedID, title, paperAbstract, null);
    }

    public UserCreatedGociStudy(String pubmedID,
                                String title,
                                String paperAbstract,
                                GociUser owner) {
        this.pubmedID = pubmedID;
        this.title = title;
        this.paperAbstract = paperAbstract;
        this.owner = owner;
        this.state = State.New_publication;
        this.eligibility = Eligibility.Unknown;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getPubMedID() {
        return pubmedID;
    }

    public String getTitle() {
        return title;
    }

    public String getPaperAbstract() {
        return paperAbstract;
    }

    public GociUser getOwner() {
        return owner;
    }

    public void setOwner(GociUser owner) {
        this.owner = owner;
        fireStudyUpdatedEvent(new GociEvent(this, GociEvent.Change.OWNER_UPDATE));
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        fireStudyUpdatedEvent(new GociEvent(this, GociEvent.Change.STATE_UPDATE));
    }

    public Eligibility getGwasEligibility() {
        return eligibility;
    }

    public void setGwasEligibility(Eligibility eligibility) {
        this.eligibility = eligibility;
        fireStudyUpdatedEvent(new GociEvent(this, GociEvent.Change.ELIGIBILITY_UPDATE));
    }
}
