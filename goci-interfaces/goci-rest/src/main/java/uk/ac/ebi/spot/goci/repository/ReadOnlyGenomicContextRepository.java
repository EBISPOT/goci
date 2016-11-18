package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.GenomicContext;

/**
 * Created by Dani on 16/11/16.
 */
@RepositoryRestResource(collectionResourceRel = "genomicContexts", path = "genomicContexts")
public interface ReadOnlyGenomicContextRepository extends ReadOnlyRepository<GenomicContext, Long> {
}
