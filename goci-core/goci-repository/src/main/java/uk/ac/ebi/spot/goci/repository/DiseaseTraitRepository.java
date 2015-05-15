package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;

import java.util.List;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing DiseaseTrait entity object
 */

@RepositoryRestResource
public interface DiseaseTraitRepository extends JpaRepository<DiseaseTrait, Long> {
    DiseaseTrait findByTraitIgnoreCase(String trait);

    List<DiseaseTrait> findByStudiesIdAndStudiesHousekeepingPublishDateIsNotNull(Long studyId);

    List<DiseaseTrait> findByStudiesIdAndStudiesHousekeepingPublishDateIsNotNull(Sort sort, Long studyId);

    Page<DiseaseTrait> findByStudiesIdAndStudiesHousekeepingPublishDateIsNotNull(Pageable pageable, Long studyId);

    List<DiseaseTrait> findByStudiesAssociationsIdAndStudiesHousekeepingPublishDateIsNotNull(Long associationId);

    List<DiseaseTrait> findByStudiesAssociationsIdAndStudiesHousekeepingPublishDateIsNotNull(Sort sort,
                                                                                             Long associationId);

    Page<DiseaseTrait> findByStudiesAssociationsIdAndStudiesHousekeepingPublishDateIsNotNull(Pageable pageable,
                                                                                             Long associationId);
}
