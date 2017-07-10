package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.ArrayList;
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
public interface StudyRepository extends JpaRepository<Study, Long>, JpaSpecificationExecutor {

    @RestResource(exported = false)
    Collection<Study> findByDiseaseTraitId(Long diseaseTraitId);

    Page<Study> findByDiseaseTraitId(Long diseaseTraitId, Pageable pageable);

    @RestResource(exported = false)
    Collection<Study> findByPubmedId(String pubmedId);

    Page<Study> findByPubmedId(String pubmedId, Pageable pageable);

    // Pageable queries for filtering main page
    @RestResource(exported = false)
    Page<Study> findByHousekeepingCurationStatusIdAndHousekeepingCuratorId(Long status,
                                                                           Long curator,
                                                                           Pageable pageable);

    @RestResource(exported = false)
    List<Study> findByHousekeepingCurationStatusId(Long status);

    @RestResource(exported = false)
    Page<Study> findByHousekeepingCurationStatusId(Long status, Pageable pageable);

    @RestResource(exported = false)
    Page<Study> findByHousekeepingCurationStatusIdNot(Long status, Pageable pageable);

    @RestResource(exported = false)
    Page<Study> findByHousekeepingCuratorId(Long curator, Pageable pageable);

    @RestResource(exported = false)
    List<Study> findByHousekeepingCuratorId(Long curator);

    @RestResource(exported = false)
    // Custom query to find studies in reports table
    @Query("select s from Study s where s.housekeeping.curator.id like :curator and s.housekeeping.curationStatus.id like :status and EXTRACT(YEAR FROM (TRUNC(TO_DATE(s.publicationDate), 'YEAR'))) = :year and EXTRACT(MONTH FROM (TRUNC(TO_DATE(s.publicationDate), 'MONTH'))) = :month")
    Page<Study> findByPublicationDateAndCuratorAndStatus(@Param("curator") Long curator, @Param("status") Long status,
                                                         @Param("year") Integer year,
                                                         @Param("month") Integer month, Pageable pageable);

    // Queries for study types
    Page<Study> findByGxe(Boolean gxe, Pageable pageable);

    Page<Study> findByGxg(Boolean gxg, Pageable pageable);

    @RestResource(exported = false)
    Page<Study> findByCnv(Boolean cnv, Pageable pageable);

//    @RestResource(exported = false)
//    Page<Study> findByTargetedArray(Boolean targetedArray, Pageable pageable);
//
//    @RestResource(exported = false)
//    Page<Study> findByGenomewideArray(Boolean genomewideArray, Pageable pageable);

    @RestResource(exported = false)
    Page<Study> findByHousekeepingCheckedMappingErrorOrHousekeepingCurationStatusId(Boolean checkedMappingError,
                                                                                    Long status,
                                                                                    Pageable pageable);

    Page<Study> findByGenotypingTechnologiesGenotypingTechnology(String genotypingTechnology, Pageable pageable);


    List<Study> findStudyDistinctByAssociationsMultiSnpHaplotypeTrue(Sort sort);

    List<Study> findStudyDistinctByAssociationsSnpInteractionTrue(Sort sort);

    // EFO trait query
    Page<Study> findByEfoTraitsId(Long efoTraitId, Pageable pageable);

    // Query housekeeping notes field
    @RestResource(exported = false)
    Page<Study> findByHousekeepingNotesContainingIgnoreCase(String query, Pageable pageable);

    // Query note field
    @RestResource(exported = false)
    Page<Study> findDistinctByNotesTextNoteContainingIgnoreCase(String query, Pageable pageable);


    // Custom query to get list of study authors
    @Query("select distinct s.author from Study s") List<String> findAllStudyAuthors(Sort sort);

    Page<Study> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    @RestResource(exported = false)
    List<Study> findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull();

    @RestResource(exported = false)
    List<Study> findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(Sort sort);

    @RestResource(exported = false)
    Page<Study> findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(Pageable pageable);

    @RestResource(exported = false)
    List<Study> findByAssociationsLociStrongestRiskAllelesSnpIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
            Long snpId);

    @RestResource(exported = false)
    List<Study> findByAssociationsIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
            Long associationId);

    @RestResource(exported = false)
    List<Study> findByDiseaseTraitIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
            Long diseaseTraitId);

    @RestResource(exported = false)
    List<Study> findByHousekeepingCatalogPublishDateIsNullOrHousekeepingCatalogUnpublishDateIsNotNull();

    Study findByAssociationsId(Long associationId);

    Page<Study> findByFullPvalueSet(Boolean fullPvalueSet, Pageable pageable);

    Page<Study> findByUserRequested(Boolean userRequested, Pageable pageable);



}

