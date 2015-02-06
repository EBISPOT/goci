package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.exception.PubmedImportException;
import uk.ac.ebi.spot.goci.curation.model.PubmedIdForImport;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.PropertyFilePubMedLookupService;
import uk.ac.ebi.spot.goci.service.exception.PubmedLookupException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 20/11/14.
 *
 * @author emma
 *         Study Controller interprets user input and transform it into a study
 *         model that is represented to the user by the associated HTML page.
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
    private PropertyFilePubMedLookupService propertyFilePubMedLookupService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public StudyController(StudyRepository studyRepository, HousekeepingRepository housekeepingRepository, DiseaseTraitRepository diseaseTraitRepository, EfoTraitRepository efoTraitRepository, CuratorRepository curatorRepository, CurationStatusRepository curationStatusRepository, AssociationRepository associationRepository, EthnicityRepository ethnicityRepository, PropertyFilePubMedLookupService propertyFilePubMedLookupService) {
        this.studyRepository = studyRepository;
        this.housekeepingRepository = housekeepingRepository;
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.associationRepository = associationRepository;
        this.ethnicityRepository = ethnicityRepository;
        this.propertyFilePubMedLookupService = propertyFilePubMedLookupService;
    }


    /* All studies and various filtered lists */

    // Return all studies
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String searchStudies(Model model) {

        // Find all studies
        model.addAttribute("studies", studyRepository.findAll());

        // Add a studySearchFilter to model in case user want to filter table
        model.addAttribute("studySearchFilter", new StudySearchFilter());

        return "studies";
    }

    // Studies by curator and/or status
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, params = "filters=true", method = RequestMethod.POST)
    public String searchForStudyByFilter(@ModelAttribute StudySearchFilter studySearchFilter, Model model) {

        // Get ids of objects searched for
        Long status = studySearchFilter.getStatusSearchFilterId();
        Long curator = studySearchFilter.getCuratorSearchFilterId();

        // If user entered a status
        if (status != null) {
            // If we have curator and status find by both
            if (curator != null) {
                model.addAttribute("studies", studyRepository.findByCurationStatusAndCuratorAllIgnoreCase(status, curator));
            } else {
                model.addAttribute("studies", studyRepository.findByCurationStatusIgnoreCase(status));
            }
        }
        // If user entered curator
        else if (curator != null) {
            model.addAttribute("studies", studyRepository.findByCuratorIgnoreCase(curator));
        }

        // If all else fails return all studies
        else {
            model.addAttribute("studies", studyRepository.findAll());
        }

        return "studies";
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
        Study existingStudy = studyRepository.findByPubmedId(pubmedId);
        if (existingStudy != null) {
            throw new PubmedImportException();
        }

        // Pass to importer
        Study importedStudy = propertyFilePubMedLookupService.dispatchSummaryQuery(pubmedId);

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
    public String updateStudy(@ModelAttribute Study study, Model model, @PathVariable Long studyId, RedirectAttributes redirectAttributes) {

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

        } else {
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
        studyHousekeeping.setNotes("Duplicate of study: " + studyToDuplicate.getAuthor() + ", PMID: " + studyToDuplicate.getPubmedId());
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
        String message = "Study is a duplicate of "+ studyToDuplicate.getAuthor() + ", PMID: " + studyToDuplicate.getPubmedId();
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
        } else {
            model.addAttribute("studyHousekeeping", study.getHousekeeping());
        }

        // Return the housekeeping object attached to study and return the study
        model.addAttribute("study", study);
        return "study_housekeeping";
    }


    // Update page with housekeeping/curator information linked to a study
    @RequestMapping(value = "/{studyId}/housekeeping", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String updateStudyHousekeeping(@ModelAttribute Housekeeping housekeeping, @PathVariable Long studyId, RedirectAttributes redirectAttributes) {

        // Establish whether user has set status to "Publish study" and "Send to NCBI"
        // as corresponding dates will be set in housekeeping table
        CurationStatus currentStatus = housekeeping.getCurationStatus();

        // TODO POSSIBLY CHANGE LOGIC SO THIS DATE IS SET BY NIGHTLY RELEASE PROCESS
        // OTHERWISE ANY TIME USER SAVES FROM WHEN STATUS IS SET TO "publish study"
        // THE DATE GETS UPDATED
        if (currentStatus != null && currentStatus.getStatus().equals("Publish study")) {
            java.util.Date publishDate = new java.util.Date();
            housekeeping.setPublishDate(publishDate);
        }

        if (currentStatus != null && currentStatus.getStatus().equals("Send to NCBI")) {
            java.util.Date sendToNCBIDate = new java.util.Date();
            housekeeping.setSendToNCBIDate(sendToNCBIDate);
        }

        // Save housekeeping returned from form
        housekeepingRepository.save(housekeeping);

        // Find study
        Study study = studyRepository.findOne(studyId);

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
        duplicateStudy.setAuthor(studyToDuplicate.getAuthor());
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
        Collection<EfoTrait> efoTraits= studyToDuplicate.getEfoTraits();
        Collection<EfoTrait> efoTraitsDuplicateStudy=new ArrayList<EfoTrait>();

        if(efoTraits != null && !efoTraits.isEmpty()){
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
        return diseaseTraitRepository.findAll();
    }

    // EFO traits
    @ModelAttribute("efoTraits")
    public List<EfoTrait> populateEFOTraits(Model model) {
        return efoTraitRepository.findAll();
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

}
