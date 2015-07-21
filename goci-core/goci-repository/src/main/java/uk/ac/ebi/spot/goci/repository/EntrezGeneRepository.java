package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.goci.model.EntrezGene;

/**
 * Created by emma on 21/07/2015.
 *
 * @author emma
 *         <p>
 *         Repository accessing Entrez Gene entity object
 */
public interface EntrezGeneRepository extends JpaRepository<EntrezGene, Long> {

    EntrezGene findByEntrezGeneId(String entrezGeneId);
}
