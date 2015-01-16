package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.goci.curation.service.PubmedIdForImport;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.PropertyFilePubMedDispatcherService;
import uk.ac.ebi.spot.goci.service.exception.PubmedLookupException;

import java.util.List;

/**
 * Created by emma on 20/11/14.
 *
 * @author emma
 *         Study Controllers interpret user input and transform it into a study
 *         model that is represented to the user by the associated HTML page
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
    private PropertyFilePubMedDispatcherService propertyFilePubMedDispatcherService;

    @Autowired
    public StudyController(StudyRepository studyRepository, HousekeepingRepository housekeepingRepository, DiseaseTraitRepository diseaseTraitRepository, EfoTraitRepository efoTraitRepository, CuratorRepository curatorRepository, CurationStatusRepository curationStatusRepository, PropertyFilePubMedDispatcherService propertyFilePubMedDispatcherService) {
        this.studyRepository = studyRepository;
        this.housekeepingRepository = housekeepingRepository;
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.propertyFilePubMedDispatcherService = propertyFilePubMedDispatcherService;
    }


    /* All studies */

    // Return all studies
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String searchStudies(Model model) {
        model.addAttribute("studies", studyRepository.findAll());
        return "studies";
    }


   /* New Study*/

    // Add a new study
    // Directs user to an empty form to which they can create a new study
    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String newStudyForm(Model model) {
        model.addAttribute("study", new Study());

        // Return an empty String object so we can use this to store user entered pubmed id in future import
        model.addAttribute("pubmedIdForImport", new PubmedIdForImport());
        return "add_study";
    }


    // Save study found by Pubmed Id
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/new/import", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String importStudy(@ModelAttribute PubmedIdForImport pubmedIdForImport) {

        // Tidy our string
        String pubmedId = pubmedIdForImport.getPubmedId().trim();

        // Pass to importer
        Study importedStudy = propertyFilePubMedDispatcherService.dispatchSummaryQuery(pubmedId);
        studyRepository.save(importedStudy);

        return "redirect:/studies/" + importedStudy.getId();
    }


    // Save newly added study details
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addStudy(@ModelAttribute Study study) {
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
    public String updateStudy(@ModelAttribute Study study, Model model, @PathVariable Long studyId) {

        // Use id in URL to get study and then its associated housekeeping
        Study existingStudy = studyRepository.findOne(studyId);
        Housekeeping existingHousekeeping = existingStudy.getHousekeeping();

        // Set the housekeeping of the study returned to one already linked to it in database
        // Need to do this as we don't return housekeeping in form
        study.setHousekeeping(existingHousekeeping);

        // Saves the new information returned from form
        studyRepository.save(study);
        return "redirect:/studies/" + study.getId();
    }


    // Delete an existing study
    @RequestMapping(value = "/{studyId}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudyToDelete(Model model, @PathVariable Long studyId) {
        Study studyToDelete = studyRepository.findOne(studyId);
        model.addAttribute("studyToDelete", studyToDelete);
        return "delete_study";
    }

    @RequestMapping(value = "/{studyId}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String deleteStudy(@PathVariable Long studyId) {

        // Find our study based in the ID
        Study studyToDelete = studyRepository.findOne(studyId);

        // What do we need to delete ?


        //studyRepository.delete(studyToDelete);
        return "redirect:/studies/";
    }

    /* Study housekeeping/curator information */

    // Generate page with housekeeping/curator information linked to a study
    @RequestMapping(value = "/{studyId}/housekeeping", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudyHousekeeping(Model model, @PathVariable Long studyId) {

        // Find study
        Study study = studyRepository.findOne(studyId);

        // Determine if it has a housekeeping object
        if (study.getHousekeeping() == null) {
            study.setHousekeeping(new Housekeeping());
        }

        // Return the housekeeping object attached to study and return the study
        model.addAttribute("studyHousekeeping", study.getHousekeeping());
        model.addAttribute("study", study);
        return "study_housekeeping";
    }


    // Update page with housekeeping/curator information linked to a study
    @RequestMapping(value = "/{studyId}/housekeeping", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String updateStudyHousekeeping(@ModelAttribute Housekeeping housekeeping, @PathVariable Long studyId) {

        // Save housekeeping returned from form
        housekeepingRepository.save(housekeeping);

        // Find study
        Study study = studyRepository.findOne(studyId);

        // Set study housekeeping
        study.setHousekeeping(housekeeping);

        // Save our study
        studyRepository.save(study);
        return "redirect:/studies/" + study.getId() + "/housekeeping";
    }

    /* Model Attributes :
    *  Used for dropdowns in HTML forms
    *
    * */

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

    /* Exception handling */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PubmedLookupException.class)
    public String handlePubmedLookupException(PubmedLookupException pubmedLookupException) {
      //  return pubmedLookupException.getMessage();
        return "pubmed_lookup_warning";
    }
}
