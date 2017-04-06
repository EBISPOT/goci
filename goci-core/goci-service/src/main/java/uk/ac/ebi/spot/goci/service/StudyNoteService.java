package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Note;
import uk.ac.ebi.spot.goci.model.NoteSubject;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyNote;
import uk.ac.ebi.spot.goci.repository.NoteRepository;
import uk.ac.ebi.spot.goci.repository.NoteSubjectRepository;
import uk.ac.ebi.spot.goci.repository.StudyNoteRepository;

import java.util.Collection;
import java.util.List;

/**
 * Created by xinhe on 03/04/2017.
 * The service can be potentially useful for other services, for example, we might want to automatically generate notes for the mapping etc.
 */
@Service
public class StudyNoteService {
    private StudyNoteRepository studyNoteRepository;
    private NoteSubjectRepository noteSubjectRepository;

    private CuratorService curatorService;


    @Autowired
    public StudyNoteService(StudyNoteRepository studyNoteRepository,
                            NoteSubjectRepository noteSubjectRepository,
                            CuratorService curatorService) {
        this.studyNoteRepository = studyNoteRepository;
        this.noteSubjectRepository = noteSubjectRepository;
        this.curatorService = curatorService;
    }


    public Collection<StudyNote> findByStudyId(Long studyId){
        Collection<StudyNote> studyNotes= studyNoteRepository.findByGenericId(studyId);
        return studyNotes;
    }

    public StudyNote findOne(Long id){
        return studyNoteRepository.findOne(id);
    }

    public void saveStudyNote(StudyNote studyNote){
//        #todo exception
        studyNoteRepository.save(studyNote);
    }

    public void deleteStudyNote(StudyNote studyNote){
        //        #todo exception
        studyNoteRepository.delete(studyNote);
    }

    public StudyNote createEmptyStudyNote(Study study, SecureUser user){
        StudyNote note = new StudyNote();
        note.setStudy(study);
        //defult notesubject
        NoteSubject ns = noteSubjectRepository.findOne(new Long(1));
        note.setNoteSubject(ns);

        //defult curator will be the one who is currently adding the note
        Curator curator = curatorService.getCuratorIdByEmail(user.getEmail());
        note.setCurator(curator);

        note.setStatus(false);
        note.setGenericId(study.getId());
        return note;
    }



}

