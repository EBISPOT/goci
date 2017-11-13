package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.PublicationAuthors;

@RepositoryRestResource
public interface PublicationAuthorsRepository extends JpaRepository<PublicationAuthors, Long> {

    PublicationAuthors findByAuthorIdAndPublicationId(Long author_id, Long publication_id);

}
