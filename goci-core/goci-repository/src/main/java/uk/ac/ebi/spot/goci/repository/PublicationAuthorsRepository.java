package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.spot.goci.model.PublicationAuthors;

import javax.transaction.Transactional;

@RepositoryRestResource(exported = false)
public interface PublicationAuthorsRepository extends JpaRepository<PublicationAuthors, Long> {

    @RestResource(exported = false)
    PublicationAuthors findByAuthorIdAndPublicationIdAndSort(Long author_id, Long publication_id, Integer sort);

    @RestResource(exported = false)
    @Modifying
    @Transactional
    @Query(value = "delete from PUBLICATION_AUTHORS pa where pa.publication_id = :publicationId",nativeQuery = true)
    void deleteByPublicationId(@Param("publicationId") Long publicationId);

}
