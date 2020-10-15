package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.deposition.SubmissionImportProgress;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface SubmissionImportProgressRepository extends JpaRepository<SubmissionImportProgress, Long> {

    Optional<SubmissionImportProgress> findBySubmissionId(String submissionId);

}
