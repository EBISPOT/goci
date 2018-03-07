package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by cinzia on 22/09/2017.
 */
@Entity
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Fullname is missing")
    private String fullname;

    //Converted fullname with standard char
    @JsonIgnore
    private String fullnameStandard;

    @JsonIgnore
    private String firstName;

    @JsonIgnore
    private String lastName;

    @JsonIgnore
    private String initials;

    @JsonIgnore
    private String affiliation;

    private String orcid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", updatable = false)
    @JsonIgnore
    private Date createdAt;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED")
    @JsonIgnore
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }


    @OneToMany
    @JoinTable(name = "PUBLICATION_AUTHORS",
            joinColumns = @JoinColumn(name = "AUTHOR_ID"),
            inverseJoinColumns = @JoinColumn(name = "PUBLICATION_ID"))
    @JsonIgnore
    private Collection<Publication> publications = new ArrayList<>();


    //JPA Constructor
    public Author() {}

    public Author(String fullname, String fullnameStandard, String lastName, String firstName, String initials,String orcid) {
        this.fullname = fullname;
        this.fullnameStandard = fullnameStandard;
        this.lastName = lastName;
        this.firstName = firstName;
        this.initials = initials;
        this.orcid = orcid;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public Collection<Publication> getPublications() { return publications; }

    public void setPublication(Collection<Publication> publications) { this.publications = publications; }

    // Use Optional for Default value.

    public String getFullnameShort(int  lenghtFullname) {
        return fullname.substring(0, Math.min(fullname.length(), lenghtFullname));
    }

    public void setFullnameStandart(String fullnameStandard) { this.fullnameStandard = fullnameStandard; }

    public String getFullnameStandard() { return this.fullnameStandard; }

    public void setFullnameStandard(String fullnameStandard) { this.fullnameStandard = fullnameStandard; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName;    }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getInitials() { return initials; }

    public void setInitials(String initials) { this.initials = initials; }

    public String getAffiliation() { return affiliation; }

    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }
}
