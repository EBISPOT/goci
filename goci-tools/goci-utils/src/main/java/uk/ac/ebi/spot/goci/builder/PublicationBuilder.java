package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;
import java.util.Date;

/**
 * Created by cinzia on 17/10/2017.
 *
 * @author Cinzia
 *         <p>
 *         Publication builder used in testing
 */


public class PublicationBuilder {

    private Publication publication = new Publication();

    public PublicationBuilder setId(Long id) {
        publication.setId(id);
        return this;
    }

    public PublicationBuilder setPubmedId(String pubmedId) {
        publication.setPubmedId(pubmedId);
        return this;
    }

    public PublicationBuilder setFirstAuthor(Author author) {
        publication.setFirstAuthor(author);
        return this;
    }

    public PublicationBuilder setPublicationDate(Date publicationDate) {
        publication.setPublicationDate(publicationDate);
        return this;
    }

    public PublicationBuilder setPublication(String journal) {
        publication.setPublication(journal);
        return this;
    }

    public PublicationBuilder setTitle(String title) {
        publication.setTitle(title);
        return this;
    }

    public PublicationBuilder setStudies(Collection<Study> studies) {
        publication.setStudies(studies);
        return this;
    }

    public Publication build() {
        return publication;
    }
}
