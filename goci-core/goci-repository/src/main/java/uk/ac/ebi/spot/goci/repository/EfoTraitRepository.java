package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.spot.goci.model.EfoTrait;

import java.util.List;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing EfoTrait entity object
 */

@RepositoryRestResource
public interface EfoTraitRepository extends JpaRepository<EfoTrait, Long> {
    @RestResource(exported = false)
    List<EfoTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Long studyId);

    @RestResource(exported = false)
    List<EfoTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long studyId);

    Page<EfoTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long studyId);

    @RestResource(exported = false)
    List<EfoTrait> findByAssociationsId(Long associationId);

    @RestResource(exported = false)
    List<EfoTrait> findByAssociationsId(Sort sort, Long associationId);

    Page<EfoTrait> findByAssociationsId(Pageable pageable, Long associationId);

    @RestResource(exported = false)
    List<EfoTrait> findByUri(String uri);

    @RestResource(exported = false)
    List<EfoTrait> findByUri(Sort sort, String uri);

    Page<EfoTrait> findByUri(Pageable pageable, String uri);

    EfoTrait findByTraitIgnoreCase(String trait);

    EfoTrait findByShortForm(String shortForm);

}

