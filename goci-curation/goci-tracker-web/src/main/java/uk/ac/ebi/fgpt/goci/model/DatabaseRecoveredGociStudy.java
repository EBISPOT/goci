package uk.ac.ebi.fgpt.goci.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import uk.ac.ebi.fgpt.goci.dao.GociEvent;

/**
 * A basic implementation of a GociStudy for studies recovered from the database.  Getters and setters are available for
 * all fields to enable a DAO to set these values.
 *
 * @author Tony Burdett
 * Date 27/10/11
 */
public class DatabaseRecoveredGociStudy extends AbstractGociStudy {
    private String id;
    private String pubmedID;
    private String title;
    private String paperAbstract;
    private GociUser owner;
    private GociStudy.State state;
    private GociStudy.Eligibility eligibility;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getPubMedID() {
        return pubmedID;
    }

    public void setPubMedID(String pubmedID) {
        this.pubmedID = pubmedID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPaperAbstract() {
        return paperAbstract;
    }

    public void setPaperAbstract(String paperAbstract) {
        this.paperAbstract = paperAbstract;
    }

    public GociUser getOwner() {
        return owner;
    }

    public void setOwner(GociUser owner) {
        this.owner = owner;
        fireStudyUpdatedEvent(new GociEvent(this, GociEvent.Change.OWNER_UPDATE));
    }

    public GociStudy.State getState() {
        return state;
    }

    public void setState(GociStudy.State state) {
        this.state = state;
        fireStudyUpdatedEvent(new GociEvent(this, GociEvent.Change.STATE_UPDATE));
    }

    public GociStudy.Eligibility getGwasEligibility() {
        return eligibility;
    }

    public void setGwasEligibility(GociStudy.Eligibility eligibility) {
        this.eligibility = eligibility;
        fireStudyUpdatedEvent(new GociEvent(this, GociEvent.Change.ELIGIBILITY_UPDATE));
    }
}
