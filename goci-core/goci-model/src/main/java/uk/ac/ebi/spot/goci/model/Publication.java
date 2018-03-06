package uk.ac.ebi.spot.goci.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

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
    @JsonFormat(pattern="YYYY-MM-dd")
    private Date publicationDate;

    @NotBlank(message = "Please enter a publication")
    private String publication;

    @NotBlank(message = "Please enter a title")
    private String title;

    @OneToMany(mappedBy = "publicationId")
    @JsonIgnore
    private Collection<Study> studies;

    @OneToOne
    @JsonProperty("author")
    private Author firstAuthor;

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

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "PUBLICATION_AUTHORS",
            joinColumns = @JoinColumn(name = "PUBLICATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "AUTHOR_ID"))
    @JsonIgnore
    private Collection<Author> authors = new ArrayList<>();


    @OneToMany(mappedBy="publication",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PublicationAuthors> publicationAuthors;

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

    @JsonProperty("author")
    public void setFirstAuthor(Author firstAuthor) { this.firstAuthor =firstAuthor; }

    @JsonProperty("author")
    public Author getFirstAuthor() { return firstAuthor; }

    public List<PublicationAuthors> getPublicationAuthors() {
        Collections.sort(publicationAuthors, new Comparator<PublicationAuthors>(){
            public int compare(PublicationAuthors author1, PublicationAuthors author2){
                return author1.getSort() - author2.getSort();
            }
        });

        return publicationAuthors;
    }

    public void setPublicationAuthors(List<PublicationAuthors> publicationAuthors) {
        this.publicationAuthors = publicationAuthors;
    }
}
