package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.spot.goci.model.Association;

import java.util.Collection;
import java.util.List;


/**
 * Created by emma on 26/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Association entity object
 */
@RepositoryRestResource
public interface AssociationRepository extends JpaRepository<Association, Long> {
    @RestResource(exported = false)
    Collection<Association> findByStudyId(long studyId);

    @RestResource(exported = false)
    Page<Association> findByStudyId(long studyId, Pageable pageable);

    Collection<Association> findByStudyAccessionId(String accessionId);

    @RestResource(path = "findByPubmedId", rel = "findByPubmedId")
    Collection<Association> findByStudyPublicationIdPubmedId(String pubmedId);

    @RestResource(exported = false)
    Collection<Association> findByStudyId(long studyId, Sort sort);

    @RestResource(exported = false)
    List<Association> findByStudyIdAndLastUpdateDateIsNotNullOrderByLastUpdateDateDesc(Long studyId);

    @RestResource(exported = false)
    Collection<Association> findByLociStrongestRiskAllelesSnpId(long snpId);

    @RestResource(exported = false)
    List<Association> findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull();

    @RestResource(exported = false)
    List<Association> findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Sort sort);

    @RestResource(exported = false)
    Page<Association> findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable);

    @RestResource(exported = false)
    List<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Long snpId);

    @RestResource(exported = false)
    List<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long snpId);

    @RestResource(exported = false)
    Page<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long snpId);

    @RestResource(exported = false)
    List<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Long diseaseTraitId);

    @RestResource(exported = false)
    List<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long diseaseTraitId);

    @RestResource(exported = false)
    Page<Association> findByStudyDiseaseTraitIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long diseaseTraitId);

    @RestResource(exported = false)
    List<Association> findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Long efoTraitId);

    @RestResource(exported = false)
    List<Association> findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long efoTraitId);

    @RestResource(exported = false)
    Page<Association> findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long efoTraitId);

    @RestResource(exported = false)
    @Query(value = "select * from "+
            "(select a.*, rownum rnum from (select * from association order by id) a where rownum <= :maxRow) "+
            " where rnum >= :minRow ",
            nativeQuery = true)
    Collection<Association> findAllLSF(@Param("minRow") Integer minRow, @Param("maxRow") Integer maxRow);

    @RestResource(exported = false)
    Collection<Association> findBylastMappingDateIsNull();

    @RestResource(path = "findByRsId", rel = "findByRsId")
    Collection<Association> findBySnpsRsId(String rsId);

    @RestResource(path = "findByEfoTrait", rel = "findByEfoTrait")
    Collection<Association> findByEfoTraitsTrait(@Param("efoTrait") String trait);

    @RestResource(path = "findByRsIdAndAccessionId", rel = "findByRsIdAndAccessionId")
    Page<Association> findBySnpsRsIdAndStudyAccessionId(String rsId, String accessionId, Pageable pageable);


    @RestResource(exported = false)
    Page<Association> findByStudyPublicationIdPubmedId(String pubmedId, Pageable pageable);
}
