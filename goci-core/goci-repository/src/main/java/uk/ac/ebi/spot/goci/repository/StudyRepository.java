package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.spot.goci.model.Study;

import javax.transaction.Transactional;
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

    @RestResource(exported = false)
    Page<Study> findByDiseaseTraitId(Long diseaseTraitId, Pageable pageable);

    // THOR
    @RestResource(exported = false)
    Collection<Study> findByPublicationIdPubmedId(String pubmedId);

    Page<Study> findByPublicationIdPubmedId(String pubmedId, Pageable pageable);


    // Pageable queries for filtering main page
    @RestResource(exported = false)
    Page<Study> findByHousekeepingCurationStatusIdAndHousekeepingCuratorId(Long status,
                                                                           Long curator,
                                                                           Pageable pageable);
    @RestResource(exported = false)
    Page<Study> findByHousekeepingCurationStatusId(Long status, Pageable pageable);

    @RestResource(exported = false)
    Page<Study> findByHousekeepingCurationStatusIdNot(Long status, Pageable pageable);

    @RestResource(exported = false)
    Page<Study> findByHousekeepingCuratorId(Long curator, Pageable pageable);

    @RestResource(exported = false)
    // Custom query to find studies in reports table
    @Query("select s from Study s where s.housekeeping.curator.id like :curator and s.housekeeping.curationStatus.id like :status and EXTRACT(YEAR FROM (TRUNC(TO_DATE(s.publicationId.publicationDate), 'YEAR'))) = :year and EXTRACT(MONTH FROM (TRUNC(TO_DATE(s.publicationId.publicationDate), 'MONTH'))) = :month")
    Page<Study> findByPublicationDateAndCuratorAndStatus(@Param("curator") Long curator, @Param("status") Long status,
                                                         @Param("year") Integer year,
                                                         @Param("month") Integer month, Pageable pageable);

    // Queries for study types
    @RestResource(exported = false)
    Page<Study> findByGxe(Boolean gxe, Pageable pageable);

    @RestResource(exported = false)
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

    @RestResource(exported = false)
    Page<Study> findByGenotypingTechnologiesGenotypingTechnology(String genotypingTechnology, Pageable pageable);


    @RestResource(exported = false)
    List<Study> findStudyDistinctByAssociationsMultiSnpHaplotypeTrue(Sort sort);

    @RestResource(exported = false)
    List<Study> findStudyDistinctByAssociationsSnpInteractionTrue(Sort sort);

    @RestResource(exported = false)
    Collection<Study> findByEfoTraitsId(Long efoTraitId);


    // EFO trait query
    @RestResource(exported = false)
    Page<Study> findByEfoTraitsId(Long efoTraitId, Pageable pageable);

    // Query housekeeping notes field
    @RestResource(exported = false)
    Page<Study> findByHousekeepingNotesContainingIgnoreCase(String query, Pageable pageable);

    // Query note field
    @RestResource(exported = false)
    Page<Study> findDistinctByNotesTextNoteContainingIgnoreCase(String query, Pageable pageable);

    @RestResource(exported = false)
    //Removed Distinct because publicationDate is another table. With distinct returns just the Study attributes
    Page<Study> findByNotesTextNoteContainingIgnoreCase(String query, Pageable pageable);

    // THOR to change
    // Custom query to get list of study authors
    //@Query("select distinct s.author from Study s") List<String> findAllStudyAuthors(Sort sort);

    // THOR
    //Page<Study> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    @RestResource(exported=false)
    Page<Study> findByPublicationIdFirstAuthorFullnameContainingIgnoreCase(String author, Pageable pageable);

    @RestResource(exported = false)
    Page<Study> findByPublicationIdFirstAuthorFullnameStandardContainingIgnoreCase(String author, Pageable pageable);

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

    @RestResource(exported = false)
    Study findByAssociationsId(Long associationId);

    Page<Study> findByFullPvalueSet(Boolean fullPvalueSet, Pageable pageable);

    Page<Study> findByUserRequested(Boolean userRequested, Pageable pageable);

    @RestResource(path = "findByEfoUri", rel = "findByEfoUri")
    Page<Study> findByEfoTraitsUri(String uri, Pageable pageable);

    @RestResource(path = "findByEfoTrait", rel = "findByEfoTrait")
    Page<Study> findByEfoTraitsTraitIgnoreCase(@Param("efoTrait") String trait, Pageable pageable);

    @RestResource(path = "findByDiseaseTrait", rel = "findByDiseaseTrait")
    Page<Study> findByDiseaseTraitTraitIgnoreCase(@Param("diseaseTrait") String trait, Pageable pageable);

    @RestResource
    Study findByAccessionId(String accessionId);

    @RestResource(exported = false)
    Page<Study> findByOpenTargets(Boolean openTargets, Pageable pageable);

    @RestResource(exported = false)
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "update study s set s.publication_id=null where s.id=:studyId")
    void setPublicationIdNull(@Param("studyId") Long studyId);


}

