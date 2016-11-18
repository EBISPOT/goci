package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.EfoTrait;

/**
 * Created by Dani on 16/11/16.
 */
@RepositoryRestResource(collectionResourceRel = "efoTraits", path = "efoTraits")
public interface ReadOnlyEfoTraitRepository extends ReadOnlyRepository<EfoTrait, Long> {
}
