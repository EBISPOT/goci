package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

/**
 * Created by Dani on 16/11/16.
 */

@RepositoryRestResource(collectionResourceRel = "singleNucleotidePolymorphisms", path = "singleNucleotidePolymorphisms")
public interface ReadOnlySingleNucleotidePolymorphismRepository extends ReadOnlyRepository<SingleNucleotidePolymorphism, Long> {
}
