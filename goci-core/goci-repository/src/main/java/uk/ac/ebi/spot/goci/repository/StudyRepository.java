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
import java.util.Date;
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

    // Custom query
    @Query("select s from Study s where s.housekeeping.curationStatus.id = :status order by s.studyDate desc")
    Collection<Study> findByCurationStatusIgnoreCase(@Param("status") Long status);

    // Pageable queries for filtering main page
    Page<Study> findByHousekeepingCurationStatusIdAndHousekeepingCuratorId(Long status,
                                                                           Long curator,
                                                                           Pageable pageable);

    Page<Study> findByHousekeepingCurationStatusId(Long status, Pageable pageable);

    Page<Study> findByHousekeepingCurationStatusIdNot(Long status, Pageable pageable);

    Page<Study> findByHousekeepingCuratorId(Long curator, Pageable pageable);

    // Custom query to calculate curator totals
    @Query("select s from Study s where s.housekeeping.curator.id = :curator and s.studyDate between :dateFrom and :dateTo")
    List<Study> findByStudyDateAndCurator(@Param("curator") Long curator,
                                          @Param("dateFrom") Date dateFrom,
                                          @Param("dateTo") Date dateTo);

    // Queries for study types
    Page<Study>findByGxe(Boolean gxe ,Pageable pageable);

    Page<Study>findByGxg(Boolean gxg ,Pageable pageable);

    Page<Study>findByCnv(Boolean cnv ,Pageable pageable);

    Page<Study>findByHousekeepingCheckedNCBIErrorOrHousekeepingCurationStatusId(Boolean checkedNCBIError,Long status ,Pageable pageable);

    // EFO trait query
    Page<Study> findByEfoTraitsId(Long efoTraitId, Pageable pageable);

    // Query housekeeping notes field
    Page<Study> findByHousekeepingNotesContainingIgnoreCase(String query, Pageable pageable);

    // Custom query to get list of study authors
    @Query("select s.author from Study s order by s.author asc")
    List<String> findAllStudyAuthors();

    List<Study> findByHousekeepingSendToNCBIDate(Date date);

    List<Study> findByAuthorContainingIgnoreCase(String author);

    List<Study> findByAuthorContainingIgnoreCase(String author , Sort sort);

    Page<Study>  findByAuthorContainingIgnoreCase(String author , Pageable pageable);

    List<Study> findByHousekeepingPublishDateIsNotNull();

    List<Study> findByHousekeepingPublishDateIsNotNull(Sort sort);

    Page<Study> findByHousekeepingPublishDateIsNotNull(Pageable pageable);

    List<Study> findByAssociationsLociStrongestRiskAllelesSnpIdAndHousekeepingPublishDateIsNotNull(Long snpId);

    List<Study> findByAssociationsLociStrongestRiskAllelesSnpIdAndHousekeepingPublishDateIsNotNull(Sort sort,
                                                                                                   Long snpId);

    Page<Study> findByAssociationsLociStrongestRiskAllelesSnpIdAndHousekeepingPublishDateIsNotNull(Pageable pageable,
                                                                                                   Long snpId);

    List<Study> findByAssociationsIdAndHousekeepingPublishDateIsNotNull(Long associationId);

    List<Study> findByAssociationsIdAndHousekeepingPublishDateIsNotNull(Sort sort, Long associationId);

    Page<Study> findByAssociationsIdAndHousekeepingPublishDateIsNotNull(Pageable pageable, Long associationId);

    List<Study> findByDiseaseTraitIdAndHousekeepingPublishDateIsNotNull(Long diseaseTraitId);

    List<Study> findByDiseaseTraitIdAndHousekeepingPublishDateIsNotNull(Sort sort, Long diseaseTraitId);

    Page<Study> findByDiseaseTraitIdAndHousekeepingPublishDateIsNotNull(Pageable pageable, Long diseaseTraitId);

}

