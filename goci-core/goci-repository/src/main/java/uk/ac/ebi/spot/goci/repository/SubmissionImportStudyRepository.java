package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.deposition.SubmissionImportStudy;

import java.util.List;
import java.util.stream.Stream;

@RepositoryRestResource(exported = false)
public interface SubmissionImportStudyRepository extends JpaRepository<SubmissionImportStudy, Long> {

    List<SubmissionImportStudy> findBySubmissionId(String submissionId);

    List<SubmissionImportStudy> findByAccessionId(String accessionId);

    Stream<SubmissionImportStudy> readBySubmissionId(String submissionId);

    void deleteByIdIn(List<Long> ids);

    long countBySubmissionIdAndSuccessAndFinalized(String submissionId, boolean success, boolean finalized);

    void deleteBySubmissionId(String submissionID);
}
