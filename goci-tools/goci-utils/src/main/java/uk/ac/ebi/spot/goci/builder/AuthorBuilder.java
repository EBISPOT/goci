package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;

import java.util.Collection;

/**
 * Created by cinzia on 17/10/2017.
 *
 * @author Cinzia
 *         <p>
 *         Author builder used in testing
 */

public class AuthorBuilder {

    private Author author = new Author();

    public AuthorBuilder setId(Long id) {
        author.setId(id);
        return this;
    }

    public AuthorBuilder setPublication(Collection<Publication> publication) {
        author.setPublication(publication);
        return this;
    }

    public AuthorBuilder setOrcid(String orcid) {
        author.setOrcid(orcid);
        return this;
    }

    public AuthorBuilder setFullname(String fullname) {
        author.setFullname(fullname);
        return this;
    }

    public Author build() { return author;}
}
