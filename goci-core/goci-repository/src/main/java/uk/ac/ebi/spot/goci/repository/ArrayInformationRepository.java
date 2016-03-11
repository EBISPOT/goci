package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.ArrayInformation;

/**
 * Created by dwelter on 10/03/16.
 */
@RepositoryRestResource
public interface ArrayInformationRepository extends JpaRepository<ArrayInformation, Long> {
    ArrayInformation findByStudyId(Long studyId);
}
