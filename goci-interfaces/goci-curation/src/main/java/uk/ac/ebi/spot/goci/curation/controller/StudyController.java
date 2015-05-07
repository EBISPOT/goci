package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.exception.PubmedImportException;
import uk.ac.ebi.spot.goci.curation.model.PubmedIdForImport;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.curation.service.MailService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.DefaultPubMedSearchService;
import uk.ac.ebi.spot.goci.service.exception.PubmedLookupException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by emma on 20/11/14.
 *
 * @author emma Study Controller interprets user input and transform it into a study model that is represented to the
 *         user by the associated HTML page.
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
    private AssociationRepository associationRepository;
    private EthnicityRepository ethnicityRepository;

    // Pubmed ID lookup service
    private DefaultPubMedSearchService defaultPubMedSearchService;
    private MailService mailService;

    public static final int MAX_PAGE_ITEM_DISPLAY = 10;

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
                           AssociationRepository associationRepository,
                           EthnicityRepository ethnicityRepository,
                           DefaultPubMedSearchService defaultPubMedSearchService,
                           MailService mailService) {
        this.studyRepository = studyRepository;
        this.housekeepingRepository = housekeepingRepository;
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.associationRepository = associationRepository;
        this.ethnicityRepository = ethnicityRepository;
        this.defaultPubMedSearchService = defaultPubMedSearchService;
        this.mailService = mailService;
    }

    /* All studies and various filtered lists */
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String allStudiesPage(Model model,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) String pubmed,
                                 @RequestParam(required = false) String author,
                                 @RequestParam(required = false) String studyType,
                                 @RequestParam(required = false) Long efoTraitId,
                                 @RequestParam(required = false) Long status,
                                 @RequestParam(required = false) Long curator,
                                 @RequestParam(required = false) String sortType) {


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
            sortString = "&sortType=" + sortType;
        }

        // This is the default study page will all studies
        Page<Study> studyPage =
                studyRepository.findAll(constructPageSpecification(page - 1, sort));

        // Search by pubmed ID option available from landing page
        if (pubmed != null && !pubmed.isEmpty()) {
            studyPage =
                    studyRepository.findByPubmedId(pubmed, constructPageSpecification(page - 1, sort));
            filters = filters + "&pubmed=" + pubmed;
        }

        // Search by author option available from landing page
        if (author != null && !author.isEmpty()) {
            studyPage = studyRepository.findByAuthorContainingIgnoreCase(author, constructPageSpecification(page - 1,
                                                                                                            sort));
            filters = filters + "&author=" + author;
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
            studySearchFilter.setStudyType(studyType);
            filters = filters + "&studyType=" + studyType;
        }

        // Search by efo trait id
        if (efoTraitId != null) {
            studyPage = studyRepository.findByEfoTraitsId(efoTraitId, constructPageSpecification(page - 1,
                                                                                                 sort));
            studySearchFilter.setEfoTraitSearchFilterId(efoTraitId);
            filters = filters + "&efoTraitId=" + efoTraitId;
        }

        // If user entered a status
        if (status != null) {
            // If we have curator and status find by both
            if (curator != null) {
                studyPage = studyRepository.findByHousekeepingCurationStatusIdAndHousekeepingCuratorId(status,
                                                                                                       curator,
                                                                                                       constructPageSpecification(
                                                                                                               page - 1,
                                                                                                               sort));
                filters = filters + "&status=" + status + "&curator=" + curator;

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
        model.addAttribute("studies", studyPage);

        //Pagination variables
        long totalStudies = studyPage.getTotalElements();
        int current = studyPage.getNumber() + 1;
        int begin = Math.max(1, current - 5); // Returns the greater of two values
        int end = Math.min(begin + 10, studyPage.getTotalPages()); // how many pages to display in the pagination bar

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);
        model.addAttribute("totalStudies", totalStudies);

        // Add studySearchFilter to model so user can filter table
        model.addAttribute("studySearchFilter", studySearchFilter);

        return "studies";
    }

    // Redirects from landing page and main page
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String searchForStudyByFilter(@ModelAttribute StudySearchFilter studySearchFilter,
                                         Model model,
                                         @RequestParam(required = true) String filters) {

        // Get ids of objects searched for
        Long status = studySearchFilter.getStatusSearchFilterId();
        Long curator = studySearchFilter.getCuratorSearchFilterId();
        String pubmedId = studySearchFilter.getPubmedId();
        String author = studySearchFilter.getAuthor();
        String studyType = studySearchFilter.getStudyType();
        Long efoTraitId = studySearchFilter.getEfoTraitSearchFilterId();

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
            return "redirect:/studies?page=1&studyType=" + studyType;
        }

        // Search by efo trait
        else if (efoTraitId != null) {
            return "redirect:/studies?page=1&efoTraitId=" + efoTraitId;
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

   /* New Study*/

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
    public String importStudy(@ModelAttribute PubmedIdForImport pubmedIdForImport) throws PubmedImportException {

        // Remove whitespace
        String pubmedId = pubmedIdForImport.getPubmedId().trim();

        // Check if there is an existing study with the same pubmed id
        Collection<Study> existingStudies = studyRepository.findByPubmedId(pubmedId);
        if (existingStudies.size() > 0) {
            throw new PubmedImportException();
        }

        // Pass to importer
        Study importedStudy = defaultPubMedSearchService.findPublicationSummary(pubmedId);

        // Create housekeeping object
        Housekeeping studyHousekeeping = createHousekeeping();

        // Update and save study
        importedStudy.setHousekeeping(studyHousekeeping);
        Study newStudy = studyRepository.save(importedStudy);

        // Save new study
        studyRepository.save(importedStudy);
        return "redirect:/studies/" + importedStudy.getId();
    }


    // Save newly added study details
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addStudy(@Valid @ModelAttribute Study study, BindingResult bindingResult, Model model) {

        // If we have errors in the fields entered, i.e they are blank, then return these to form so user can fix
        if (bindingResult.hasErrors()) {
            model.addAttribute("study", study);

            // Return an empty pubmedIdForImport object to store user entered pubmed id
            model.addAttribute("pubmedIdForImport", new PubmedIdForImport());

            return "add_study";
        }

        // Create housekeeping object
        Housekeeping studyHousekeeping = createHousekeeping();

        // Update and save study
        study.setHousekeeping(studyHousekeeping);
        Study newStudy = studyRepository.save(study);

        return "redirect:/studies/" + newStudy.getId();
    }

   /* Exitsing study*/

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
    public String updateStudy(@ModelAttribute Study study,
                              Model model,
                              @PathVariable Long studyId,
                              RedirectAttributes redirectAttributes) {

        // Use id in URL to get study and then its associated housekeeping
        Study existingStudy = studyRepository.findOne(studyId);
        Housekeeping existingHousekeeping = existingStudy.getHousekeeping();

        // Set the housekeeping of the study returned to one already linked to it in database
        // Need to do this as we don't return housekeeping in form
        study.setHousekeeping(existingHousekeeping);

        // Saves the new information returned from form
        studyRepository.save(study);

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

        // If so warn the curator
        if (!associations.isEmpty()) {
            return "delete_study_with_associations_warning";

        }
        else {
            model.addAttribute("studyToDelete", studyToDelete);
            return "delete_study";
        }

    }

    @RequestMapping(value = "/{studyId}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String deleteStudy(@PathVariable Long studyId) {

        // Find our study based on the ID
        Study studyToDelete = studyRepository.findOne(studyId);

        // Before we delete the study get its associated housekeeping and ethnicity
        Long housekeepingId = studyToDelete.getHousekeeping().getId();
        Housekeeping housekeepingAttachedToStudy = housekeepingRepository.findOne(housekeepingId);
        Collection<Ethnicity> ethnicitiesAttachedToStudy = ethnicityRepository.findByStudyId(studyId);

        // Delete ethnicity information linked to this study
        for (Ethnicity ethnicity : ethnicitiesAttachedToStudy) {
            ethnicityRepository.delete(ethnicity);
        }

        // Delete study
        studyRepository.delete(studyToDelete);

        // Delete housekeeping
        housekeepingRepository.delete(housekeepingAttachedToStudy);

        return "redirect:/studies";
    }

    // Duplicate a study
    @RequestMapping(value = "/{studyId}/duplicate", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String duplicateStudy(@PathVariable Long studyId, RedirectAttributes redirectAttributes) {

        // Find study user wants to duplicate, based on the ID
        Study studyToDuplicate = studyRepository.findOne(studyId);

        // New study will be created by copying existing study details
        Study duplicateStudy = copyStudy(studyToDuplicate);

        // Create housekeeping object and add duplicate message
        Housekeeping studyHousekeeping = createHousekeeping();
        studyHousekeeping.setNotes(
                "Duplicate of study: " + studyToDuplicate.getAuthor() + ", PMID: " + studyToDuplicate.getPubmedId());
        duplicateStudy.setHousekeeping(studyHousekeeping);

        // Save newly duplicated study
        studyRepository.save(duplicateStudy);

        // Copy existing ethnicity
        Collection<Ethnicity> studyToDuplicateEthnicities = ethnicityRepository.findByStudyId(studyId);
        for (Ethnicity studyToDuplicateEthnicity : studyToDuplicateEthnicities) {
            Ethnicity duplicateEthnicity = copyEthnicity(studyToDuplicateEthnicity);
            duplicateEthnicity.setStudy(duplicateStudy);
            ethnicityRepository.save(duplicateEthnicity);
        }

        // Add duplicate message
        String message =
                "Study is a duplicate of " + studyToDuplicate.getAuthor() + ", PMID: " + studyToDuplicate.getPubmedId();
        redirectAttributes.addFlashAttribute("duplicateMessage", message);

        return "redirect:/studies/" + duplicateStudy.getId();
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
        return "study_housekeeping";
    }


    // Update page with housekeeping/curator information linked to a study
    @RequestMapping(value = "/{studyId}/housekeeping",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String updateStudyHousekeeping(@ModelAttribute Housekeeping housekeeping,
                                          @PathVariable Long studyId,
                                          RedirectAttributes redirectAttributes) {


        // Establish linked study
        Study study = studyRepository.findOne(studyId);

        // Before we save housekeeping get the status in database so we can check for a change
        CurationStatus statusInDatabase = housekeepingRepository.findOne(housekeeping.getId()).getCurationStatus();

        // Save housekeeping returned from form straight away to save any curator entered details like notes etc
        housekeepingRepository.save(housekeeping);

        // For the study check all SNPs have been checked
        Collection<Association> associations = associationRepository.findByStudyId(studyId);
        int snpsNotChecked = 0;
        for (Association association : associations) {
            // If we have one that is not checked set value
            if (association.getSnpChecked() == false) {
                snpsNotChecked = 1;
            }
        }

        // Establish whether user has set status to "Publish study" and "Send to NCBI"
        // as corresponding dates will be set in housekeeping table
        CurationStatus currentStatus = housekeeping.getCurationStatus();

        // If the status has changed
        if (currentStatus != statusInDatabase) {
            if (currentStatus != null && currentStatus.getStatus().equals("Publish study")) {

                // If not checked redirect back to page and make no changes
                if (snpsNotChecked == 1) {

                    // Restore old status
                    housekeeping.setCurationStatus(statusInDatabase);
                    // Save any changes made to housekeeping
                    housekeepingRepository.save(housekeeping);

                    String message =
                            "Some SNP associations have not been checked, please review before publishing";
                    redirectAttributes.addFlashAttribute("snpsNotChecked", message);
                    return "redirect:/studies/" + study.getId() + "/housekeeping";

                }

                else {
                    java.util.Date publishDate = new java.util.Date();
                    housekeeping.setPublishDate(publishDate);
                }
            }

            //Set date and send email notification
            if (currentStatus != null && currentStatus.getStatus().equals("Send to NCBI")) {
                // If not checked redirect back to page and make no changes
                if (snpsNotChecked == 1) {

                    // Restore old status
                    housekeeping.setCurationStatus(statusInDatabase);
                    // Save any changes made to housekeeping
                    housekeepingRepository.save(housekeeping);

                    String message =
                            "Some SNP associations have not been checked, please review before sending to NCBI";
                    redirectAttributes.addFlashAttribute("snpsNotChecked", message);
                    return "redirect:/studies/" + study.getId() + "/housekeeping";

                }

                else {
                    java.util.Date sendToNCBIDate = new java.util.Date();
                    housekeeping.setSendToNCBIDate(sendToNCBIDate);
                    mailService.sendEmailNotification(study, currentStatus.getStatus());
                }
            }

            // Send notification email to curators
            if (currentStatus != null && currentStatus.getStatus().equals("Level 1 curation done")) {
                mailService.sendEmailNotification(study, currentStatus.getStatus());
            }
        }


        // Save any changes made to housekeeping
        housekeepingRepository.save(housekeeping);

        // Set study housekeeping
        study.setHousekeeping(housekeeping);

        // Save our study
        studyRepository.save(study);

        // Add save message
        String message = "Changes saved successfully";
        redirectAttributes.addFlashAttribute("changesSaved", message);
        return "redirect:/studies/" + study.getId() + "/housekeeping";
    }


    /* General purpose methods */

    private Housekeeping createHousekeeping() {
        // Create housekeeping object and create the study added date
        Housekeeping housekeeping = new Housekeeping();
        java.util.Date studyAddedDate = new java.util.Date();
        housekeeping.setStudyAddedDate(studyAddedDate);

        // Set status
        CurationStatus status = curationStatusRepository.findByStatus("Awaiting Curation");
        housekeeping.setCurationStatus(status);

        // Set curator
        Curator curator = curatorRepository.findByLastName("Level 1 Curator");
        housekeeping.setCurator(curator);

        // Save housekeeping
        housekeepingRepository.save(housekeeping);

        // Save housekeeping
        return housekeeping;
    }

    private Study copyStudy(Study studyToDuplicate) {

        Study duplicateStudy = new Study();
        duplicateStudy.setAuthor(studyToDuplicate.getAuthor() + " DUP");
        duplicateStudy.setStudyDate(studyToDuplicate.getStudyDate());
        duplicateStudy.setPublication(studyToDuplicate.getPublication());
        duplicateStudy.setTitle(studyToDuplicate.getTitle());
        duplicateStudy.setInitialSampleSize(studyToDuplicate.getInitialSampleSize());
        duplicateStudy.setReplicateSampleSize(studyToDuplicate.getReplicateSampleSize());
        duplicateStudy.setPlatform(studyToDuplicate.getPlatform());
        duplicateStudy.setPubmedId(studyToDuplicate.getPubmedId());
        duplicateStudy.setCnv(studyToDuplicate.getCnv());
        duplicateStudy.setGxe(studyToDuplicate.getGxe());
        duplicateStudy.setGxg(studyToDuplicate.getGxg());
        duplicateStudy.setDiseaseTrait(studyToDuplicate.getDiseaseTrait());

        // Deal with EFO traits
        Collection<EfoTrait> efoTraits = studyToDuplicate.getEfoTraits();
        Collection<EfoTrait> efoTraitsDuplicateStudy = new ArrayList<EfoTrait>();

        if (efoTraits != null && !efoTraits.isEmpty()) {
            efoTraitsDuplicateStudy.addAll(efoTraits);
            duplicateStudy.setEfoTraits(efoTraitsDuplicateStudy);
        }


        return duplicateStudy;
    }

    private Ethnicity copyEthnicity(Ethnicity studyToDuplicateEthnicity) {
        Ethnicity duplicateEthnicity = new Ethnicity();
        duplicateEthnicity.setType(studyToDuplicateEthnicity.getType());
        duplicateEthnicity.setNumberOfIndividuals(studyToDuplicateEthnicity.getNumberOfIndividuals());
        duplicateEthnicity.setEthnicGroup(studyToDuplicateEthnicity.getEthnicGroup());
        duplicateEthnicity.setCountryOfOrigin(studyToDuplicateEthnicity.getCountryOfOrigin());
        duplicateEthnicity.setCountryOfRecruitment(studyToDuplicateEthnicity.getCountryOfRecruitment());
        duplicateEthnicity.setDescription(studyToDuplicateEthnicity.getDescription());
        duplicateEthnicity.setPreviouslyReported(studyToDuplicateEthnicity.getPreviouslyReported());
        duplicateEthnicity.setSampleSizesMatch(studyToDuplicateEthnicity.getSampleSizesMatch());
        duplicateEthnicity.setNotes(studyToDuplicateEthnicity.getNotes());

        return duplicateEthnicity;

    }

    // Find correct sorting type and direction
    private Sort findSort(String sortType) {

        // Default sort by date
        Sort sort = sortByStudyDateDesc();

        Map<String, Sort> sortTypeMap = new HashMap<>();
        sortTypeMap.put("authorsortasc", sortByAuthorAsc());
        sortTypeMap.put("authorsortdesc", sortByAuthorDesc());
        sortTypeMap.put("titlesortasc", sortByTitleAsc());
        sortTypeMap.put("titlesortdesc", sortByTitleDesc());
        sortTypeMap.put("studydatesortasc", sortByStudyDateAsc());
        sortTypeMap.put("studydatesortdesc", sortByStudyDateDesc());
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


    /* Model Attributes :
    *  Used for dropdowns in HTML forms
    */

    // Disease Traits
    @ModelAttribute("diseaseTraits")
    public List<DiseaseTrait> populateDiseaseTraits(Model model) {
        return diseaseTraitRepository.findAll(sortByTraitAsc());
    }

    // EFO traits
    @ModelAttribute("efoTraits")
    public List<EfoTrait> populateEFOTraits(Model model) {
        return efoTraitRepository.findAll(sortByTraitAsc());
    }

    // Curators
    @ModelAttribute("curators")
    public List<Curator> populateCurators(Model model) {
        return curatorRepository.findAll();
    }

    // Curation statuses
    @ModelAttribute("curationstatuses")
    public List<CurationStatus> populateCurationStatuses(Model model) {
        return curationStatusRepository.findAll();
    }


    // Study types
    @ModelAttribute("studyTypes")
    public List<String> populateStudyTypeOptions(Model model) {

        List<String> studyTypesOptions = new ArrayList<String>();
        studyTypesOptions.add("GXE");
        studyTypesOptions.add("GXG");
        studyTypesOptions.add("CNV");
        return studyTypesOptions;
    }


    /* Sorting options */

    // Returns a Sort object which sorts disease traits in ascending order by trait, ignoring case
    private Sort sortByTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "trait").ignoreCase());
    }

    private Sort sortByStudyDateAsc() {return new Sort(new Sort.Order(Sort.Direction.ASC, "studyDate"));}

    private Sort sortByStudyDateDesc() {return new Sort(new Sort.Order(Sort.Direction.DESC, "studyDate"));}

    private Sort sortByAuthorAsc() {return new Sort(new Sort.Order(Sort.Direction.ASC, "author"));}

    private Sort sortByAuthorDesc() {return new Sort(new Sort.Order(Sort.Direction.DESC, "author"));}

    private Sort sortByTitleAsc() {return new Sort(new Sort.Order(Sort.Direction.ASC, "title"));}

    private Sort sortByTitleDesc() {return new Sort(new Sort.Order(Sort.Direction.DESC, "title"));}

    private Sort sortByPublicationAsc() {return new Sort(new Sort.Order(Sort.Direction.ASC, "publication"));}

    private Sort sortByPublicationDesc() {return new Sort(new Sort.Order(Sort.Direction.DESC, "publication"));}

    private Sort sortByPubmedIdAsc() {return new Sort(new Sort.Order(Sort.Direction.ASC, "pubmedId"));}

    private Sort sortByPubmedIdDesc() {return new Sort(new Sort.Order(Sort.Direction.DESC, "pubmedId"));}

    private Sort sortByDiseaseTraitAsc() {return new Sort(new Sort.Order(Sort.Direction.ASC, "diseaseTrait.trait").ignoreCase());}

    private Sort sortByDiseaseTraitDesc() {return new Sort(new Sort.Order(Sort.Direction.DESC, "diseaseTrait.trait").ignoreCase());}

    private Sort sortByEfoTraitAsc() {return new Sort(new Sort.Order(Sort.Direction.ASC, "efoTraits.trait").ignoreCase());}

    private Sort sortByEfoTraitDesc() {return new Sort(new Sort.Order(Sort.Direction.DESC, "efoTraits.trait").ignoreCase());}

    private Sort sortByCuratorAsc() {return new Sort(new Sort.Order(Sort.Direction.ASC, "housekeeping.curator"));}

    private Sort sortByCuratorDesc() {return new Sort(new Sort.Order(Sort.Direction.DESC, "housekeeping.curator"));}

    private Sort sortByCurationStatusAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC,
                                       "housekeeping.curationStatus"));
    }

    private Sort sortByCurationStatusDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC,
                                       "housekeeping.curationStatus"));
    }

    /* Pagination */
    // Pagination, method passed page index and inlcudes max number of studies, sorted by study date, to return
    private Pageable constructPageSpecification(int pageIndex, Sort sort) {
        Pageable pageSpecification = new PageRequest(pageIndex, MAX_PAGE_ITEM_DISPLAY, sort);
        return pageSpecification;
    }

}
