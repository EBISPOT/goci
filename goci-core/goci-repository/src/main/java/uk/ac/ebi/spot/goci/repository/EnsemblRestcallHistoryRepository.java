package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.EnsemblRestcallHistory;

import java.util.Collection;

/**
 * Created by Cinzia on 30/01/2017.
 *
 * @author cinzia
 *         <p>
 *         Repository accessing EnsemblRestCallHistory entity object
 *         This table stores the Ensembl requests and the relative responses.
 */

@RepositoryRestResource(exported = false)
public interface EnsemblRestcallHistoryRepository extends JpaRepository<EnsemblRestcallHistory, Long> {

    Collection<EnsemblRestcallHistory> findByRequestTypeAndEnsemblParamAndEnsemblVersion(String requestType, String ensemblParam, String ensemblVersion);

}
