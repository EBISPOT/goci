package uk.ac.ebi.spot.goci.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Cinzia 11/2017
 *
 * @author Cinzia
 *         <p>
 *         Model representing Publication information stored about a study that is used during curation
 */
@Entity
public class Publication {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Please enter a pubmed id")
    private String pubmedId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Please enter a study date in format YYYY-MM-DD")
    private Date publicationDate;

    @NotBlank(message = "Please enter a publication")
    private String publication;

    @NotBlank(message = "Please enter a title")
    private String title;

    @OneToMany(mappedBy = "publicationId")
    private Collection<Study> studies;

    @OneToOne
    @JsonIgnore
    private Author firstAuthor;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", updatable = false)
    private Date createdAt;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED")
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

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "PUBLICATION_AUTHORS",
            joinColumns = @JoinColumn(name = "PUBLICATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "AUTHOR_ID"))
    private Collection<Author> authors = new ArrayList<>();

    // JPA no-args constructor
    public Publication() {
    }

    public Publication(String pubmedId, Date publicationDate, String publication, String title) {
        this.pubmedId = pubmedId;
        this.publicationDate = publicationDate;
        this.publication = publication;
        this.title = title;
    }

    public Long getId() { return id; }
    public void setId(Long id) {this.id = id;}

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<Author> getAuthors() { return authors; }

    public void setAuthors(Collection<Author> authors) { this.authors = authors; }

    public Collection<Study> getStudies() { return studies; }

    public void setStudies(Collection<Study> studies) { this.studies = studies; }

    public void setFirstAuthor(Author firstAuthor) { this.firstAuthor =firstAuthor; }

    public Author getFirstAuthor() { return firstAuthor; }

}
