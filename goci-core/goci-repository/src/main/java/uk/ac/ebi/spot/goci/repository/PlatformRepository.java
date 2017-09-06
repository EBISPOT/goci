package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Platform;

import java.util.List;

/**
 * Created by dwelter on 10/03/16.
 */
@RepositoryRestResource(exported = false)
public interface PlatformRepository extends JpaRepository<Platform, Long>{

    Platform findByManufacturer(String manufacturer);

//    List<Platform> findByStudyId(Long studyId);

    List<Platform> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Long studyId);



}
