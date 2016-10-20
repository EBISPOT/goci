package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 20/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Study entity object
 */


@RepositoryRestResource
public interface StudyRepository extends JpaRepository<Study, Long> {

    Collection<Study> findByDiseaseTraitId(Long diseaseTraitId);

    Page<Study> findByDiseaseTraitId(Long diseaseTraitId, Pageable pageable);

    Collection<Study> findByPubmedId(String pubmedId);

    Page<Study> findByPubmedId(String pubmedId, Pageable pageable);

    // Pageable queries for filtering main page
    Page<Study> findByHousekeepingCurationStatusIdAndHousekeepingCuratorId(Long status,
                                                                           Long curator,
                                                                           Pageable pageable);

    Page<Study> findByHousekeepingCurationStatusId(Long status, Pageable pageable);

    Page<Study> findByHousekeepingCurationStatusIdNot(Long status, Pageable pageable);

    Page<Study> findByHousekeepingCuratorId(Long curator, Pageable pageable);

    // Custom query to find studies in reports table
    @Query("select s from Study s where s.housekeeping.curator.id like :curator and s.housekeeping.curationStatus.id like :status and EXTRACT(YEAR FROM (TRUNC(TO_DATE(s.publicationDate), 'YEAR'))) = :year and EXTRACT(MONTH FROM (TRUNC(TO_DATE(s.publicationDate), 'MONTH'))) = :month")
    Page<Study> findByPublicationDateAndCuratorAndStatus(@Param("curator") Long curator, @Param("status") Long status,
                                                         @Param("year") Integer year,
                                                         @Param("month") Integer month, Pageable pageable);

    // Queries for study types
    Page<Study> findByGxe(Boolean gxe, Pageable pageable);

    Page<Study> findByGxg(Boolean gxg, Pageable pageable);

    Page<Study> findByCnv(Boolean cnv, Pageable pageable);

    Page<Study> findByTargetedArray(Boolean targetedArray, Pageable pageable);

    Page<Study> findByGenomewideArray(Boolean genomewideArray, Pageable pageable);

    Page<Study> findByHousekeepingCheckedMappingErrorOrHousekeepingCurationStatusId(Boolean checkedMappingError,
                                                                                    Long status,
                                                                                    Pageable pageable);

    List<Study> findStudyDistinctByAssociationsMultiSnpHaplotypeTrue(Sort sort);

    List<Study> findStudyDistinctByAssociationsSnpInteractionTrue(Sort sort);

    // EFO trait query
    Page<Study> findByEfoTraitsId(Long efoTraitId, Pageable pageable);

    // Query housekeeping notes field
    Page<Study> findByHousekeepingNotesContainingIgnoreCase(String query, Pageable pageable);

    // Custom query to get list of study authors
    @Query("select distinct s.author from Study s") List<String> findAllStudyAuthors(Sort sort);

    Page<Study> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    List<Study> findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull();

    List<Study> findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(Sort sort);

    Page<Study> findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(Pageable pageable);

    List<Study> findByAssociationsLociStrongestRiskAllelesSnpIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
            Long snpId);

    List<Study> findByAssociationsIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
            Long associationId);

    List<Study> findByDiseaseTraitIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
            Long diseaseTraitId);

    Study findByAssociationsId(Long associationId);

    Page<Study> findByFullPvalueSet(Boolean fullPvalueSet, Pageable pageable);

}

