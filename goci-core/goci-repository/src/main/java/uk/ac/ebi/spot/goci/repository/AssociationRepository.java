package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Association;

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

    Collection<Association> findByStudyId(long studyId, Sort sort);

    Collection<Association> findByLociStrongestRiskAllelesSnpId(long snpId);

    List<Association> findByStudyHousekeepingCatalogPublishDateIsNotNull();

    List<Association> findByStudyHousekeepingCatalogPublishDateIsNotNull(Sort sort);

    Page<Association> findByStudyHousekeepingCatalogPublishDateIsNotNull(Pageable pageable);

    List<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingCatalogPublishDateIsNotNull(Long snpId);

    List<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingCatalogPublishDateIsNotNull(Sort sort, Long snpId);

    Page<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingCatalogPublishDateIsNotNull(Pageable pageable, Long snpId);

    List<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingCatalogPublishDateIsNotNull(Long diseaseTraitId);

    List<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingCatalogPublishDateIsNotNull(Sort sort, Long diseaseTraitId);

    Page<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingCatalogPublishDateIsNotNull(Pageable pageable, Long diseaseTraitId);

    List<Association> findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNull(Long efoTraitId);

    List<Association> findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNull(Sort sort, Long efoTraitId);

    List<Association> findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNull(Pageable pageable, Long efoTraitId);
}
