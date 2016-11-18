package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.RiskAllele;

/**
 * Created by Dani on 16/11/16.
 */

@RepositoryRestResource(collectionResourceRel = "riskAlleles", path = "riskAlleles")
public interface ReadOnlyRiskAlleleRepository extends ReadOnlyRepository<RiskAllele, Long> {
}
