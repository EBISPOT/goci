package uk.ac.ebi.spot.goci.ui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.ui.model.Study;

/**
 * Created by emma on 20/11/14.
 * @author emma
 *
 * Repository accessing Study entity object
 */


@RepositoryRestResource
public interface StudyRepository extends JpaRepository<Study, Long> {

/*
    @Query("select s from Study s where s.housekeeping.pending = :pending")
    Collection<Study> findByPending(@Param("pending") String pending);

    @Query("select s from Study s where s.housekeeping.publish = :publish")
    Collection<Study> findByPublish(@Param("publish") String publish);
*/


}

