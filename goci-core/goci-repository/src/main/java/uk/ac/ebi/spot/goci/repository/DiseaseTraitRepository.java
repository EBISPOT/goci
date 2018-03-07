package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;

import java.util.List;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing DiseaseTrait entity object
 */

@RepositoryRestResource(exported = false)
public interface DiseaseTraitRepository extends JpaRepository<DiseaseTrait, Long> {
//    @RestResource(path = "diseaseTrait", rel = "diseaseTrait")
    DiseaseTrait findByTraitIgnoreCase(String trait);

    @RestResource(exported = false)
    List<DiseaseTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Long studyId);

    @RestResource(exported = false)
    List<DiseaseTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long studyId);

    @RestResource(exported = false)
    Page<DiseaseTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long studyId);

    @RestResource(exported = false)
    List<DiseaseTrait> findByStudiesAssociationsIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Long associationId);

    @RestResource(exported = false)
    List<DiseaseTrait> findByStudiesAssociationsIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long associationId);

    @RestResource(exported = false)
    Page<DiseaseTrait> findByStudiesAssociationsIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long associationId);

    @RestResource(path = "pubmedId", rel = "pubmedID")
    Page<DiseaseTrait> findByStudiesPublicationIdPubmedId(String pubmedId, Pageable pageable);

}
