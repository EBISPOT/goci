package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.projection.StudySearchProjection;

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

    @Query("select efoTrait.trait as trait, studies.id as studyId" +
            " FROM EfoTrait as efoTrait" +

            " INNER JOIN efoTrait.studies as studies " +
            " WHERE studies.id in :ids")
    List<StudySearchProjection> findUsingStudyIds(@Param("ids") List<Long> ids);

    @RestResource(exported = false)
    List<EfoTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Long studyId);

    @RestResource(exported = false)
    List<EfoTrait> findByStudiesBkgIdAndStudiesBkgHousekeepingCatalogPublishDateIsNotNullAndStudiesBkgHousekeepingCatalogUnpublishDateIsNull(
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
    @Query("select e.trait from EfoTrait e join e.associations a where a.id = :associationId")
    List<String> findTraitNamesByAssociationsId(Long associationId);

    @RestResource(exported = false)
    @Query("select e.trait from EfoTrait e join e.associationsBkg a where a.id = :associationId")
    List<String> findBkgTraitNamesByAssociationsId(Long associationId);

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

    EfoTrait findByMongoSeqId(String mongoSeqId);

    @RestResource(exported = false)
    Page<EfoTrait> findByStudiesPublicationIdPubmedId(String pumbedId, Pageable pageable);



}

