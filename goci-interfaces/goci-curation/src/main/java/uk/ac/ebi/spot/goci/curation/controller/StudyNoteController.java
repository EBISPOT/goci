package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.model.MultiStudyNoteForm;
import uk.ac.ebi.spot.goci.curation.model.StudyNoteForm;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.StudyNoteOperationsService;
import uk.ac.ebi.spot.goci.curation.model.errors.ErrorNotification;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.NoteSubject;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyNote;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.NoteService;
import uk.ac.ebi.spot.goci.service.NoteSubjectService;
import uk.ac.ebi.spot.goci.service.StudyNoteService;
import uk.ac.ebi.spot.goci.service.StudyService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by xinhe on 04/04/2017.
 * This controller is responsible for handle study note CRUD
 */
@Controller
public class StudyNoteController {

    private StudyRepository studyRepository;
    private NoteSubjectService noteSubjectService;
    private CurrentUserDetailsService currentUserDetailsService;

    private StudyNoteOperationsService studyNoteOperationsService;
    private StudyOperationsService studyOperationsService;
    private StudyService studyService;
    private StudyNoteService studyNoteService;

    private Logger log = LoggerFactory.getLogger(getClass());
    protected Logger getLog() {
        return log;
    }

    public StudyNoteController(StudyRepository studyRepository,
                               NoteSubjectService noteSubjectService,
                               CurrentUserDetailsService currentUserDetailsService,
                               StudyNoteOperationsService studyNoteOperationsService,
                               StudyOperationsService studyOperationsService,
                               StudyService studyService,
                               StudyNoteService studyNoteService
                               ) {
        this.studyRepository = studyRepository;
        this.noteSubjectService = noteSubjectService;
        this.currentUserDetailsService = currentUserDetailsService;
        this.studyNoteOperationsService = studyNoteOperationsService;
        this.studyOperationsService = studyOperationsService;
        this.studyService = studyService;
        this.studyNoteService = studyNoteService;
    }

    @RequestMapping(value = "/studies/{studyId}/notes",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewStudyNotes(Model model, @PathVariable Long studyId,HttpServletRequest request) {
        //get the study
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

        SecureUser user = currentUserDetailsService.getUserFromRequest(request);

        // an form object mapped from the studyNote object, it contains a list of notes
        MultiStudyNoteForm msnf = studyNoteOperationsService.generateMultiStudyNoteForm(study.getNotes(), study, user);

        model.addAttribute("multiStudyNoteForm", msnf);


        return "study_notes";
    }


    @RequestMapping(value = "/studies/{studyId}/notes", method = RequestMethod.POST, params = {"addNote"})
    public String addNoteToTable(Model model, @PathVariable Long studyId,
                                 HttpServletRequest request) {

        //get the study
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

//disabled the check because we want to add note to the table
//        if(study.getHousekeeping().getIsPublished()){
//            return "redirect:/studies/" + studyId + "/notes";
//        }

        //the newly added note can only be assigned one of the availlable subject, not including system note subjects.
        Collection<NoteSubject> noteSubjects = noteSubjectService.findAvailableNoteSubjectForStudy(study);
        model.addAttribute("availableNoteSubject",noteSubjects);

        SecureUser user = currentUserDetailsService.getUserFromRequest(request);

        // an form object mapped from the studyNote object, it contains a list of notes
        MultiStudyNoteForm msnf = studyNoteOperationsService.generateMultiStudyNoteForm(study.getNotes(), study, user);


        //create a default study note with default setting
        StudyNote emptyNote = studyNoteOperationsService.createEmptyStudyNote(study,user);
        StudyNoteForm emptyNoteForm = studyNoteOperationsService.convertToStudyNoteForm(emptyNote);

        //attach the empty form
        msnf.getNomalNoteForms().add(0,emptyNoteForm);
        //Index of value to add
//        final Integer rowId = msnf.getNomalNoteForms().size()-1;

        //enable the edit for the new note and disable all edit for other notes
        msnf.startEdit(0);

        //reload system notes because they are not part of the input
        msnf.setSystemNoteForms(studyNoteOperationsService.generateSystemNoteForms(study.getNotes()));


        model.addAttribute("multiStudyNoteForm", msnf);

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

        StudyNoteForm snf = multiStudyNoteForm.getNomalNoteForms().get(rowId.intValue());
        StudyNote noteToRemove = studyNoteOperationsService.convertToStudyNote(snf, study);


        //if not removing empty row
        if (noteToRemove.getId() != null){

            ErrorNotification notification = studyOperationsService.deleteStudyNote(study, noteToRemove);

            if(notification.hasErrors()){
                //we want to display the error to the user simply on top of the form
                getLog().warn("Request: " + req.getRequestURL() + " raised an error." + notification.errorMessage());
                model.addAttribute("errors", "Delete FAIL! " + notification.errorMessage());

                Collection<NoteSubject> noteSubjects = noteSubjectService.findAvailableNoteSubjectForStudy(study);
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
        Collection<NoteSubject> noteSubjects = noteSubjectService.findAvailableNoteSubjectForStudy(study);
        model.addAttribute("availableNoteSubject",noteSubjects);

        //form validation
        if (bindingResult.hasErrors()) {
            //reload system notes because they are not part of the input
            multiStudyNoteForm.setSystemNoteForms(studyNoteOperationsService.generateSystemNoteForms(study.getNotes()));
            model.addAttribute("availableNoteSubject",noteSubjects);
            model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);
            return "study_notes";
        }

        //convert studynoteform to studynote domain object and save
        StudyNoteForm snf = multiStudyNoteForm.getNomalNoteForms().get(rowId.intValue());
        StudyNote noteToEdit = studyNoteOperationsService.convertToStudyNote(snf, study);


        SecureUser user = currentUserDetailsService.getUserFromRequest(req);

        ErrorNotification notification = studyOperationsService.addStudyNote(study, noteToEdit, user);
        if(!notification.hasErrors()){
            return "redirect:/studies/" + studyId + "/notes";
        }else{
            //deal with error
            // we want to display the error to the user simply on top of the form
            getLog().warn("Request: " + req.getRequestURL() + " raised an error." + notification.errorMessage());
            model.addAttribute("errors", "Update FAIL! " + notification.errorMessage());
            multiStudyNoteForm.setSystemNoteForms(studyNoteOperationsService.generateSystemNoteForms(study.getNotes()));
            model.addAttribute("availableNoteSubject",noteSubjects);
            model.addAttribute("multiStudyNoteForm", multiStudyNoteForm);
        }
        return "study_notes";
    }


    //This will enable save/remove button for a study note and disable all other action for other notes
    @RequestMapping(value = "/studies/{studyId}/notes", method = RequestMethod.POST, params = {"editNote"})
    public String enableEditNote( Model model, @PathVariable Long studyId,
                             HttpServletRequest req) {

        //Index of value to remove
        final Integer rowId = Integer.valueOf(req.getParameter("editNote"));

        //get the study
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

        //get All note subjects for dropdown
        //remove subjects including 'Imported from previous system' 'SystemNote'
        Collection<NoteSubject> noteSubjects = noteSubjectService.findAvailableNoteSubjectForStudy(study);
        model.addAttribute("availableNoteSubject",noteSubjects);

        SecureUser user = currentUserDetailsService.getUserFromRequest(req);

        // an form object mapped from the studyNote object, it contains a list of notes
        MultiStudyNoteForm msnf = studyNoteOperationsService.generateMultiStudyNoteForm(study.getNotes(), study, user);

        //enable the edit for the note and disable all edit for other notes
        msnf.startEdit(rowId);

        model.addAttribute("multiStudyNoteForm", msnf);
        return "study_notes";
    }

    //This will discard unsave note and refresh the page
    @RequestMapping(value = "/studies/{studyId}/notes", method = RequestMethod.POST, params = {"discardsEditNote"})
    public String discardEditNote(@ModelAttribute("multiStudyNoteForm") MultiStudyNoteForm multiStudyNoteForm,
                                 Model model, @PathVariable Long studyId,
                                 HttpServletRequest req) {

        return "redirect:/studies/" + studyId + "/notes";
    }

    @RequestMapping(value = "/studies/{studyId}/notes", method = RequestMethod.POST, params = {"duplicateNote"})
    public String duplicateNoteAcrossPublication(Model model, @PathVariable Long studyId,
                                                 HttpServletRequest req) {
        final Long noteId = Long.valueOf(req.getParameter("duplicateNote"));
        SecureUser user = currentUserDetailsService.getUserFromRequest(req);


        Study study = studyRepository.findOne(studyId);


        ErrorNotification notification = studyOperationsService.duplicateStudyNoteToSiblingStudies(study,noteId,user);

        if(notification.hasErrors()){
            //we want to display the error to the user simply on top of the form
            getLog().warn("Request: " + req.getRequestURL() + " raised an error." + notification.errorMessage());
            model.addAttribute("errors", "Duplicate FAIL! " + notification.errorMessage());
//            redirectAttributes.addFlashAttribute("errors", "Duplicate FAIL! " + notification.errorMessage());
//            req.setAttribute("errors", "Duplicate FAIL! " + notification.errorMessage());
        }

        return "redirect:/studies/" + studyId + "/notes";
    }


    @RequestMapping(value = "/studies/note/subject/{subjectId}",method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public NoteSubject querySubjectSubject(@PathVariable Long subjectId) {
        return noteSubjectService.findOne(subjectId);
    }


    //#xintodo how we handle exception
        @ExceptionHandler(Exception.class)
        public @ResponseBody String handleNoRenderableDataException(Exception e) {
            String responseMsg = "Exxception!!!!!!!!!!" + e.getMessage();
            getLog().error(responseMsg, e);
            return responseMsg;
        }




}



