package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.StudyExtension;

@RepositoryRestResource(exported = false)
public interface StudyExtensionRepository extends JpaRepository<StudyExtension, Long> {
    @Query(value = "SELECT SE.ID FROM STUDY_EXTENSION SE WHERE SE.STUDY_ID= :studyId", nativeQuery = true) StudyExtension getStudyExtensionId(Long studyId);
}
