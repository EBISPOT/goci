package uk.ac.ebi.spot.goci.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@IdClass(MyKey.class)
public class PublicationAuthors implements Serializable {

    @Id
    @Column(name = "publication_id", nullable = false)
    private Long publicationId;

    @Id
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    private Integer sort;

    @Column(name = "sort")
    public Integer getSort() {
        return sort;
    }
    // Constructor JPA
    public PublicationAuthors() {}

    public PublicationAuthors(Long authorId, Long publicationId, Integer sort) {
        this.sort = sort;
        this.authorId = authorId;
        this.publicationId = publicationId;
    }

    public Long getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(Long publicationId) {
        this.publicationId = publicationId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

}


class MyKey implements Serializable {
    private Long publicationId;

    private Long authorId;
}