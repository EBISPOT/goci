package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.DeletedAncestry;

import java.util.List;

/**
 * Created by emma on 05/08/16.
 *
 * @author emma
 *         <p>
 *         Repository accessing Deleted Ancestry entity object
 */
@RepositoryRestResource
public interface DeletedAncestryRepository extends JpaRepository<DeletedAncestry, Long> {

    List<DeletedAncestry> findByStudyId(Long id);
}

