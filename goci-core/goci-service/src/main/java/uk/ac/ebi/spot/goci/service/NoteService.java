package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Note;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.NoteRepository;

import java.util.Collection;

/**
 * Created by cinzia on 07/04/2017.
 */

@Service
public class NoteService {
    private NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // This method delete all the notes with study_id = Study
    public void deleteAllNote(Study study){
        Collection<Note> notes = noteRepository.findByStudyId(study.getId());

        for (Note note : notes) {
            noteRepository.delete(note);
        }
    }
}
