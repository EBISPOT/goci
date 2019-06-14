package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.NoteSubject;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyNote;

public class StudyNoteBuilder {
    private StudyNote studyNote = new StudyNote();

    public StudyNoteBuilder setId(Long id) {
        studyNote.setId(id);
        return this;
    }

    public StudyNoteBuilder setStudyNote(StudyNote studyNote) {
        this.studyNote = studyNote;
        return this;
    }

    public StudyNoteBuilder setNoteSubject(NoteSubject noteSubject) {
        studyNote.setNoteSubject(noteSubject);
        return this;
    }

    public StudyNoteBuilder setStudy(Study study) {
        studyNote.setStudy(study);
        return this;
    }

    public StudyNoteBuilder setTextNote(String textNote) {
        studyNote.setTextNote(textNote);
        return this;
    }


    public StudyNoteBuilder setCurator(Curator curator) {
        studyNote.setCurator(curator);
        return this;
    }

    public StudyNoteBuilder setGenericId(Long genericId) {
        studyNote.setGenericId(genericId);
        return this;
    }

    public StudyNoteBuilder setStatus(Boolean status) {
        studyNote.setStatus(status);
        return this;
    }

    public StudyNote build() {
        return studyNote;
    }

}
