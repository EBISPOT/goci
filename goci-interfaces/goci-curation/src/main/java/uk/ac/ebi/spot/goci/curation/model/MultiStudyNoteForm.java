package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.Study;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinhe on 03/04/2017.
 */
public class MultiStudyNoteForm  {
    // depend on the form, this can be associationId or studyId.
    private Long id;

    private Study study;

    @Valid
    private List<StudyNoteForm> noteForms = new ArrayList<StudyNoteForm>();

    public MultiStudyNoteForm() {

    }

    public MultiStudyNoteForm(Long id,
                              Study study,
                              List<StudyNoteForm> noteForms) {
        this.id = id;
        this.study = study;
        this.noteForms = noteForms;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public List<StudyNoteForm> getNoteForms() {
        return noteForms;
    }

    public void setNoteForms(List<StudyNoteForm> noteForms) {
        this.noteForms = noteForms;
    }
}
