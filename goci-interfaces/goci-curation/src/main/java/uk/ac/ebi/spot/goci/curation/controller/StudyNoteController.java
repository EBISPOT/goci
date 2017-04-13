package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import uk.ac.ebi.spot.goci.curation.service.StudyNoteOperationService;
import uk.ac.ebi.spot.goci.curation.model.errors.ErrorNotification;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.NoteSubject;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyNote;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.NoteSubjectService;
import uk.ac.ebi.spot.goci.service.StudyNoteService;
import uk.ac.ebi.spot.goci.service.StudyService;

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
    private StudyOperationsService studyOperationsService;
    private StudyService studyService;

    private Logger log = LoggerFactory.getLogger(getClass());
    protected Logger getLog() {
        return log;
    }

    public StudyNoteController(StudyRepository studyRepository,
                               StudyNoteService studyNoteService,
                               NoteSubjectService noteSubjectService,
                               CurrentUserDetailsService currentUserDetailsService,
                               StudyNoteOperationService studyNoteOperationService,
                               StudyOperationsService studyOperationsService,
                               StudyService studyService) {
        this.studyRepository = studyRepository;
        this.studyNoteService = studyNoteService;
        this.noteSubjectService = noteSubjectService;
        this.currentUserDetailsService = currentUserDetailsService;
        this.studyNoteOperationService = studyNoteOperationService;
        this.studyOperationsService = studyOperationsService;
        this.studyService = studyService;
    }

    @RequestMapping(value = "/studies/{studyId}/notes",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewStudyNotes(Model model, @PathVariable Long studyId) {
        //get the study
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

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


        if(study.getHousekeeping().getIsPublished()){
            return "redirect:/studies/" + studyId + "/notes";
        }

        //the nearly added note can only be assigned one of the availlable subject, not including system note subjects.
        Collection<NoteSubject> noteSubjects = noteSubjectService.findUsableNoteSubject();
        model.addAttribute("availableNoteSubject",noteSubjects);

        SecureUser user = currentUserDetailsService.getUserFromRequest(request);

        //create a default study note with detaul setting
        StudyNote emptyNote = studyNoteService.createGeneralNote(study,user);
        StudyNoteForm emptyNoteForm = studyNoteOperationService.convertToStudyNoteForm(emptyNote);

        //attach the empty form
        multiStudyNoteForm.getNoteForms().add(emptyNoteForm);
        //Index of value to add
        final Integer rowId = multiStudyNoteForm.getNoteForms().size()-1;

        //enable the edit for the new note and disable all edit for other notes
        multiStudyNoteForm.startEdit(rowId);

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

        StudyNoteForm snf = multiStudyNoteForm.getNoteForms().get(rowId.intValue());
        StudyNote noteToRemove = studyNoteOperationService.convertToStudyNote(snf, study);


        //user cannot touch system notes
        if (studyNoteService.isSystemNote(noteToRemove)){
            model.addAttribute("errors", "Update FAIL! Ststem note cannot be removed.");
            model.addAttribute("availableNoteSubject",noteToRemove);
            model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);
            return "study_notes";
        }


        //if not removing empty row
        if (noteToRemove.getId() != null){

            ErrorNotification notification = studyOperationsService.deleteStudyNote(study, noteToRemove);

            if(notification.hasErrors()){
                //we want to display the error to the user simply on top of the form
                getLog().warn("Request: " + req.getRequestURL() + " raised an error." + notification.errorMessage());
                model.addAttribute("errors", "Delete FAIL! " + notification.errorMessage());

                Collection<NoteSubject> noteSubjects = noteSubjectService.findUsableNoteSubject();
                model.addAttribute("availableNoteSubject",noteSubjects);
                model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);
                return "study_notes";
            }else{
                return "redirect:/studies/" + studyId + "/notes";
            }
        }

        return "redirect:/studies/" + studyId + "/notes";

    }





    @RequestMapping(value = "/studies/{studyId}/notes",
                    method = RequestMethod.POST, params = {"saveNote"})
    public String saveNote(@ModelAttribute("multiStudyNoteForm") @Valid MultiStudyNoteForm multiStudyNoteForm,
                           BindingResult bindingResult,
                           Model model, @PathVariable Long studyId, HttpServletRequest req) {

        //Index of value to save
        final Integer rowId = Integer.valueOf(req.getParameter("saveNote"));

        //get the study
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

        //the newly added note can only be assigned one of the availlable subject, not including system note subjects.
        Collection<NoteSubject> noteSubjects = noteSubjectService.findUsableNoteSubject();
        model.addAttribute("availableNoteSubject",noteSubjects);

        //form validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("availableNoteSubject",noteSubjects);
            model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);
            return "study_notes";
        }

        //convert studynoteform to studynote domain object and save
        StudyNoteForm snf = multiStudyNoteForm.getNoteForms().get(rowId.intValue());
        StudyNote noteToEdit = studyNoteOperationService.convertToStudyNote(snf, study);


        ErrorNotification notification = studyOperationsService.addStudyNote(study, noteToEdit);
        if(!notification.hasErrors()){
            return "redirect:/studies/" + studyId + "/notes";
        }else{
            //deal with error
            // we want to display the error to the user simply on top of the form
            getLog().warn("Request: " + req.getRequestURL() + " raised an error." + notification.errorMessage());
            model.addAttribute("errors", "Update FAIL! " + notification.errorMessage());
            model.addAttribute("availableNoteSubject",noteSubjects);
            model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);
        }
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
        //remove subjects including 'Imported from previous system' 'SystemNote'
        Collection<NoteSubject> noteSubjects = noteSubjectService.findUsableNoteSubject();
        model.addAttribute("availableNoteSubject",noteSubjects);

        //enable the edit for the note and disable all edit for other notes
        multiStudyNoteForm.startEdit(rowId);

        model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);
        return "study_notes";
    }

    //This will discard unsave note and refresh the page
    @RequestMapping(value = "/studies/{studyId}/notes", method = RequestMethod.POST, params = {"discardsEditNote"})
    public String discardEditNote(@ModelAttribute("multiStudyNoteForm") MultiStudyNoteForm multiStudyNoteForm,
                                 Model model, @PathVariable Long studyId,
                                 HttpServletRequest req) {

        return "redirect:/studies/" + studyId + "/notes";
    }





//      controller base exception handler
//        @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
//        @ExceptionHandler(StudyIsPublishedException.class)
//        public @ResponseBody String handleNoRenderableDataException(StudyIsPublishedException e) {
//            String responseMsg = "This study is published!!!!!!!!!!!" + e.getMessage();
//            getLog().error(responseMsg, e);
//            return responseMsg;
//        }

}



