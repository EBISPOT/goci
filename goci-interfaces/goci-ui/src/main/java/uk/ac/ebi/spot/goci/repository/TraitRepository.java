package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Trait;

/**
* Created by dwelter on 18/12/14.
*/
@RepositoryRestResource
public interface TraitRepository extends JpaRepository<Trait, Long> {
    Trait findByTrait(String trait);
}