package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.StudyExtension;

@RepositoryRestResource(exported = false)
public interface StudyExtensionRepository extends JpaRepository<StudyExtension, Long> {}
