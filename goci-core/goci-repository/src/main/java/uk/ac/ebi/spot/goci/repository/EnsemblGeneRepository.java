package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.EnsemblGene;

/**
 * Created by emma on 21/07/2015.
 *
 * @author emma
 *         <p>
 *         Repository accessing Ensembl Gene entity object
 */
@RepositoryRestResource(exported = false)
public interface EnsemblGeneRepository extends JpaRepository<EnsemblGene, Long> {

    EnsemblGene findByEnsemblGeneId(String ensemblGeneId);

}
