package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.AncestralGroup;

/**
 * Created by Dani on 13/04/2017.
 */

@RepositoryRestResource(exported = false)
public interface AncestralGroupRepository extends JpaRepository<AncestralGroup, Long> {

    AncestralGroup findByAncestralGroup(String ancestralGroup);
}
