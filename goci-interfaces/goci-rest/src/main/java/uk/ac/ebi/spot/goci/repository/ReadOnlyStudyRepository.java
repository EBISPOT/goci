package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Study;

/**
 * Created by dwelter on 14/11/16.
 */

//@NoRepositoryBean
@RepositoryRestResource(collectionResourceRel = "studies", path = "studies")
public interface ReadOnlyStudyRepository extends ReadOnlyRepository<Study, Long> {

    Study findOne(Long id);

    Iterable<Study> findAll();

//    Iterable<Study> findAll(Sort sort, int depth);

//    Iterable<Study> findAll(Iterable<Long> ids, int depth);
//
//    Iterable<Study> findAll(Iterable<Long> ids, Sort sort);
//
//    Iterable<Study> findAll(Iterable<Long> ids, Sort sort, int depth);
//
//
//    Page<Study> findAll(Pageable pageable, int depth);
}
