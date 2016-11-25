package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 28/11/14.
 *
 * @author emma
 *         <p>
 *         Model object representing ancestrality information attached to a study
 */

@Entity
public class Ancestry implements Trackable {
    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private Integer numberOfIndividuals;

    private String ancestralGroup;

    private String countryOfOrigin;

    private String countryOfRecruitment;

    private String description;

    // In database but not available in curation interface forms as no longer used
    private String previouslyReported;

    private String sampleSizesMatch;

    private String notes;

    @OneToOne
    private Study study;

    @OneToMany
    @JoinTable(name = "ANCESTRY_EVENT",
               joinColumns = @JoinColumn(name = "ANCESTRY_ID"),
               inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Collection<Event> events = new ArrayList<>();

    // JPA no-args constructor
    public Ancestry() {
    }

    public Ancestry(String countryOfOrigin,
                    String countryOfRecruitment,
                    String description,
                    String ancestralGroup,
                    Collection<Event> events,
                    String notes,
                    Integer numberOfIndividuals,
                    String previouslyReported, String sampleSizesMatch, Study study, String type) {
        this.countryOfOrigin = countryOfOrigin;
        this.countryOfRecruitment = countryOfRecruitment;
        this.description = description;
        this.ancestralGroup = ancestralGroup;
        this.events = events;
        this.notes = notes;
        this.numberOfIndividuals = numberOfIndividuals;
        this.previouslyReported = previouslyReported;
        this.sampleSizesMatch = sampleSizesMatch;
        this.study = study;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNumberOfIndividuals() {
        return numberOfIndividuals;
    }

    public void setNumberOfIndividuals(Integer numberOfIndividuals) {
        this.numberOfIndividuals = numberOfIndividuals;
    }

    public String getAncestralGroup() {
        return ancestralGroup;
    }

    public void setAncestralGroup(String ancestralGroup) {
        this.ancestralGroup = ancestralGroup;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getCountryOfRecruitment() {
        return countryOfRecruitment;
    }

    public void setCountryOfRecruitment(String countryOfRecruitment) {
        this.countryOfRecruitment = countryOfRecruitment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreviouslyReported() {
        return previouslyReported;
    }

    public void setPreviouslyReported(String previouslyReported) {
        this.previouslyReported = previouslyReported;
    }

    public String getSampleSizesMatch() {
        return sampleSizesMatch;
    }

    public void setSampleSizesMatch(String sampleSizesMatch) {
        this.sampleSizesMatch = sampleSizesMatch;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }

    @Override public void addEvent(Event event) {
        Collection<Event> currentEvents = getEvents();
        currentEvents.add(event);
        setEvents((currentEvents));
    }
}
