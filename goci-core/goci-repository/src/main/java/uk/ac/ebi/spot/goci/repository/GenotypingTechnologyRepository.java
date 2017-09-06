package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.GenotypingTechnology;

import java.util.List;

/**
 * Created by dwelter on 22/06/17.
 */

@RepositoryRestResource(exported = false)
public interface GenotypingTechnologyRepository extends JpaRepository<GenotypingTechnology, Long> {

    GenotypingTechnology findByGenotypingTechnology(String genotypingTechnology);

//    List<GenotypingTechnology> findByStudyId(Long studyId);

    List<GenotypingTechnology> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Long studyId);
}
