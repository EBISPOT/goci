package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Gene;

/**
 * Created by Dani on 16/11/16.
 */
@RepositoryRestResource(collectionResourceRel = "genes", path = "genes")
public interface ReadOnlyGeneRepositiry extends ReadOnlyRepository<Gene, Long> {
}
