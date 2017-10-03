package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.goci.model.Publication;

import java.util.Collection;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    @RestResource(exported = false)
    Publication findByPubmedId(String pubmedId);
}
