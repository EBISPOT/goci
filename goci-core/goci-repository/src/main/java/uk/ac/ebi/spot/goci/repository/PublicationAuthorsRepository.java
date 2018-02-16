package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.spot.goci.model.PublicationAuthors;

@RepositoryRestResource(exported = false)
public interface PublicationAuthorsRepository extends JpaRepository<PublicationAuthors, Long> {

    @RestResource(exported = false)
    PublicationAuthors findByAuthorIdAndPublicationIdAndSort(Long author_id, Long publication_id, Integer sort);

}
