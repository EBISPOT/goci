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
import uk.ac.ebi.spot.goci.model.Study;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * Created by emma on 26/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Association entity object
 */
@RepositoryRestResource
public interface AssociationRepository extends JpaRepository<Association, Long> {
    Collection<Association> findByStudyId(long studyId);

    @RestResource(exported = false)
    Page<Association> findByStudyId(long studyId, Pageable pageable);

    @RestResource(exported = false)
    @Query("select new Association (a.id) from Association a join a.study s where s.id = :studyId")
    Collection<Association> findIdByStudyId(long studyId);

    //GOCI-2267 hotfix
    @RestResource(exported=false)
    @Query(value="SELECT max(a.LAST_MAPPING_DATE) FROM Association a where a.study_id = :study_id and a.LAST_MAPPING_PERFORMED_BY='automatic_mapping_process'", nativeQuery = true)
    Optional<Timestamp> findLastMappingDateByStudyId(@Param("study_id") Long study_id);

    @RestResource(exported = false)
    Collection<Association> findByStudyId(long studyId, Sort sort);

    List<Association> findByStudyIdAndLastUpdateDateIsNotNullOrderByLastUpdateDateDesc(Long studyId);

    Collection<Association> findByLociStrongestRiskAllelesSnpId(long snpId);

    @RestResource(exported = false)
    List<Association> findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull();

    @RestResource(exported = false)
    List<Association> findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Sort sort);

    Page<Association> findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable);

    Page<Association> findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNullAndStudyPublicationIdPubmedId(
            String pubmedId, Pageable pageable);

    @RestResource(exported = false)
    List<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Long snpId);

    @RestResource(exported = false)
    List<Association> findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long snpId);

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

    Page<Association> findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long efoTraitId);

    @Query(value = "select * from "+
            "(select a.*, rownum rnum from (select * from association order by id) a where rownum <= :maxRow) "+
            " where rnum >= :minRow ",
            nativeQuery = true)
    Collection<Association> findAllLSF(@Param("minRow") Integer minRow, @Param("maxRow") Integer maxRow);


    Collection<Association> findBylastMappingDateIsNull();

    Collection<Association> findByEfoTraitsId(Long efoTraitId);

    @RestResource(exported = false)
    Page<Association> findByStudyPublicationIdPubmedId(String pubmedId, Pageable pageable);
}
