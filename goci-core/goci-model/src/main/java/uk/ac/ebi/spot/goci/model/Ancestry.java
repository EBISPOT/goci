package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

//    private String ancestralGroup;

//    private String countryOfOrigin;

//    private String countryOfRecruitment;

    @JsonIgnore
    private String description;

    // In database but not available in curation interface forms as no longer used
    @JsonIgnore
    private String previouslyReported;

    @JsonIgnore
    private String sampleSizesMatch;

    @JsonIgnore
    private String notes;

    @RestResource(exported = false)
    @OneToOne
    @JsonBackReference
    private Study study;

    @ManyToMany
    @JoinTable(name = "ANCESTRY_ANCESTRAL_GROUP",
               joinColumns = @JoinColumn(name = "ANCESTRY_ID"),
               inverseJoinColumns = @JoinColumn(name = "ANCESTRAL_GROUP_ID"))
    @JsonManagedReference
    private Collection<AncestralGroup> ancestralGroups;

    @ManyToMany
    @JoinTable(name = "ANCESTRY_COUNTRY_OF_ORIGIN",
               joinColumns = @JoinColumn(name = "ANCESTRY_ID"),
               inverseJoinColumns = @JoinColumn(name = "COUNTRY_ID"))
    @JsonManagedReference
    private Collection<Country> countryOfOrigin;

    @ManyToMany
    @JoinTable(name = "ANCESTRY_COUNTRY_RECRUITMENT",
               joinColumns = @JoinColumn(name = "ANCESTRY_ID"),
               inverseJoinColumns = @JoinColumn(name = "COUNTRY_ID"))
    @JsonManagedReference
    private Collection<Country> countryOfRecruitment;


    @JsonIgnore
    @OneToMany
    @JoinTable(name = "ANCESTRY_EVENT",
               joinColumns = @JoinColumn(name = "ANCESTRY_ID"),
               inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Collection<Event> events = new ArrayList<>();

    // JPA no-args constructor
    public Ancestry() {
    }

    public Ancestry(Collection<Country> countryOfOrigin,
                    Collection<Country> countryOfRecruitment,
                    String description,
//                    String ancestralGroup,
                    Collection<Event> events,
                    String notes,
                    Integer numberOfIndividuals,
                    String previouslyReported, String sampleSizesMatch, Study study, String type,
                    Collection<AncestralGroup> ancestralGroups) {
        this.countryOfOrigin = countryOfOrigin;
        this.countryOfRecruitment = countryOfRecruitment;
        this.description = description;
//        this.ancestralGroup = ancestralGroup;
        this.events = events;
        this.notes = notes;
        this.numberOfIndividuals = numberOfIndividuals;
        this.previouslyReported = previouslyReported;
        this.sampleSizesMatch = sampleSizesMatch;
        this.study = study;
        this.type = type;
        this.ancestralGroups = ancestralGroups;
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

//    public String getAncestralGroup() {
//        return ancestralGroup;
//    }
//
//    public void setAncestralGroup(String ancestralGroup) {
//        this.ancestralGroup = ancestralGroup;
//    }

    public Collection<Country> getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(Collection<Country> countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public Collection<Country> getCountryOfRecruitment() {
        return countryOfRecruitment;
    }

    public void setCountryOfRecruitment(Collection<Country> countryOfRecruitment) {
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

    public Collection<AncestralGroup> getAncestralGroups() {
        return ancestralGroups;
    }

    public void setAncestralGroups(Collection<AncestralGroup> ancestralGroups) {
        this.ancestralGroups = ancestralGroups;
    }
}
