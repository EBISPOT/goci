package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.projection.DiseaseTraitProjection;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing DiseaseTrait entity object
 */

@RepositoryRestResource
public interface DiseaseTraitRepository extends JpaRepository<DiseaseTrait, Long> {
    Optional<DiseaseTrait> findByTraitIgnoreCase(String trait);

    @Query("Select diseaseTrait.id as id, diseaseTrait.trait as trait, count(studies.id) as studiesCount from DiseaseTrait diseaseTrait " +
            "LEFT JOIN diseaseTrait.studies studies group by diseaseTrait.id, diseaseTrait.trait " +
            "ORDER BY studiesCount desc ")
    List<DiseaseTraitProjection> findAllOrOrderByStudiesLargest(Pageable pageable);

    @Query(value = "Select dt from DiseaseTrait dt " +
            "WHERE lower( dt.trait ) LIKE lower(CONCAT('%',:search,'%')) ")
    Page<DiseaseTrait> findBySearchParameter(@Param("search") String search, Pageable pageable);

    Optional<DiseaseTrait> findByTrait(String trait);

    List<DiseaseTrait> findAllByIdIsIn(List<Long> id);

    @RestResource(exported = false)
    List<DiseaseTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Long studyId);

    @RestResource(exported = false)
    List<DiseaseTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long studyId);

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

    Page<DiseaseTrait> findByStudiesAssociationsIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long associationId);

    Page<DiseaseTrait> findByStudiesPublicationIdPubmedId(
            String pubmedId,
            Pageable pageable);

    @RestResource(exported = false)
    @Query(nativeQuery = true, value = "select dt.id, count(dt.id) from disease_trait dt left join " +
            "study_disease_trait sdt on dt.id=sdt.disease_trait_id inner join study s on s.id = sdt.study_id " +
            "group by dt.id order by count(dt.id) desc")
    List<Map.Entry> getDiseaseTraitCounts();

}
