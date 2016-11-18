package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;

/**
 * Created by Dani on 16/11/16.
 */
@RepositoryRestResource(collectionResourceRel = "diseaseTraits", path = "diseaseTraits")
public interface ReadOnlyDiseaseTraitRepository extends ReadOnlyRepository<DiseaseTrait, Long> {
}
