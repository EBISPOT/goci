package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Note;
import uk.ac.ebi.spot.goci.model.StudyNote;

import java.util.Collection;
import java.util.List;

/**
 * Created by cinzia on 28/03/2017.
 */
@Transactional
@RepositoryRestResource(exported = false)
public interface StudyNoteRepository extends NoteBaseRepository<StudyNote> {

}
