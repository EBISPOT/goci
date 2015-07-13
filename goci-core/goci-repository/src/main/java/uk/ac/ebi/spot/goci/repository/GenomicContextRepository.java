package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.GenomicContext;

import java.util.Collection;

/**
 * Created by Laurent on 12/05/15.
 * <p>
 * author lgil
 * <p>
 * Repository accessing GenomicContext entity object
 */
@RepositoryRestResource
public interface GenomicContextRepository extends JpaRepository<GenomicContext, Long> {

    Collection<GenomicContext> findBySnpId(Long snpId);

    Collection<GenomicContext> findByIsIntergenicAndIsUpstreamAndIsDownstreamAndDistanceAndGeneGeneNameAndSourceAndMappingMethodAndSnpId(
            Boolean isIntergenic,
            Boolean isUpstream,
            Boolean isDownstream,
            Long distance, String geneName, String source, String mappingMethod, Long snpId);

}
