package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Note;

/**
 * Created by cinzia on 27/03/2017.
 */
@Transactional
@RepositoryRestResource(exported = false)
public interface NoteRepository extends NoteBaseRepository<Note>{
}
