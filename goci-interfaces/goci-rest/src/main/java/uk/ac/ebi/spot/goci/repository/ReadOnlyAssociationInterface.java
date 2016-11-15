package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Association;

/**
 * Created by dwelter on 14/11/16.
 */

//@NoRepositoryBean
@RepositoryRestResource
public interface ReadOnlyAssociationInterface extends ReadOnlyRepository<Association, Long> {

    Association findOne(Long id);

//    Iterable<Association> findAll();
//
//    Iterable<Association> findAll(Sort sort, int depth);
//
//    Iterable<Association> findAll(Iterable<Long> ids, int depth);
//
//    Iterable<Association> findAll(Iterable<Long> ids, Sort sort);
//
//    Iterable<Association> findAll(Iterable<Long> ids, Sort sort, int depth);
//
//
//    Page<Association> findAll(Pageable pageable, int depth);
//
//    Collection<Association> findByStudyId(long studyId);
//
//    Collection<Association> findByStudyId(long studyId, Sort sort);
}
