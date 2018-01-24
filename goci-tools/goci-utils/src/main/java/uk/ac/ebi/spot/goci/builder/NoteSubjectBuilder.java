package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.NoteSubject;

public class NoteSubjectBuilder {

    private NoteSubject noteSubject= new NoteSubject();

    public NoteSubjectBuilder setId(Long id) {
        noteSubject.setId(id);
        return this;
    }

    public NoteSubject build() { return noteSubject; }


    public NoteSubjectBuilder setSubject(String subject) {
        noteSubject.setSubject(subject);
        return this;
    }

}
