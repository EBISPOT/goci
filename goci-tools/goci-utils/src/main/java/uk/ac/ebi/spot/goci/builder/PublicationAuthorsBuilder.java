package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.model.PublicationAuthors;

/**
 * Created by cinzia on 22/02/2018.
 *
 * @author Cinzia
 *         <p>
 *         PublicationAuthors builder used in testing
 */

public class PublicationAuthorsBuilder {
    PublicationAuthors publicationAuthors = new PublicationAuthors();

    public PublicationAuthors build() {
        return publicationAuthors;
    }

    public PublicationAuthorsBuilder setPublication(Publication publication) {
        publicationAuthors.setPublication(publication);
        return this;
    }

    public PublicationAuthorsBuilder setAuthor(Author author) {
        publicationAuthors.setAuthor(author);
        return this;
    }

    public PublicationAuthorsBuilder setSort(Integer sort) {
        publicationAuthors.setSort(sort);
        return this;
    }



}
