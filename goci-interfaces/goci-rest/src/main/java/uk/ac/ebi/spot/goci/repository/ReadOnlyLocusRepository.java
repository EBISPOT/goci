package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Locus;

/**
 * Created by Dani on 16/11/16.
 */

@RepositoryRestResource(collectionResourceRel = "loci", path = "loci")
public interface ReadOnlyLocusRepository extends ReadOnlyRepository<Locus, Long> {
}
