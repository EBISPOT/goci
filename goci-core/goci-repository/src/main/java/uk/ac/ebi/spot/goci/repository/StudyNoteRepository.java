package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Note;
import uk.ac.ebi.spot.goci.model.StudyNote;
import uk.ac.ebi.spot.goci.model.projection.StudySearchProjection;

import java.util.Collection;
import java.util.List;

/**
 * Created by cinzia on 28/03/2017.
 */
@Transactional
@RepositoryRestResource(exported = false)
public interface StudyNoteRepository extends NoteBaseRepository<StudyNote> {

    @Query("select studyNote.textNote as textNote, study.id as studyId" +
            " FROM StudyNote as studyNote" +

            " INNER JOIN studyNote.study as study " +
            " WHERE study.id in :ids")
    List<StudySearchProjection> findUsingStudyIds(@Param("ids") List<Long> ids);
}
