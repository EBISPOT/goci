package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.MultiStudyNoteForm;
import uk.ac.ebi.spot.goci.curation.model.StudyNoteForm;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.NoteSubjectService;
import uk.ac.ebi.spot.goci.curation.service.StudyNoteOperationService;
import uk.ac.ebi.spot.goci.model.NoteSubject;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyNote;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.StudyNoteService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;

/**
 * Created by xinhe on 04/04/2017.
 * This controller is responsible for handle study note CRUD
 */
@Controller
public class StudyNoteController {

    private StudyRepository studyRepository;

    private StudyNoteService studyNoteService;
    private NoteSubjectService noteSubjectService;
    private CurrentUserDetailsService currentUserDetailsService;

    private StudyNoteOperationService studyNoteOperationService;


    public StudyNoteController(StudyRepository studyRepository,
                               StudyNoteService studyNoteService,
                               NoteSubjectService noteSubjectService,
                               CurrentUserDetailsService currentUserDetailsService,
                               StudyNoteOperationService studyNoteOperationService) {
        this.studyRepository = studyRepository;
        this.studyNoteService = studyNoteService;
        this.noteSubjectService = noteSubjectService;
        this.currentUserDetailsService = currentUserDetailsService;
        this.studyNoteOperationService = studyNoteOperationService;
    }

    @RequestMapping(value = "/studies/{studyId}/notes",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewStudyNotes(Model model, @PathVariable Long studyId) {
        //get the study
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

        //get All note subjects for dropdown
        Collection<NoteSubject> noteSubjects = noteSubjectService.findAll();
        model.addAttribute("availableNoteSubject",noteSubjects);

        // Get study notes
        Collection<StudyNote> studyNotes = study.getNotes();

        // an form object mapped from the studyNote object, it contains a list of notes
        MultiStudyNoteForm msnf = studyNoteOperationService.generateMultiStudyNoteForm(study.getNotes(), study);

        model.addAttribute("multiStudyNoteForm", msnf);

        return "study_notes";
    }


    @RequestMapping(value = "/studies/{studyId}/notes", method = RequestMethod.POST, params = {"addNote"})
    public String addNoteToTable(MultiStudyNoteForm multiStudyNoteForm, Model model, @PathVariable Long studyId,
                                 HttpServletRequest request) {

        //get the study
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

        //get All note subjects for dropdown
        Collection<NoteSubject> noteSubjects = noteSubjectService.findAll();
        model.addAttribute("availableNoteSubject",noteSubjects);

        SecureUser user = currentUserDetailsService.getUserFromRequest(request);

        //create a default study note with detaul setting
        StudyNote emptyNote = studyNoteService.createEmptyStudyNote(study,user);
        StudyNoteForm emptyNoteForm = studyNoteOperationService.convertToStudyNoteForm(emptyNote);

        //attach the emput form
        multiStudyNoteForm.getNoteForms().add(emptyNoteForm);

        model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);

        return "study_notes";
    }


    @RequestMapping(value = "/studies/{studyId}/notes", method = RequestMethod.POST, params = {"removeNote"})
    public String removeNote(@ModelAttribute("multiStudyNoteForm") MultiStudyNoteForm multiStudyNoteForm,
                             BindingResult bindingResult, Model model, @PathVariable Long studyId,
                             HttpServletRequest req) {

        //Index of value to remove
        final Integer rowId = Integer.valueOf(req.getParameter("removeNote"));

        //get the study
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

        //get All note subjects for dropdown
        Collection<NoteSubject> noteSubjects = noteSubjectService.findAll();
        model.addAttribute("availableNoteSubject",noteSubjects);


        StudyNoteForm snf = multiStudyNoteForm.getNoteForms().get(rowId.intValue());

        StudyNote noteToRemove = studyNoteOperationService.convertToStudyNote(snf, study);


        //if not removing empty row
        if (noteToRemove.getId() != null){

            try{
                //#xintodo better exception hande needed
                studyNoteService.deleteStudyNote(noteToRemove);
            }
            catch (Exception e){
                model.addAttribute("updateError", e.toString());
            }
        }

        multiStudyNoteForm.getNoteForms().remove(rowId.intValue());

        model.addAttribute("study", study);
        model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);

        return "study_notes";
    }





    @RequestMapping(value = "/studies/{studyId}/notes",
                    method = RequestMethod.POST, params = {"saveNote"})
    public String saveNote(@Valid MultiStudyNoteForm multiStudyNoteForm,BindingResult bindingResult,
                           Model model, @PathVariable Long studyId, HttpServletRequest req) {

        //Index of value to save
        final Integer rowId = Integer.valueOf(req.getParameter("saveNote"));

        //get the study
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

        //get All note subjects for dropdown
        Collection<NoteSubject> noteSubjects = noteSubjectService.findAll();
        model.addAttribute("availableNoteSubject",noteSubjects);

        //#xintodo display error properly
        if (bindingResult.hasErrors()) {
            model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);
            return "study_notes";
        }


        StudyNoteForm snf = multiStudyNoteForm.getNoteForms().get(rowId.intValue());
        StudyNote noteToEdit = studyNoteOperationService.convertToStudyNote(snf, study);

        try{
            //#xintodo better exception hande needed
            studyNoteService.saveStudyNote(noteToEdit);
        }
        catch (Exception e){
            model.addAttribute("updateError", e.toString());
        }

        model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);

        return "study_notes";
    }


    //This will enable save/remove button for a study note and disable all other action for other notes
    @RequestMapping(value = "/studies/{studyId}/notes", method = RequestMethod.POST, params = {"editNote"})
    public String EnableEditNote(@ModelAttribute("multiStudyNoteForm") MultiStudyNoteForm multiStudyNoteForm,
                             Model model, @PathVariable Long studyId,
                             HttpServletRequest req) {

        //Index of value to remove
        final Integer rowId = Integer.valueOf(req.getParameter("editNote"));

        //get the study
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

        //get All note subjects for dropdown
        Collection<NoteSubject> noteSubjects = noteSubjectService.findAll();
        model.addAttribute("availableNoteSubject",noteSubjects);

        //enable the edit for the note and disable all edit for other notes
        multiStudyNoteForm.getNoteForms().forEach(studyNoteForm -> {
            studyNoteForm.makeNotEditable();
        });
        multiStudyNoteForm.getNoteForms().get(rowId.intValue()).Edit();

        model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);
        return "study_notes";
    }

    //This will enable save/remove button for a study note
    @RequestMapping(value = "/studies/{studyId}/notes", method = RequestMethod.POST, params = {"discardsEditNote"})
    public String discardEditNote(@ModelAttribute("multiStudyNoteForm") MultiStudyNoteForm multiStudyNoteForm,
                                 Model model, @PathVariable Long studyId,
                                 HttpServletRequest req) {

        return "redirect:/studies/" + studyId + "/notes";
    }

}

