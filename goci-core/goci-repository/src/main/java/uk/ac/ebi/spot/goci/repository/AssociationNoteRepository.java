package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.StudyNote;

/**
 * Created by cinzia on 28/03/2017.
 */
@Transactional
@RepositoryRestResource(exported = false)
public interface AssociationNoteRepository extends NoteBaseRepository<StudyNote> {
}
