package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Gene;

import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p/>
 *         Repository accessing Gene entity object
 */
@RepositoryRestResource
public interface GeneRepository extends JpaRepository<Gene, Long> {
   List<Gene> findByGeneNameIgnoreCase(String geneName);
}
