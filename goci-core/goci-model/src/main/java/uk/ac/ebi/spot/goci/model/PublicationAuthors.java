package uk.ac.ebi.spot.goci.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Cinzia 11/2017
 *
 * @author Cinzia
 *         <p>
 *         Model representing Publication_Authors information stored about a study that is used during curation
 */



//@Embeddable
@Entity
public class PublicationAuthors {
    @EmbeddedId
    protected PublicationAuthorsPk publicationAuthorsPk;

    @ManyToOne
    @JoinColumn(name = "publication_id", insertable = false, updatable = false)
    private Publication publication;

    @ManyToOne
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private Author author;

    private Integer sort;

    @Column(name = "sort")
    public Integer getSort() {
        return sort;
    }
    // Constructor JPA
    public PublicationAuthors() {}

    public PublicationAuthors(Author author, Publication publication, Integer sort) {
        this.sort = sort;
        this.author = author;
        this.publication = publication;
        PublicationAuthorsPk primaryKey = new PublicationAuthorsPk(author.getId(), publication.getId());
        publicationAuthorsPk = primaryKey;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

}


@Embeddable
class PublicationAuthorsPk implements Serializable {

    @Column(name = "publication_id")
    protected Long publicationId;

    @Column(name = "author_id")
    protected Long authorId;

    public PublicationAuthorsPk() {}

    public PublicationAuthorsPk(Long authorId, Long publication_id) {
        this.authorId = authorId;
        this.publicationId = publication_id;
    }

    public Long getPublicationId() { return publicationId; }

    public void setPublicationId(Long publicationId) { this.publicationId = publicationId; }

    public Long getAuthorId() { return authorId;}

    public void setAuthorId(Long authorId) { this.authorId = authorId; }

}
