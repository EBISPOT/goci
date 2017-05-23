package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.StudyReport;

/**
 * Created by emma on 13/03/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing Study Report entity object
 */
@RepositoryRestResource(exported = false)
public interface StudyReportRepository extends JpaRepository<StudyReport, Long> {
    StudyReport findByStudyId(Long studyId);
}
