package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.EntrezGene;

/**
 * Created by Dani on 16/11/16.
 */
@RepositoryRestResource(collectionResourceRel = "entrezGenes", path = "entrezGenes")
public interface ReadOnlyEntrezGeneRepository extends ReadOnlyRepository<EntrezGene, Long> {
}
