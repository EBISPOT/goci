package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.constants.Endpoint;
import uk.ac.ebi.spot.goci.curation.controller.assembler.StudyToViewAssembler;
import uk.ac.ebi.spot.goci.curation.dto.StudyViewDto;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;
import uk.ac.ebi.spot.goci.curation.exception.NoStudyDirectoryException;
import uk.ac.ebi.spot.goci.curation.exception.PubmedImportException;
import uk.ac.ebi.spot.goci.curation.model.*;
import uk.ac.ebi.spot.goci.curation.service.*;
import uk.ac.ebi.spot.goci.curation.service.deposition.DepositionSubmissionService;
import uk.ac.ebi.spot.goci.curation.caching.CacheService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionProvenance;
import uk.ac.ebi.spot.goci.model.deposition.DepositionUser;
import uk.ac.ebi.spot.goci.model.deposition.Submission;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.exception.PubmedLookupException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by emma on 20/11/14.
 *
 * @author emma
 * <p>
 * Study Controller interprets user input and transform it into a study model that is represented to the user by
 * the associated HTML page.
 */

@Controller
@RequestMapping("/studies")
public class StudyController {

    @Value("${deposition.ui.uri}")
    private String depositionUiURL;

    @Autowired private CacheService cacheService;
    @Autowired private DiseaseTraitService diseaseTraitService;
    @Autowired private EfoTraitService efoTraitService;
    private final DepositionSubmissionService submissionService;
    private StudyRepository studyRepository;
    private HousekeepingRepository housekeepingRepository;;
    private AssociationRepository associationRepository;
    private PublicationOperationsService publicationOperationsService;

    private StudyOperationsService studyOperationsService;
    private MappingDetailsService mappingDetailsService;
    private CurrentUserDetailsService currentUserDetailsService;
    private StudyFileService studyFileService;
    private StudyDuplicationService studyDuplicationService;
    private StudyDeletionService studyDeletionService;
    private EventsViewService eventsViewService;
    private StudyUpdateService studyUpdateService;

    private static final int MAX_PAGE_ITEM_DISPLAY = 25;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public StudyController(StudyRepository studyRepository,
                           HousekeepingRepository housekeepingRepository,
                           AssociationRepository associationRepository,
                           StudyOperationsService studyOperationsService,
                           MappingDetailsService mappingDetailsService,
                           CurrentUserDetailsService currentUserDetailsService,
                           StudyFileService studyFileService,
                           StudyDuplicationService studyDuplicationService,
                           StudyDeletionService studyDeletionService,
                           @Qualifier("studyEventsViewService") EventsViewService eventsViewService,
                           StudyUpdateService studyUpdateService,
                           PublicationOperationsService publicationOperationsService,
                           DepositionSubmissionService submissionService) {
        this.studyRepository = studyRepository;
        this.housekeepingRepository = housekeepingRepository;
        this.associationRepository = associationRepository;
        this.studyOperationsService = studyOperationsService;
        this.mappingDetailsService = mappingDetailsService;
        this.currentUserDetailsService = currentUserDetailsService;
        this.studyFileService = studyFileService;
        this.studyDuplicationService = studyDuplicationService;
        this.studyDeletionService = studyDeletionService;
        this.eventsViewService = eventsViewService;
        this.studyUpdateService = studyUpdateService;
        this.publicationOperationsService = publicationOperationsService;
        this.submissionService = submissionService;
    }

    /* All studies and various filtered lists */
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String allStudiesPage(Model model,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) String pubmed,
                                 @RequestParam(required = false) String author,
                                 @RequestParam(value = "studytype", required = false) String studyType,
                                 @RequestParam(value = "efotraitid", required = false) Long efoTraitId,
                                 @RequestParam(value = "notesquery", required = false) String notesQuery,
                                 @RequestParam(required = false) String gcstId,
                                 @RequestParam(required = false) String studyId,
                                 @RequestParam(required = false) Long status,
                                 @RequestParam(required = false) Long curator,
                                 @RequestParam(value = "sorttype", required = false) String sortType,
                                 @RequestParam(value = "diseasetraitid", required = false) Long diseaseTraitId,
                                 @RequestParam(required = false) Integer year,
                                 @RequestParam(required = false) Integer month) {

        model.addAttribute("baseUrl", depositionUiURL);

        // This is passed back to model and determines if pagination is applied
        Boolean pagination = true;
        if (page == null) {
            return "redirect:/studies?page=1";
        }

        StudyViewDto studyViewDto =
                studyOperationsService.getStudies(page, studyType, sortType, pubmed, author, pagination, efoTraitId,
                        diseaseTraitId, notesQuery, status, curator, month, year, gcstId, studyId);
        String filters = studyViewDto.getFilters();
        String sortString = studyViewDto.getSortString();
        List<Study> studies = studyViewDto.getStudies();
        Page<Study> studyPage = studyViewDto.getStudyPage();
        StudySearchFilter studySearchFilter = studyViewDto.getStudySearchFilter();

        String uri = "/studies?page=1";
        String downloadUrl = String.format("%s%s/export?page=all", Endpoint.API_V1, Endpoint.STUDIES);
        if (!filters.isEmpty()) {
            uri = uri + filters;
            downloadUrl += filters;
        }
        model.addAttribute("uri", uri);
        model.addAttribute("downloadUrl", downloadUrl);

        // Return study page and filters, filters will be used by pagination bar
        if (!filters.isEmpty()) {
            if (!sortString.isEmpty()) {
                filters = filters + sortString;
            }
        }
        // If user has just sorted without any filter we need to pass this back to pagination bar
        else {
            if (!sortString.isEmpty()) {
                filters = sortString;
            }
        }
        model.addAttribute("filters", filters);

        long totalStudies;
        int current = 1;

        // Construct table using pagination
        if (studies == null) {
            model.addAttribute("studies", studyPage);
            //Pagination variables
            totalStudies = studyPage.getTotalElements();
            current = studyPage.getNumber() + 1;

            int begin = Math.max(1, current - 5); // Returns the greater of two values
            int end =
                    Math.min(begin + 10, studyPage.getTotalPages()); // how many pages to display in the pagination bar

            model.addAttribute("beginIndex", begin);
            model.addAttribute("endIndex", end);
            model.addAttribute("currentIndex", current);
        } else {
            model.addAttribute("studies", studies);
            totalStudies = studies.size();
        }
        model.addAttribute("totalStudies", totalStudies);
        model.addAttribute("pagination", pagination);

        // Add studySearchFilter to model so user can filter table
        model.addAttribute("studySearchFilter", studySearchFilter);

        // Add assignee and status assignment so user can assign study to curator or assign a status. Also set uri so we can redirect to page user was on
        Assignee assignee = new Assignee();
        StatusAssignment statusAssignment = new StatusAssignment();
        assignee.setUri("/studies?page=" + current + filters);
        statusAssignment.setUri("/studies?page=" + current + filters);
        model.addAttribute("assignee", assignee);
        model.addAttribute("statusAssignment", statusAssignment);

        //Map<String, String> pubmedMap = submissionService.getSubmissionPubMedIds();
        studyPage.forEach(study -> {
            try {
                String submissionId = submissionService.getSubmissionPubMedIds(study.getPublicationId().getPubmedId());

                //if (pubmedMap.containsKey(study.getPublicationId().getPubmedId())) {
                study.getPublicationId().setActiveSubmission(true);
                //study.getPublicationId().setSubmissionId(pubmedMap.get(study.getPublicationId().getPubmedId()));
                study.getPublicationId().setSubmissionId(submissionId);
            } catch(Exception ex) {
                log.error("Exception in restTemplate call with Submission Envelope"+ex.getMessage(),ex);
            }
            //}
        });
        return "studies";
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String searchForStudyByFilter(@ModelAttribute StudySearchFilter studySearchFilter) {

        // Get ids of objects searched for
        Long status = studySearchFilter.getStatusSearchFilterId();
        Long curator = studySearchFilter.getCuratorSearchFilterId();
        String pubmedId = studySearchFilter.getPubmedId();
        String author = studySearchFilter.getAuthor();
        String studyType = studySearchFilter.getStudyType();
        Long efoTraitId = studySearchFilter.getEfoTraitSearchFilterId();
        String notesQuery = studySearchFilter.getNotesQuery();
        Long diseaseTraitId = studySearchFilter.getDiseaseTraitSearchFilterId();
        String gcstId = studySearchFilter.getGcstId();
        String studyId = studySearchFilter.getStudyId();

        // Search by pubmed ID option available from landing page
        if (pubmedId != null && !pubmedId.isEmpty()) {
            return "redirect:/studies?page=1&pubmed=" + pubmedId;
        }

        // Search by author option available from landing page
        else if (author != null && !author.isEmpty()) {
            return "redirect:/studies?page=1&author=" + author;
        }

        // Search by study type
        else if (studyType != null && !studyType.isEmpty()) {
            return "redirect:/studies?page=1&studytype=" + studyType;
        }

        // Search by efo trait
        else if (efoTraitId != null) {
            return "redirect:/studies?page=1&efotraitid=" + efoTraitId;
        }

        // Search by disease trait
        else if (diseaseTraitId != null) {
            return "redirect:/studies?page=1&diseasetraitid=" + diseaseTraitId;
        }

        // Search by string in notes
        else if (notesQuery != null && !notesQuery.isEmpty()) {
            return "redirect:/studies?page=1&notesquery=" + notesQuery;
        }

        // If user entered a status
        else if (status != null) {
            // If we have curator and status find by both
            if (curator != null) {
                return "redirect:/studies?page=1&status=" + status + "&curator=" + curator;
            } else {
                return "redirect:/studies?page=1&status=" + status;
            }
        }
        // If user entered curator
        else if (curator != null) {
            return "redirect:/studies?page=1&curator=" + curator;
        } else if (gcstId != null) {
            return "redirect:/studies?page=1&gcstId=" + gcstId;
        } else if (studyId != null) {
            return "redirect:/studies?page=1&studyId=" + studyId;
        }

        // If all else fails return all studies
        else {
            // Find all studies ordered by study date and only display first page
            return "redirect:/studies?page=1";
        }

    }

    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String newStudyForm(Model model) {
        model.addAttribute("study", new Study());
        model.addAttribute("pubmedIdForImport", new PubmedIdForImport());
        return "add_study";
    }

    @CrossOrigin
    @RequestMapping(value = "/new/import", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<ArrayList<HashMap<String, String>>> importStudy(@RequestBody String pubmedIdForImport, HttpServletRequest request) {
        SecureUser currentUser = currentUserDetailsService.getUserFromRequest(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        ArrayList<HashMap<String, String>> result = publicationOperationsService.importNewPublications(pubmedIdForImport, currentUser);
        return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/new/migratePublications", produces = MediaType.TEXT_HTML_VALUE, method = {RequestMethod.GET, RequestMethod.POST})
    public synchronized String importAllStudy(@ModelAttribute PubmedIdForImport pubmedIdForImport) throws PubmedImportException, NoStudyDirectoryException {
        publicationOperationsService.importPublicationsWithoutFirstAuthor();
        return "redirect:/studies/";
    }

    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public synchronized String addStudy(@Valid @ModelAttribute Study study, BindingResult bindingResult, Model model,
                                        HttpServletRequest request) throws NoStudyDirectoryException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("study", study);
            model.addAttribute("pubmedIdForImport", new PubmedIdForImport());
            return "add_study";
        }

        Study savedStudy = studyOperationsService.createStudy(study, currentUserDetailsService.getUserFromRequest(request));
        try {
            studyFileService.createStudyDir(savedStudy.getId());
        } catch (NoStudyDirectoryException e) {
            getLog().error("No study directory exception");
            model.addAttribute("study", savedStudy);
            return "error_pages/study_dir_failure";
        }
        return "redirect:/studies/" + savedStudy.getId();
    }

    @RequestMapping(value = "/{studyId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudy(Model model, @PathVariable Long studyId) {
        log.info("Started retrieving study with id: {}", studyId);
        Study studyToView = studyRepository.findOne(studyId);
        log.info("Finished retrieving study with id: {}", studyId);
        Map<String, String> pubmedMap = submissionService.getSubmissionPubMedIds();
        if (pubmedMap.containsKey(studyToView.getPublicationId().getPubmedId())) {
            studyToView.getPublicationId().setActiveSubmission(true);
            studyToView.getPublicationId().setSubmissionId(pubmedMap.get(studyToView.getPublicationId().getPubmedId()));
        }

        model.addAttribute("study", studyToView);
        log.info("Started retrieving study extension id: {}", studyId);
        if (studyToView.getStudyExtension() == null) {
            StudyExtension extension = new StudyExtension();
            extension.setStudy(studyToView);
            studyToView.setStudyExtension(extension);
        }
        model.addAttribute("extension", studyToView.getStudyExtension());
        log.info("Finished retrieving study extension id: {}", studyId);

        DepositionProvenance depositionProvenance = submissionService.getProvenance(studyToView.getPublicationId().getPubmedId());
        DepositionUser depositionUser = depositionProvenance == null ? new DepositionUser("N/A", "N/A") : depositionProvenance.getUser();
        model.addAttribute("submitter", depositionUser);

        model.addAttribute("studyToViewDto", StudyToViewAssembler.assemble(studyToView));

        log.info("Finished retrieving study provenance");
        return "study";
    }

    @RequestMapping(value = "/{studyId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String updateStudy(@ModelAttribute Study study, @ModelAttribute StudyExtension extension, @PathVariable Long studyId,
                              RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (extension == null) {
            extension = new StudyExtension();
        }
        studyUpdateService.updateStudy(studyId, study, extension, currentUserDetailsService.getUserFromRequest(request));
        String message = "Changes saved successfully";
        redirectAttributes.addFlashAttribute("changesSaved", message);
        return "redirect:/studies/" + study.getId();
    }

    @CrossOrigin
    @RequestMapping(value = "/{studyId}/changeFirstAuthor/{authorId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<HashMap<String, String>> changeFirstAuthor(@PathVariable(value = "studyId") Long studyId,
                                                              @PathVariable(value = "authorId") Long authorId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");

        HashMap<String, String> result = new HashMap<String, String>();
        Boolean isChangeAuthor = publicationOperationsService.changeFirstAuthorByStudyId(studyId, authorId);
        String key = (isChangeAuthor) ? "success" : "error";
        result.put(key, "");
        return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/{studyId}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudyToDelete(Model model, @PathVariable Long studyId) {

        Study studyToDelete = studyRepository.findOne(studyId);
        Collection<Association> associations = associationRepository.findByStudyId(studyId);
        Long housekeepingId = studyToDelete.getHousekeeping().getId();
        Housekeeping housekeepingAttachedToStudy = housekeepingRepository.findOne(housekeepingId);
        Map<String, Submission> submissions = studyToDelete.getPublicationId() != null ? submissionService.getSubmissionsForPMID(studyToDelete.getPublicationId().getPubmedId()) :
                new HashMap<>();
        model.addAttribute("studyToDelete", new StudyToDelete(studyToDelete, Integer.toString(submissions.size())));

        if (housekeepingAttachedToStudy.getCatalogPublishDate() != null) {
            return "delete_published_study_warning";
        } else if (!associations.isEmpty()) {
            return "delete_study_with_associations_warning";
        } else {
            return "delete_study";
        }
    }

    @RequestMapping(value = "/{studyId}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String deleteStudy(@PathVariable Long studyId, HttpServletRequest request) {

        // Find our study based on the ID
        Study studyToDelete = studyRepository.findOne(studyId);
        studyDeletionService.deleteStudy(studyToDelete, currentUserDetailsService.getUserFromRequest(request));
        return "redirect:/studies";
    }

    @RequestMapping(value = "/{studyId}/duplicate", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String duplicateStudyGet(Model model, @PathVariable Long studyId,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) {

        Study studyToDuplicate = studyRepository.findOne(studyId);
        model.addAttribute("study", studyToDuplicate);

        return "study_duplication";
    }

    @RequestMapping(value = "/{studyId}/duplicate", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> duplicateStudyPost(@PathVariable Long studyId,
                                              @RequestBody String tagsNoteList, HttpServletRequest request) {

        String result = "";
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");

        // Find study user wants to duplicate, based on the ID
        Study studyToDuplicate = studyRepository.findOne(studyId);
        SecureUser secureUser = currentUserDetailsService.getUserFromRequest(request);
        result = studyDuplicationService.create(studyToDuplicate, tagsNoteList, secureUser);

        if (result == "") {
            result = new StringBuilder("{\"success\":\"studies?page=1&pubmed=").append(studyToDuplicate.getPublicationId().getPubmedId()).append("\"}").toString();
        }
        return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
    }


    @RequestMapping(value = "/{studyId}/assign", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String assignStudyCurator(@PathVariable Long studyId, @ModelAttribute Assignee assignee,
                                     RedirectAttributes redirectAttributes, HttpServletRequest request) {

        Study study = studyRepository.findOne(studyId);

        if (assignee.getCuratorId() == null) {
            String blankAssignee =
                    "Cannot assign a blank value as a curator for study: " + study.getPublicationId().getFirstAuthor().getFullnameShort(30) + ", " + " pubmed = " +
                            study.getPublicationId().getPubmedId();
            redirectAttributes.addFlashAttribute("blankAssignee", blankAssignee);
        } else {
            studyOperationsService.assignStudyCurator(study, assignee, currentUserDetailsService.getUserFromRequest(request));
        }
        return "redirect:" + assignee.getUri();
    }

    @RequestMapping(value = "/{studyId}/status_update", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String assignStudyStatus(@PathVariable Long studyId, @ModelAttribute StatusAssignment statusAssignment,
                                    RedirectAttributes redirectAttributes, HttpServletRequest request) {

        Study study = studyRepository.findOne(studyId);
        if (statusAssignment.getStatusId() == null) {
            String blankStatus =
                    "Cannot assign a blank value as a status for study: " + study.getPublicationId().getFirstAuthor().getFullnameShort(30) + ", " + " pubmed = " +
                            study.getPublicationId().getPubmedId();
            redirectAttributes.addFlashAttribute("blankStatus", blankStatus);
        } else {
            String message = studyOperationsService.assignStudyStatus(study,
                    statusAssignment,
                    currentUserDetailsService.getUserFromRequest(
                            request));
            redirectAttributes.addFlashAttribute("studySnpsNotApproved", message);
        }
        return "redirect:" + statusAssignment.getUri();
    }

    @RequestMapping(value = "/{studyId}/housekeeping", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudyHousekeeping(Model model, @PathVariable Long studyId) {

        Study study = studyRepository.findOne(studyId);
        if (study.getHousekeeping() == null) {
            model.addAttribute("studyHousekeeping", new Housekeeping());
        } else {
            model.addAttribute("studyHousekeeping", study.getHousekeeping());
        }

        model.addAttribute("study", study);
        model.addAttribute("mappingDetails", mappingDetailsService.createMappingSummary(study));
        return "study_housekeeping";
    }

    @RequestMapping(value = "/{studyId}/housekeeping", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String updateStudyHousekeeping(@ModelAttribute Housekeeping housekeeping, @PathVariable Long studyId,
                                          RedirectAttributes redirectAttributes, HttpServletRequest request) {

        Study study = studyRepository.findOne(studyId);
        String message = studyOperationsService.updateHousekeeping(housekeeping, study, currentUserDetailsService.getUserFromRequest(request));
        if (message == null) {
            message = "Changes saved successfully";
            redirectAttributes.addFlashAttribute("changesSaved", message);
        } else {
            redirectAttributes.addFlashAttribute("publishError", message);
        }
        return "redirect:/studies/" + study.getId() + "/housekeeping";
    }


    @RequestMapping(value = "/{studyId}/unpublish", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudyToUnpublish(Model model, @PathVariable Long studyId) {
        Study studyToUnpublish = studyRepository.findOne(studyId);
        model.addAttribute("studyToUnpublish", studyToUnpublish);
        return "unpublish_study";
    }

    @RequestMapping(value = "/{studyId}/unpublish", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String unpublishStudy(@ModelAttribute Study studyToUnpublish, @PathVariable Long studyId, HttpServletRequest request) {
        studyOperationsService.unpublishStudy(studyId, studyToUnpublish.getHousekeeping().getUnpublishReason(),
                currentUserDetailsService.getUserFromRequest(request));
        return "redirect:/studies/" + studyToUnpublish.getId() + "/housekeeping";
    }

    @RequestMapping(value = "/{studyId}/studyfiles", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getStudyFiles(Model model, @PathVariable Long studyId) {
        model.addAttribute("files", studyFileService.getStudyFiles(studyId));
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "study_files";
    }

    @RequestMapping(value = "/{studyId}/studyfiles/{fileName}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource downloadStudyFile(@PathVariable Long studyId,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws FileNotFoundException {
        String path = request.getServletPath();
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        return new FileSystemResource(studyFileService.getFileFromFileName(studyId, fileName));
    }

    @RequestMapping(value = "/{studyId}/studyfiles/{fileName}/delete", method = RequestMethod.GET)
    public String deleteStudyFile(@PathVariable Long studyId,
                                  @PathVariable String fileName) {
        studyFileService.deleteFile(studyId, fileName);
        return "redirect:/studies/" + studyId + "/studyfiles";
    }

    @RequestMapping(value = "/{studyId}/studyfiles", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public Callable<String> uploadStudyFile(@RequestParam("file") MultipartFile file,
                                            @PathVariable Long studyId,
                                            Model model,
                                            HttpServletRequest request)
            throws FileUploadException, IOException {

        model.addAttribute("study", studyRepository.findOne(studyId));

        // Return view
        return () -> {
            try {
                studyFileService.upload(file, studyId);
                studyFileService.createFileUploadEvent(studyId, currentUserDetailsService.getUserFromRequest(request));
                return "redirect:/studies/" + studyId + "/studyfiles";
            } catch (FileUploadException | IOException e) {
                getLog().error("File upload exception", e);
                return "error_pages/study_file_upload_failure";
            }
        };
    }

    @RequestMapping(value = "/{studyId}/tracking", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getStudyEvents(Model model, @PathVariable Long studyId) {
        model.addAttribute("events", eventsViewService.createViews(studyId));
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "study_events";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PubmedLookupException.class)
    public String handlePubmedLookupException(PubmedLookupException pubmedLookupException) {
        getLog().error("pubmed lookup exception", pubmedLookupException);
        return "pubmed_lookup_warning";
    }

    @ExceptionHandler(PubmedImportException.class)
    public String handlePubmedImportException(PubmedImportException pubmedImportException) {
        getLog().error("pubmed import exception", pubmedImportException);
        return "pubmed_import_warning";
    }

    @ExceptionHandler({FileNotFoundException.class})
    public String handleFileNotFound() {
        return "error_pages/file_not_found";
    }

    @ModelAttribute("diseaseTraits")
    public List<DiseaseTrait> populateDiseaseTraits() {
        return diseaseTraitService.getAllDiseaseTraits();
    }

    @ModelAttribute("diseaseTraitsHtml")
    public String populateDiseaseTraitsHtml() {
        return diseaseTraitService.getAllDiseaseTraitsHtml();
    }

    @ModelAttribute("efoTraits")
    public List<EfoTrait> populateEFOTraits() {
        return efoTraitService.getAllEFOTraits();
    }

    @ModelAttribute("efoTraitsHtml")
    public String populateEFOTraitsHtml() {
        return efoTraitService.getAllEFOTraitsHtml();
    }

    @ModelAttribute("curators")
    public List<Curator> populateCurators() {
        return cacheService.getAllCurators();
    }

    @ModelAttribute("platforms")
    public List<Platform> populatePlatforms() {
        return cacheService.getAllPlatforms();
    }

    @ModelAttribute("platformsHtml")
    public String populatePlatformsHtml() {
        return cacheService.getAllPlatformsHtml();
    }

    @ModelAttribute("genotypingTechnologies")
    public List<GenotypingTechnology> populateGenotypingTechnologies() {
        return cacheService.getAllGenotypingTechnologies();
    }

    @ModelAttribute("curationstatuses")
    public List<CurationStatus> populateCurationStatuses() {
        return cacheService.getAllCurationStatuses();
    }

    @ModelAttribute("unpublishreasons")
    public List<UnpublishReason> populateUnpublishReasons() {
        return cacheService.getAllUnpublishReasons();
    }


    @ModelAttribute("studyTypes")
    public List<String> populateStudyTypeOptions() {

        List<String> studyTypesOptions = new ArrayList<String>();
        studyTypesOptions.add("GXE");
        studyTypesOptions.add("GXG");
        studyTypesOptions.add("CNV");
        studyTypesOptions.add("Genome-wide genotyping array studies");
        studyTypesOptions.add("Targeted genotyping array studies");
        studyTypesOptions.add("Exome genotyping array studies");
        studyTypesOptions.add("Genome-wide sequencing studies");
        studyTypesOptions.add("Exome-wide sequencing studies");
        studyTypesOptions.add("Studies in curation queue");
        studyTypesOptions.add("Multi-SNP haplotype studies");
        studyTypesOptions.add("SNP Interaction studies");
        studyTypesOptions.add("p-Value Set");
        studyTypesOptions.add("User Requested");
        studyTypesOptions.add("Open Targets");
        return studyTypesOptions;
    }

    @ModelAttribute("qualifiers")
    public List<String> populateQualifierOptions() {
        List<String> qualifierOptions = new ArrayList<>();
        qualifierOptions.add("up to");
        qualifierOptions.add("at least");
        qualifierOptions.add("~");
        qualifierOptions.add(">");

        return qualifierOptions;
    }

    @ModelAttribute("authors")
    public List<String> populateAuthors() {
        return publicationOperationsService.listFirstAuthors();
    }


}
