package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Note;
import uk.ac.ebi.spot.goci.model.NoteSubject;
import uk.ac.ebi.spot.goci.repository.NoteSubjectRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by xinhe on 06/04/2017.
 */
@Service
public class NoteSubjectService {
    private NoteSubjectRepository noteSubjectRepository;

    //#xintodo
    private static String DEFAULT = "General";
    private static String SYSTEM_NOTE = "System note";
    private static String IMPORTED_NOTE = "Imported from previous system";
    private static ArrayList<String> SYSTEM_NOTES = new ArrayList<>(Arrays.asList(SYSTEM_NOTE,IMPORTED_NOTE));

    public NoteSubjectService(NoteSubjectRepository noteSubjectRepository) {
        this.noteSubjectRepository = noteSubjectRepository;
    }

    public NoteSubject findOne(Long id){
        return noteSubjectRepository.findOne(id);
    }

    public Collection<NoteSubject> findAll(){
        return noteSubjectRepository.findAll();
    }


    protected NoteSubject findDefaultSubject() {
        NoteSubject subject = noteSubjectRepository.findAll().get(0);
        return subject;
    }

    public NoteSubject findBySubject(String subject) {
        NoteSubject subjectSelected= noteSubjectRepository.findBySubjectIgnoreCase(subject);
        if (subjectSelected==null) {
            //xintodo add log
            subjectSelected = findDefaultSubject();
        }
        return subjectSelected;
    }

    public NoteSubject findAutomaticNote(){
        return findBySubject(SYSTEM_NOTE);
    }

    public NoteSubject findGeneralNote(){
        return findBySubject(DEFAULT);
    }

    public Collection<NoteSubject> findUsableNoteSubject(){
        List<NoteSubject> allNoteSubject = noteSubjectRepository.findAll();
        SYSTEM_NOTES.forEach(s->{
            NoteSubject noteSubject = noteSubjectRepository.findBySubjectIgnoreCase(s);
            if(noteSubject != null){
                allNoteSubject.remove(noteSubject);
            }
        });
        return allNoteSubject;
    }


    public Boolean isSystemNoteSubject(NoteSubject noteSubject){
        return SYSTEM_NOTES.stream().anyMatch(str -> str.equals(noteSubject.getSubject()));
    }



}