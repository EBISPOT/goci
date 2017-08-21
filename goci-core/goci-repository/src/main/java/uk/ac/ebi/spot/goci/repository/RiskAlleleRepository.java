package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.RiskAllele;

import java.util.List;

/**
 * Created by emma on 27/01/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing RiskAllele entity object
 */
@RepositoryRestResource(exported = false)
public interface RiskAlleleRepository extends JpaRepository<RiskAllele, Long> {
    List<RiskAllele> findByRiskAlleleName(String riskAlleleName);
}
