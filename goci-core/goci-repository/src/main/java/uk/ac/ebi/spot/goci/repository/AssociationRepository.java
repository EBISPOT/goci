package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;
import java.util.List;


/**
 * Created by emma on 26/11/14.
 * @author emma
 *
 * Repository accessing Association entity object
 */
@RepositoryRestResource
public interface AssociationRepository extends JpaRepository<Association, Long> {
    Collection<Association> findByStudyId(long studyId);

    Collection<Association> findByLociStrongestRiskAllelesSnpId(long snpId);

    List<Association> findByStudyHousekeepingPublishDateIsNotNull();

    List<Association> findByStudyHousekeepingPublishDateIsNotNull(Sort sort);

    Page<Association> findByStudyHousekeepingPublishDateIsNotNull(Pageable pageable);

    List<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingPublishDateIsNotNull(Long snpId);

    List<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingPublishDateIsNotNull(Sort sort, Long snpId);

    Page<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingPublishDateIsNotNull(Pageable pageable, Long snpId);

    List<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingPublishDateIsNotNull(Long diseaseTraitId);

    List<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingPublishDateIsNotNull(Sort sort, Long diseaseTraitId);

    Page<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingPublishDateIsNotNull(Pageable pageable, Long diseaseTraitId);
}
