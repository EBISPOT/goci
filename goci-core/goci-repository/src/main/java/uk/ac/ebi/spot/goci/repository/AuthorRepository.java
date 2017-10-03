package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.goci.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @RestResource(exported = false)
    Author findByFullname(String fullname);
}

