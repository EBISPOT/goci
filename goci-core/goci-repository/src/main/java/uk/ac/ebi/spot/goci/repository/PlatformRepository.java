package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Platform;

/**
 * Created by dwelter on 10/03/16.
 */
@RepositoryRestResource
public interface PlatformRepository extends JpaRepository<Platform, Long>{

//    Collection<Platform> findByStudyId(Long studyId);

}
