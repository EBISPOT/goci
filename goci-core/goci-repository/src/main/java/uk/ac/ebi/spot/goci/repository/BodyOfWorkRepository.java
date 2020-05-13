package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.BodyOfWork;

@RepositoryRestResource(exported = false)
public interface BodyOfWorkRepository extends JpaRepository<BodyOfWork, Long> {
    BodyOfWork findByPublicationId(String publicationId);
}
