package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.UnpublishedStudy;

import org.springframework.data.domain.Pageable;

@RepositoryRestResource(exported = false)
public interface UnpublishedStudyRepository extends JpaRepository<UnpublishedStudy, Long> {

    public UnpublishedStudy findByAccession(String accessionID);

    @Query("SELECT U FROM UnpublishedStudy U WHERE U.summaryStatsFile <> 'NR' ")
    Page<UnpublishedStudy> findBySummaryStatsFileIsNotRequired(Pageable pageable);
}
