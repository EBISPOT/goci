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
import uk.ac.ebi.spot.goci.model.projection.StudySearchProjection;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by emma on 20/11/14.
 *
 * @author emma
 * <p>
 * Repository accessing Study entity object
 */


@RepositoryRestResource
public interface StudyRepository extends JpaRepository<Study, Long>, JpaSpecificationExecutor {

    @Query("Select DISTINCT study.id as studyId, study.accessionId as accessionId, firstAuthor.fullname as author, " +
            " pub.title as title, pub.publicationDate as date, pub.pubmedId as pubmedId, " +
            " pub.publication as publication, diseaseTrait.trait as diseaseTrait, " +
            " curator.lastName as curatorLastName, status.status as curationStatus " +

            " from Study study" +
            " LEFT JOIN study.publicationId pub JOIN pub.firstAuthor firstAuthor " +
            " LEFT JOIN study.efoTraits efoTraits " +
            " LEFT JOIN study.diseaseTrait diseaseTrait " +
            " LEFT JOIN study.notes notes " +
            " LEFT JOIN study.genotypingTechnologies gtech " +
            " LEFT JOIN study.housekeeping housekeeping JOIN housekeeping.curationStatus status " +
            " LEFT JOIN housekeeping.curator curator " +

            " WHERE (pub.pubmedId = :pubmedId OR :pubmedId = '*') " +
            " AND ((lower(firstAuthor.fullnameStandard) = lower(:author)) OR :author = '*') " +
            " AND (curator.id = :curator OR :curator = 0) " +
            " AND (efoTraits.id = :efoTraitId OR :efoTraitId = 0) " +
            " AND (diseaseTrait.id = :diseaseTraitId OR :diseaseTraitId = 0) " +
            " AND (status.id LIKE :status OR :status = 0) " +
            " AND (study.accessionId = :accessionId OR :accessionId = '*') " +
            " AND (study.id = :studyId OR :studyId = 0) " +

            " AND (study.gxe = :gxe OR :gxe IS NULL) " +
            " AND (study.gxg = :gxg OR :gxg IS NULL) " +
            " AND (study.cnv = :cnv OR :cnv IS NULL) " +
            " AND (gtech.genotypingTechnology = :genotypeTech OR :genotypeTech = '*') " +

            " AND ((lower(notes.textNote) LIKE lower(CONCAT('%',:notesQuery,'%'))) OR :notesQuery = '*') "
    )
    List<StudySearchProjection> findByMultipleFilters(@Param("pubmedId") String pubmedId,
                                                      @Param("author") String author,
                                                      @Param("efoTraitId") Long efoTraitId,
                                                      @Param("diseaseTraitId") Long diseaseTraitId,
                                                      @Param("notesQuery") String notesQuery,
                                                      @Param("status") Long status,
                                                      @Param("curator") Long curator,
                                                      @Param("accessionId") String accessionId,
                                                      @Param("studyId") Long studyId,

                                                      @Param("gxe") Boolean gxe,
                                                      @Param("gxg") Boolean gxg,
                                                      @Param("cnv") Boolean cnv,
                                                      @Param("genotypeTech") String genotypeTech, Pageable pageable);

    Page<Study> findByHousekeepingIsPublished(Pageable pageable, Boolean isPublished);

    @RestResource(exported = false)
    Page<Study> findByAccessionId(String gcst, Pageable pageable);

    Optional<Study> findByAccessionId(String gcst);

    @RestResource(exported = false)
    Page<Study> findById(Long studyId, Pageable pageable);


    @RestResource(exported = false)
    Collection<Study> findByDiseaseTraitId(Long diseaseTraitId);

    Page<Study> findByDiseaseTraitId(Long diseaseTraitId, Pageable pageable);

    // THOR
    @RestResource(exported = false)
    Collection<Study> findByPublicationIdPubmedId(String pubmedId);

    @RestResource(exported = false)
    Collection<Study> findTop10ByPublicationIdPubmedId(String pubmedId);

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
    Page<Study> findByGxe(Boolean gxe, Pageable pageable);

    Page<Study> findByGxg(Boolean gxg, Pageable pageable);

    @RestResource(exported = false)
    Page<Study> findByCnv(Boolean cnv, Pageable pageable);

    @RestResource(exported = false)
    Page<Study> findByHousekeepingCheckedMappingErrorOrHousekeepingCurationStatusId(Boolean checkedMappingError,
                                                                                    Long status,
                                                                                    Pageable pageable);

    Page<Study> findByGenotypingTechnologiesGenotypingTechnology(String genotypingTechnology, Pageable pageable);


    List<Study> findStudyDistinctByAssociationsMultiSnpHaplotypeTrue(Sort sort);

    List<Study> findStudyDistinctByAssociationsSnpInteractionTrue(Sort sort);

    @RestResource(exported = false)
    Collection<Study> findByEfoTraitsId(Long efoTraitId);

    @RestResource(exported = false)
    Collection<Study> findByMappedBackgroundTraitsId(Long efoTraitId);


    // EFO trait query
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
    @RestResource(exported = false)
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
    Page<Study> findByPublicationIdPubmedIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(String pubmedId, Pageable pageable);

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

    @RestResource(exported = false)
    Page<Study> findByOpenTargets(Boolean openTargets, Pageable pageable);


    @RestResource(exported = false)
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "update study s set s.publication_id=null where s.id=:studyId")
    void setPublicationIdNull(@Param("studyId") Long studyId);

    @RestResource(exported = false)
    @Query(value = "SELECT 'GCST' || accession_seq.nextval FROM dual", nativeQuery =
            true)
    String getNextAccessionId();

    @RestResource(exported = false)
    Page<Study> findAll(Pageable pageable);
<<<<<<< HEAD

=======
>>>>>>> 2.x-dev
}

