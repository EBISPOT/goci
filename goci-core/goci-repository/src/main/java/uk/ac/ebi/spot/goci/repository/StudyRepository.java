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

    Collection<Study> findByPubmedId(String pubmedId);

    // Custom queries
    @Query("select s from Study s where s.housekeeping.curationStatus.id = :status")
    Collection<Study> findByCurationStatusIgnoreCase(@Param("status") Long status);

    @Query("select s from Study s where s.housekeeping.curator.id = :curator")
    Collection<Study> findByCuratorIgnoreCase(@Param("curator") Long curator);

    @Query("select s from Study s where s.housekeeping.curationStatus.id = :status and s.housekeeping.curator.id = :curator")
    Collection<Study> findByCurationStatusAndCuratorAllIgnoreCase(@Param("status") Long status,
                                                                  @Param("curator") Long curator);

    @Query("select s from Study s where s.housekeeping.curator.id = :curator")
    Collection<Study> findByCuratorOrderByStudyDateDesc(@Param("curator") Long curator);

    // Custom query to calculate curator totals
    @Query("select s from Study s where s.housekeeping.curator.id = :curator and s.studyDate between :dateFrom and :dateTo")
    List<Study> findByStudyDateAndCurator(@Param("curator") Long curator,
                                          @Param("dateFrom") Date dateFrom,
                                          @Param("dateTo") Date dateTo);

    // Custom query for daily audit emails
    List<Study> findByHousekeepingSendToNCBIDate(Date date);

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

    List<Study> findByAuthor(String author);
}

