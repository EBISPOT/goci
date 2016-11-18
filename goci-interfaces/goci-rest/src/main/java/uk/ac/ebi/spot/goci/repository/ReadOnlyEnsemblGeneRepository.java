package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.EnsemblGene;

/**
 * Created by Dani on 16/11/16.
 */
@RepositoryRestResource(collectionResourceRel = "ensemblGenes", path = "ensemblGenes")
public interface ReadOnlyEnsemblGeneRepository extends ReadOnlyRepository<EnsemblGene, Long> {
}
