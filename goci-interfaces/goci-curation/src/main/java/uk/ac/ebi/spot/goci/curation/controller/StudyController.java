package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;
import uk.ac.ebi.spot.goci.curation.exception.NoStudyDirectoryException;
import uk.ac.ebi.spot.goci.curation.exception.PubmedImportException;
import uk.ac.ebi.spot.goci.curation.model.Assignee;
import uk.ac.ebi.spot.goci.curation.model.PubmedIdForImport;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.EventsViewService;
import uk.ac.ebi.spot.goci.curation.service.MappingDetailsService;
import uk.ac.ebi.spot.goci.curation.service.StudyDeletionService;
import uk.ac.ebi.spot.goci.curation.service.StudyDuplicationService;
import uk.ac.ebi.spot.goci.curation.service.StudyFileService;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.curation.service.StudyUpdateService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Platform;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.UnpublishReason;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.PlatformRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.repository.UnpublishReasonRepository;
import uk.ac.ebi.spot.goci.service.DefaultPubMedSearchService;
import uk.ac.ebi.spot.goci.service.exception.PubmedLookupException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by emma on 20/11/14.
 *
 * @author emma
 *         <p>
 *         Study Controller interprets user input and transform it into a study model that is represented to the user by
 *         the associated HTML page.
 */

@Controller
@RequestMapping("/studies")
public class StudyController {

    // Repositories allowing access to database objects associated with a study
    private StudyRepository studyRepository;
    private HousekeepingRepository housekeepingRepository;
    private DiseaseTraitRepository diseaseTraitRepository;
    private EfoTraitRepository efoTraitRepository;
    private CuratorRepository curatorRepository;
    private CurationStatusRepository curationStatusRepository;
    private PlatformRepository platformRepository;
    private AssociationRepository associationRepository;
    private EthnicityRepository ethnicityRepository;
    private UnpublishReasonRepository unpublishReasonRepository;

    // Services
    private DefaultPubMedSearchService defaultPubMedSearchService;
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
                           DiseaseTraitRepository diseaseTraitRepository,
                           EfoTraitRepository efoTraitRepository,
                           CuratorRepository curatorRepository,
                           CurationStatusRepository curationStatusRepository,
                           PlatformRepository platformRepository,
                           AssociationRepository associationRepository,
                           EthnicityRepository ethnicityRepository,
                           UnpublishReasonRepository unpublishReasonRepository,
                           DefaultPubMedSearchService defaultPubMedSearchService,
                           StudyOperationsService studyOperationsService,
                           MappingDetailsService mappingDetailsService,
                           CurrentUserDetailsService currentUserDetailsService,
                           StudyFileService studyFileService,
                           StudyDuplicationService studyDuplicationService,
                           StudyDeletionService studyDeletionService,
                           @Qualifier("studyEventsViewService") EventsViewService eventsViewService,
                           StudyUpdateService studyUpdateService) {
        this.studyRepository = studyRepository;
        this.housekeepingRepository = housekeepingRepository;
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.platformRepository = platformRepository;
        this.associationRepository = associationRepository;
        this.ethnicityRepository = ethnicityRepository;
        this.unpublishReasonRepository = unpublishReasonRepository;
        this.defaultPubMedSearchService = defaultPubMedSearchService;
        this.studyOperationsService = studyOperationsService;
        this.mappingDetailsService = mappingDetailsService;
        this.currentUserDetailsService = currentUserDetailsService;
        this.studyFileService = studyFileService;
        this.studyDuplicationService = studyDuplicationService;
        this.studyDeletionService = studyDeletionService;
        this.eventsViewService = eventsViewService;
        this.studyUpdateService = studyUpdateService;
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
                                 @RequestParam(required = false) Long status,
                                 @RequestParam(required = false) Long curator,
                                 @RequestParam(value = "sorttype", required = false) String sortType,
                                 @RequestParam(value = "diseasetraitid", required = false) Long diseaseTraitId,
                                 @RequestParam(required = false) Integer year,
                                 @RequestParam(required = false) Integer month) {


        // This is passed back to model and determines if pagination is applied
        Boolean pagination = true;

        // Return all studies ordered by date if no page number given
        if (page == null) {
            // Find all studies ordered by study date and only display first page
            return "redirect:/studies?page=1";
        }

        // This will be returned to view and store what curator has searched for
        StudySearchFilter studySearchFilter = new StudySearchFilter();

        // Store filters which will be need for pagination bar and to build URI passed back to view
        String filters = "";

        // Set sort object and sort string for URI
        Sort sort = findSort(sortType);
        String sortString = "";
        if (sortType != null && !sortType.isEmpty()) {
            sortString = "&sorttype=" + sortType;
        }

        // This is the default study page will all studies
        Page<Study> studyPage = studyRepository.findAll(constructPageSpecification(page - 1, sort));

        // For multi-snp and snp interaction studies pagination is not applied as the query leads to duplicates
        List<Study> studies = null;


        // Search by pubmed ID option available from landing page
        if (pubmed != null && !pubmed.isEmpty()) {
            studyPage =
                    studyRepository.findByPubmedId(pubmed, constructPageSpecification(page - 1, sort));
            filters = filters + "&pubmed=" + pubmed;
            studySearchFilter.setPubmedId(pubmed);
        }

        // Search by author option available from landing page
        if (author != null && !author.isEmpty()) {
            studyPage = studyRepository.findByAuthorContainingIgnoreCase(author, constructPageSpecification(page - 1,
                                                                                                            sort));
            filters = filters + "&author=" + author;
            studySearchFilter.setAuthor(author);
        }

        // Search by study type
        if (studyType != null && !studyType.isEmpty()) {

            if (studyType.equals("GXE")) {
                studyPage = studyRepository.findByGxe(true, constructPageSpecification(page - 1,
                                                                                       sort));
            }
            if (studyType.equals("GXG")) {
                studyPage = studyRepository.findByGxg(true, constructPageSpecification(page - 1,
                                                                                       sort));
            }

            if (studyType.equals("CNV")) {
                studyPage = studyRepository.findByCnv(true, constructPageSpecification(page - 1,
                                                                                       sort));
            }

            if (studyType.equals("Genomewide array studies")) {
                studyPage = studyRepository.findByGenomewideArray(true, constructPageSpecification(page - 1,
                                                                                                   sort));
            }

            if (studyType.equals("Targeted array studies")) {
                studyPage = studyRepository.findByTargetedArray(true, constructPageSpecification(page - 1,
                                                                                                 sort));
            }

            if (studyType.equals("Studies in curation queue")) {
                CurationStatus errorStatus = curationStatusRepository.findByStatus("Publish study");
                Long errorStatusId = errorStatus.getId();
                studyPage = studyRepository.findByHousekeepingCurationStatusIdNot(errorStatusId,
                                                                                  constructPageSpecification(
                                                                                          page -
                                                                                                  1,
                                                                                          sort));
            }

            if (studyType.equals("p-Value Set")) {
                studyPage = studyRepository.findByFullPvalueSet(true,constructPageSpecification(page - 1,
                        sort));
            }

            if (studyType.equals("Multi-SNP haplotype studies")) {
                studies = studyRepository.findStudyDistinctByAssociationsMultiSnpHaplotypeTrue(sort);
                pagination = false;
            }

            if (studyType.equals("SNP Interaction studies")) {
                studies = studyRepository.findStudyDistinctByAssociationsSnpInteractionTrue(sort);
                pagination = false;
            }

            studySearchFilter.setStudyType(studyType);
            filters = filters + "&studytype=" + studyType;
        }

        // Search by efo trait id
        if (efoTraitId != null) {
            studyPage = studyRepository.findByEfoTraitsId(efoTraitId, constructPageSpecification(page - 1,
                                                                                                 sort));
            studySearchFilter.setEfoTraitSearchFilterId(efoTraitId);
            filters = filters + "&efotraitid=" + efoTraitId;
        }

        // Search by disease trait id
        if (diseaseTraitId != null) {
            studyPage = studyRepository.findByDiseaseTraitId(diseaseTraitId, constructPageSpecification(page - 1,
                                                                                                        sort));
            studySearchFilter.setDiseaseTraitSearchFilterId(diseaseTraitId);
            filters = filters + "&diseasetraitid=" + diseaseTraitId;
        }


        // Search by notes for entered string
        if (notesQuery != null && !notesQuery.isEmpty()) {
            studyPage = studyRepository.findByHousekeepingNotesContainingIgnoreCase(notesQuery,
                                                                                    constructPageSpecification(page - 1,
                                                                                                               sort));
            studySearchFilter.setNotesQuery(notesQuery);
            filters = filters + "&notesquery=" + notesQuery;
        }

        // If user entered a status
        if (status != null) {
            // If we have curator and status find by both
            if (curator != null) {

                // This is just used to link from reports tab
                if (year != null && month != null) {
                    studyPage = studyRepository.findByPublicationDateAndCuratorAndStatus(curator,
                                                                                         status,
                                                                                         year,
                                                                                         month,
                                                                                         constructPageSpecification(
                                                                                                 page - 1,
                                                                                                 sort));

                    studySearchFilter.setMonthFilter(month);
                    studySearchFilter.setYearFilter(year);
                    filters =
                            filters + "&status=" + status + "&curator=" + curator + "&year=" + year + "&month=" + month;

                }
                else {

                    studyPage = studyRepository.findByHousekeepingCurationStatusIdAndHousekeepingCuratorId(status,
                                                                                                           curator,
                                                                                                           constructPageSpecification(
                                                                                                                   page -
                                                                                                                           1,
                                                                                                                   sort));
                    filters = filters + "&status=" + status + "&curator=" + curator;
                }

                // Return these values so they appear in filter results
                studySearchFilter.setCuratorSearchFilterId(curator);
                studySearchFilter.setStatusSearchFilterId(status);

            }
            else {
                studyPage = studyRepository.findByHousekeepingCurationStatusId(status, constructPageSpecification(
                        page - 1,
                        sort));
                filters = filters + "&status=" + status;

                // Return this value so it appears in filter result
                studySearchFilter.setStatusSearchFilterId(status);

            }
        }
        // If user entered curator
        else {
            if (curator != null) {
                studyPage = studyRepository.findByHousekeepingCuratorId(curator, constructPageSpecification(
                        page - 1,
                        sort));
                filters = filters + "&curator=" + curator;

                // Return this value so it appears in filter result
                studySearchFilter.setCuratorSearchFilterId(curator);
            }

        }

        // Return URI, this will build thymeleaf links using by sort buttons.
        // At present, do not add the current sort to the URI,
        // just maintain any filter values (pubmed id, author etc) used by curator
        String uri = "/studies?page=1";
        if (!filters.isEmpty()) {
            uri = uri + filters;
        }
        model.addAttribute("uri", uri);

        // Return study page and filters,
        // filters will be used by pagination bar
        if (!filters.isEmpty()) {
            if (!sortString.isEmpty()) {
                filters = filters + sortString;
            }
        }
        // If user has just sorted without any filter we need
        // to pass this back to pagination bar
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

        }
        else {
            model.addAttribute("studies", studies);
            totalStudies = studies.size();
        }
        model.addAttribute("totalStudies", totalStudies);
        model.addAttribute("pagination", pagination);

        // Add studySearchFilter to model so user can filter table
        model.addAttribute("studySearchFilter", studySearchFilter);

        // Add assignee and status assignment so user can assign
        // study to curator or assign a status
        // Also set uri so we can redirect to page user was on
        Assignee assignee = new Assignee();
        StatusAssignment statusAssignment = new StatusAssignment();
        assignee.setUri("/studies?page=" + current + filters);
        statusAssignment.setUri("/studies?page=" + current + filters);
        model.addAttribute("assignee", assignee);
        model.addAttribute("statusAssignment", statusAssignment);

        return "studies";
    }

    // Redirects from landing page and main page
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
            }
            else {
                return "redirect:/studies?page=1&status=" + status;
            }
        }
        // If user entered curator
        else if (curator != null) {
            return "redirect:/studies?page=1&curator=" + curator;
        }

        // If all else fails return all studies
        else {
            // Find all studies ordered by study date and only display first page
            return "redirect:/studies?page=1";
        }

    }



   /* New Study:
   *
   * Adding a study is synchronised to ensure the method can only be accessed once.
   *
   * */

    // Add a new study
    // Directs user to an empty form to which they can create a new study
    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String newStudyForm(Model model) {
        model.addAttribute("study", new Study());

        // Return an empty pubmedIdForImport object to store user entered pubmed id
        model.addAttribute("pubmedIdForImport", new PubmedIdForImport());
        return "add_study";
    }


    // Save study found by Pubmed Id
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/new/import", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public synchronized String importStudy(@ModelAttribute PubmedIdForImport pubmedIdForImport,
                                           HttpServletRequest request,
                                           Model model)
            throws PubmedImportException, NoStudyDirectoryException {

        // Remove whitespace
        String pubmedId = pubmedIdForImport.getPubmedId().trim();

        // Check if there is an existing study with the same pubmed id
        Collection<Study> existingStudies = studyRepository.findByPubmedId(pubmedId);
        if (existingStudies.size() > 0) {
            throw new PubmedImportException();
        }

        else {
            // Pass to importer
            Study importedStudy = defaultPubMedSearchService.findPublicationSummary(pubmedId);
            Study savedStudy = studyOperationsService.createStudy(importedStudy,
                                                                  currentUserDetailsService.getUserFromRequest(request));

            // Create directory to store associated files
            try {
                studyFileService.createStudyDir(savedStudy.getId());
            }
            catch (NoStudyDirectoryException e) {
                getLog().error("No study directory exception");
                model.addAttribute("study", savedStudy);
                return "error_pages/study_dir_failure";
            }

            return "redirect:/studies/" + savedStudy.getId();
        }
    }


    // Save newly added study details
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public synchronized String addStudy(@Valid @ModelAttribute Study study,
                                        BindingResult bindingResult,
                                        Model model,
                                        HttpServletRequest request) throws NoStudyDirectoryException {


        // If we have errors in the fields entered, i.e they are blank, then return these to form so user can fix
        if (bindingResult.hasErrors()) {
            model.addAttribute("study", study);

            // Return an empty pubmedIdForImport object to store user entered pubmed id
            model.addAttribute("pubmedIdForImport", new PubmedIdForImport());

            return "add_study";
        }

        Study savedStudy =
                studyOperationsService.createStudy(study, currentUserDetailsService.getUserFromRequest(request));
        // Create directory to store associated files
        try {
            studyFileService.createStudyDir(savedStudy.getId());
        }
        catch (NoStudyDirectoryException e) {
            getLog().error("No study directory exception");
            model.addAttribute("study", savedStudy);
            return "error_pages/study_dir_failure";
        }
        return "redirect:/studies/" + savedStudy.getId();
    }

   /* Existing study*/

    // View a study
    @RequestMapping(value = "/{studyId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudy(Model model, @PathVariable Long studyId) {
        Study studyToView = studyRepository.findOne(studyId);
        model.addAttribute("study", studyToView);
        return "study";
    }

    // Edit an existing study
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/{studyId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String updateStudy(@ModelAttribute Study study, @PathVariable Long studyId,
                              RedirectAttributes redirectAttributes, HttpServletRequest request) {

        studyUpdateService.updateStudy(studyId, study, currentUserDetailsService.getUserFromRequest(request));

        // Add save message
        String message = "Changes saved successfully";
        redirectAttributes.addFlashAttribute("changesSaved", message);
        return "redirect:/studies/" + study.getId();
    }


    // Delete an existing study
    @RequestMapping(value = "/{studyId}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudyToDelete(Model model, @PathVariable Long studyId) {

        Study studyToDelete = studyRepository.findOne(studyId);

        // Check if it has any associations
        Collection<Association> associations = associationRepository.findByStudyId(studyId);

        Long housekeepingId = studyToDelete.getHousekeeping().getId();
        Housekeeping housekeepingAttachedToStudy = housekeepingRepository.findOne(housekeepingId);

        model.addAttribute("studyToDelete", studyToDelete);

        if (housekeepingAttachedToStudy.getCatalogPublishDate() != null) {
            return "delete_published_study_warning";
        }
        else if (!associations.isEmpty()) {
            return "delete_study_with_associations_warning";
        }
        else {
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

    // Duplicate a study
    @RequestMapping(value = "/{studyId}/duplicate", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String duplicateStudy(@PathVariable Long studyId,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {

        // Find study user wants to duplicate, based on the ID
        Study studyToDuplicate = studyRepository.findOne(studyId);
        Study duplicateStudy = studyDuplicationService.duplicateStudy(studyToDuplicate,
                                                                      currentUserDetailsService.getUserFromRequest(
                                                                              request));

        // Add duplicate message
        String message =
                "Study is a duplicate of " + studyToDuplicate.getAuthor() + ", PMID: " + studyToDuplicate.getPubmedId();
        redirectAttributes.addFlashAttribute("duplicateMessage", message);

        return "redirect:/studies/" + duplicateStudy.getId();
    }

    // Assign a curator to a study
    @RequestMapping(value = "/{studyId}/assign", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String assignStudyCurator(@PathVariable Long studyId,
                                     @ModelAttribute Assignee assignee,
                                     RedirectAttributes redirectAttributes, HttpServletRequest request) {

        // Find the study and the curator user wishes to assign
        Study study = studyRepository.findOne(studyId);

        if (assignee.getCuratorId() == null) {
            String blankAssignee =
                    "Cannot assign a blank value as a curator for study: " + study.getAuthor() + ", " + " pubmed = " +
                            study.getPubmedId();
            redirectAttributes.addFlashAttribute("blankAssignee", blankAssignee);
        }
        else {
            studyOperationsService.assignStudyCurator(study,
                                                      assignee,
                                                      currentUserDetailsService.getUserFromRequest(request));
        }
        return "redirect:" + assignee.getUri();
    }

    // Assign a status to a study
    @RequestMapping(value = "/{studyId}/status_update",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String assignStudyStatus(@PathVariable Long studyId,
                                    @ModelAttribute StatusAssignment statusAssignment,
                                    RedirectAttributes redirectAttributes, HttpServletRequest request) {

        // Find the study and the curator user wishes to assign
        Study study = studyRepository.findOne(studyId);

        if (statusAssignment.getStatusId() == null) {
            String blankStatus =
                    "Cannot assign a blank value as a status for study: " + study.getAuthor() + ", " + " pubmed = " +
                            study.getPubmedId();
            redirectAttributes.addFlashAttribute("blankStatus", blankStatus);
        }
        else {
            String message = studyOperationsService.assignStudyStatus(study,
                                                                      statusAssignment,
                                                                      currentUserDetailsService.getUserFromRequest(
                                                                              request));
            redirectAttributes.addFlashAttribute("studySnpsNotApproved", message);
        }
        return "redirect:" + statusAssignment.getUri();
    }


    /* Study housekeeping/curator information */

    // Generate page with housekeeping/curator information linked to a study
    @RequestMapping(value = "/{studyId}/housekeeping", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudyHousekeeping(Model model, @PathVariable Long studyId) {

        // Find study
        Study study = studyRepository.findOne(studyId);

        // If we don't have a housekeeping object create one, this should not occur though as they are created when study is created
        if (study.getHousekeeping() == null) {
            model.addAttribute("studyHousekeeping", new Housekeeping());
        }
        else {
            model.addAttribute("studyHousekeeping", study.getHousekeeping());
        }

        // Return the housekeeping object attached to study and return the study
        model.addAttribute("study", study);

        // Return a DTO that holds a summary of any automated mappings
        model.addAttribute("mappingDetails", mappingDetailsService.createMappingSummary(study));

        return "study_housekeeping";
    }


    // Update page with housekeeping/curator information linked to a study
    @RequestMapping(value = "/{studyId}/housekeeping",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String updateStudyHousekeeping(@ModelAttribute Housekeeping housekeeping,
                                          @PathVariable Long studyId,
                                          RedirectAttributes redirectAttributes, HttpServletRequest request) {


        // Establish linked study
        Study study = studyRepository.findOne(studyId);

        // Update housekeeping
        String message = studyOperationsService.updateHousekeeping(housekeeping,
                                                                   study,
                                                                   currentUserDetailsService.getUserFromRequest(request));

        // Add save message
        if (message == null) {
            message = "Changes saved successfully";
        }

        redirectAttributes.addFlashAttribute("changesSaved", message);
        return "redirect:/studies/" + study.getId() + "/housekeeping";
    }


    @RequestMapping(value = "/{studyId}/unpublish", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudyToUnpublish(Model model, @PathVariable Long studyId) {

        Study studyToUnpublish = studyRepository.findOne(studyId);

        // Check if it has any associations or ethnicity information
//        Collection<Association> associations = associationRepository.findByStudyId(studyId);
//        Collection<Ethnicity> ancestryInfo = ethnicityRepository.findByStudyId(studyId);
//
//        // If so warn the curator
//        if (!associations.isEmpty() || !ancestryInfo.isEmpty()) {
//            model.addAttribute("study", studyToUnpublish);
//            return "unpublish_study_with_associations_warning";
//
//        }
//        else {
            model.addAttribute("studyToUnpublish", studyToUnpublish);
            return "unpublish_study";
//        }

    }

    @RequestMapping(value = "/{studyId}/unpublish", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String unpublishStudy(@ModelAttribute Study studyToUnpublish, @PathVariable Long studyId,
                                 HttpServletRequest request) {

        // studyToUnpuplish attribute is used simply to retrieve unpublsih reason
        studyOperationsService.unpublishStudy(studyId,
                                              studyToUnpublish.getHousekeeping().getUnpublishReason(),
                                              currentUserDetailsService.getUserFromRequest(request));
        return "redirect:/studies/" + studyToUnpublish.getId() + "/housekeeping";
    }


    // Find correct sorting type and direction
    private Sort findSort(String sortType) {

        // Default sort by date
        Sort sort = sortByPublicationDateDesc();

        Map<String, Sort> sortTypeMap = new HashMap<>();
        sortTypeMap.put("authorsortasc", sortByAuthorAsc());
        sortTypeMap.put("authorsortdesc", sortByAuthorDesc());
        sortTypeMap.put("titlesortasc", sortByTitleAsc());
        sortTypeMap.put("titlesortdesc", sortByTitleDesc());
        sortTypeMap.put("publicationdatesortasc", sortByPublicationDateAsc());
        sortTypeMap.put("publicationdatesortdesc", sortByPublicationDateDesc());
        sortTypeMap.put("pubmedsortasc", sortByPubmedIdAsc());
        sortTypeMap.put("pubmedsortdesc", sortByPubmedIdDesc());
        sortTypeMap.put("publicationsortasc", sortByPublicationAsc());
        sortTypeMap.put("publicationsortdesc", sortByPublicationDesc());
        sortTypeMap.put("efotraitsortasc", sortByEfoTraitAsc());
        sortTypeMap.put("efotraitsortdesc", sortByEfoTraitDesc());
        sortTypeMap.put("diseasetraitsortasc", sortByDiseaseTraitAsc());
        sortTypeMap.put("diseasetraitsortdesc", sortByDiseaseTraitDesc());
        sortTypeMap.put("curatorsortasc", sortByCuratorAsc());
        sortTypeMap.put("curatorsortdesc", sortByCuratorDesc());
        sortTypeMap.put("curationstatussortasc", sortByCurationStatusAsc());
        sortTypeMap.put("curationstatussortdesc", sortByCurationStatusDesc());


        if (sortType != null && !sortType.isEmpty()) {
            sort = sortTypeMap.get(sortType);
        }

        return sort;
    }

    /* Functionality to view, upload and download a study file(s)*/
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

        // Using this logic so can get full file name, if it was an @PathVariable everything after final '.' would be removed
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
            }
            catch (FileUploadException | IOException e) {
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

    /* Exception handling */

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

    /* Model Attributes :
    *  Used for dropdowns in HTML forms
    */

    // Disease Traits
    @ModelAttribute("diseaseTraits")
    public List<DiseaseTrait> populateDiseaseTraits() {
        return diseaseTraitRepository.findAll(sortByTraitAsc());
    }

    // EFO traits
    @ModelAttribute("efoTraits")
    public List<EfoTrait> populateEFOTraits() {
        return efoTraitRepository.findAll(sortByTraitAsc());
    }

    // Curators
    @ModelAttribute("curators")
    public List<Curator> populateCurators() {
        return curatorRepository.findAll(sortByLastNameAsc());
    }

    //Platforms
    @ModelAttribute("platforms")
    public List<Platform> populatePlatforms() {return platformRepository.findAll(); }


    // Curation statuses
    @ModelAttribute("curationstatuses")
    public List<CurationStatus> populateCurationStatuses() {
        return curationStatusRepository.findAll(sortByStatusAsc());
    }

    // Unpublish reasons
    @ModelAttribute("unpublishreasons")
    public List<UnpublishReason> populateUnpublishReasons() {
        return unpublishReasonRepository.findAll();
    }


    // Study types
    @ModelAttribute("studyTypes")
    public List<String> populateStudyTypeOptions() {

        List<String> studyTypesOptions = new ArrayList<String>();
        studyTypesOptions.add("GXE");
        studyTypesOptions.add("GXG");
        studyTypesOptions.add("CNV");
        studyTypesOptions.add("Genomewide array studies");
        studyTypesOptions.add("Targeted array studies");
        studyTypesOptions.add("Studies in curation queue");
        studyTypesOptions.add("Multi-SNP haplotype studies");
        studyTypesOptions.add("SNP Interaction studies");
        studyTypesOptions.add("p-Value Set");
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

    // Authors
    @ModelAttribute("authors")
    public List<String> populateAuthors() {
        return studyRepository.findAllStudyAuthors(sortByAuthorAsc());
    }


    /* Sorting options */

    private Sort sortByLastNameAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "lastName").ignoreCase());
    }

    private Sort sortByStatusAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "status").ignoreCase());
    }

    // Returns a Sort object which sorts disease traits in ascending order by trait, ignoring case
    private Sort sortByTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "trait").ignoreCase());
    }

    private Sort sortByPublicationDateAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "publicationDate"));
    }

    private Sort sortByPublicationDateDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "publicationDate"));
    }

    private Sort sortByAuthorAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "author").ignoreCase());
    }

    private Sort sortByAuthorDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "author").ignoreCase());
    }

    private Sort sortByTitleAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "title").ignoreCase());
    }

    private Sort sortByTitleDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "title").ignoreCase());
    }

    private Sort sortByPublicationAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "publication").ignoreCase());
    }

    private Sort sortByPublicationDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "publication").ignoreCase());
    }

    private Sort sortByPubmedIdAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "pubmedId"));
    }

    private Sort sortByPubmedIdDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "pubmedId"));
    }

    private Sort sortByDiseaseTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "diseaseTrait.trait").ignoreCase());
    }

    private Sort sortByDiseaseTraitDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "diseaseTrait.trait").ignoreCase());
    }

    private Sort sortByEfoTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "efoTraits.trait").ignoreCase());
    }

    private Sort sortByEfoTraitDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "efoTraits.trait").ignoreCase());
    }

    private Sort sortByCuratorAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "housekeeping.curator.lastName").ignoreCase());
    }

    private Sort sortByCuratorDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "housekeeping.curator.lastName").ignoreCase());
    }

    private Sort sortByCurationStatusAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC,
                                       "housekeeping.curationStatus.status"));
    }

    private Sort sortByCurationStatusDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC,
                                       "housekeeping.curationStatus.status"));
    }

    /* Pagination */
    // Pagination, method passed page index and includes max number of studies, sorted by study date, to return
    private Pageable constructPageSpecification(int pageIndex, Sort sort) {
        return new PageRequest(pageIndex, MAX_PAGE_ITEM_DISPLAY, sort);
    }
}