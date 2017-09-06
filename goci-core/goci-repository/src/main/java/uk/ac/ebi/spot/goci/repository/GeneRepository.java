package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Gene;

import java.util.Collection;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Gene entity object
 */
@RepositoryRestResource(exported = false)
public interface GeneRepository extends JpaRepository<Gene, Long> {
    Gene findByGeneNameIgnoreCase(String geneName);

    Collection<Gene> findByAuthorReportedFromLociAssociationStudyId(Long studyId);

    Collection<Gene> findByGenomicContextsSnpRiskAllelesLociAssociationStudyId(Long studyId);

    Collection<Gene> findByAuthorReportedFromLociStrongestRiskAllelesSnpId(Long snpId);

    Collection<Gene> findByGenomicContextsSnpId(Long snpId);

    Collection<Gene> findByAuthorReportedFromLociAssociationId(Long associationId);

    Collection<Gene> findByGenomicContextsSnpRiskAllelesLociAssociationId(Long associationId);

    Collection<Gene> findByGenomicContextsSnpRiskAllelesLociAssociationStudyDiseaseTraitId(Long traitId);

    Collection<Gene> findByGenomicContextsSnpRiskAllelesLociAssociationEfoTraitsId(Long traitId);

    Gene findByEnsemblGeneIdsId(Long id);

    Gene findByEntrezGeneIdsId(Long id);

    Gene findByGeneName(String geneName);
}
