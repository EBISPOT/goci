package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import uk.ac.ebi.spot.goci.curation.model.*;
import uk.ac.ebi.spot.goci.curation.repository.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 20/11/14.
 *
 * @author emma
 *         <p>
 *         Study Controllers interpret user input and transform it into a study
 *         model that is represented to the user by the associated HTML page
 */

@Controller
@RequestMapping("/studies")
public class StudyController {

    // Repositories allowing access to database objects associated with a study
    private StudyRepository studyRepository;
    private AssociationRepository associationRepository;
    private EthnicityRepository ethnicityRepository;
    private HousekeepingRepository housekeepingRepository;
    private DiseaseTraitRepository diseaseTraitRepository;
    private EFOTraitRepository efoTraitRepository;
    private CuratorRepository curatorRepository;
    private CurationStatusRepository curationStatusRepository;

    @Autowired
    public StudyController(StudyRepository studyRepository, AssociationRepository associationRepository, EthnicityRepository ethnicityRepository, HousekeepingRepository housekeepingRepository, DiseaseTraitRepository diseaseTraitRepository, EFOTraitRepository efoTraitRepository, CuratorRepository curatorRepository, CurationStatusRepository curationStatusRepository) {
        this.studyRepository = studyRepository;
        this.associationRepository = associationRepository;
        this.ethnicityRepository = ethnicityRepository;
        this.housekeepingRepository = housekeepingRepository;
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
    }





    // Return all studies and filter
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String searchStudies(Model model) {
        model.addAttribute("studies", studyRepository.findAll());
        return "studies";
    }


    // Add a new study
    // Directs user to an empty form to which they can create a new study
    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String newStudyForm(Model model) {
        model.addAttribute("study", new Study());
        return "add_study";
    }

    // Save newly added study details
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addStudy(@ModelAttribute Study study, Model model) {
        Study newStudy = studyRepository.save(study);
        return "redirect:/studies/" + newStudy.getId();
    }

    // View a study
    @RequestMapping(value = "/{studyId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudy(Model model, @PathVariable long studyId) {
        Study studyToView = studyRepository.findOne(studyId);
        model.addAttribute("study", studyToView);
        return "study";
    }

    // Edit an existing study
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/{studyId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String updateStudy(@ModelAttribute Study study, Model model,  @PathVariable String studyId) {

       // Use id in URL to get study and then its associated housekeeping
        Study existingStudy = studyRepository.findOne(Long.valueOf(studyId));
        Housekeeping existingHousekeeping = existingStudy.getHousekeeping();

        // Set the housekeeping of the study returned to one already linked to it in database
        // Need to do this as we dont return housekeeping in form
        study.setHousekeeping(existingHousekeeping);

        // Saves the new information returned from form
        Study updatedStudy = studyRepository.save(study);
        return "redirect:/studies/" + updatedStudy.getId();
    }


    // Generate list of SNP associations linked to a study
    @RequestMapping(value = "/{studyId}/associations", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudySnps(Model model, @PathVariable String studyId) {

        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyID(studyId));
        model.addAttribute("studyAssociations", associations);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(Long.valueOf(studyId).longValue()));
        return "study_association";
    }

    // Generate page with sample description linked to a study
    @RequestMapping(value = "/{studyId}/sampledescription", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudySampleDescription(Model model, @PathVariable String studyId) {

        Collection<Ethnicity> studyEthnicityDescriptions = new ArrayList<>();
        studyEthnicityDescriptions.addAll(ethnicityRepository.findByStudyID(studyId));
        model.addAttribute("studyEthnicityDescriptions", studyEthnicityDescriptions);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(Long.valueOf(studyId).longValue()));
        return "study_sample_description";
    }


    // Generate page with housekeeping/curator information linked to a study
    @RequestMapping(value = "/{studyId}/housekeeping", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudyHousekeeping(Model model, @PathVariable String studyId) {

        // Find study
        Study study = studyRepository.findOne(Long.valueOf(studyId).longValue());

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
    public String updateStudyHousekeeping(@ModelAttribute Housekeeping housekeeping, @PathVariable String studyId) {

        // Find study
        Study study = studyRepository.findOne(Long.valueOf(studyId));

        // Set study housekeeping
        study.setHousekeeping(housekeeping);

        // Save our study
        Study updatedStudy = studyRepository.save(study);
        return "redirect:/studies/" + updatedStudy.getId() + "/housekeeping";
    }

    /* Model Attributes */
    // Disease Traits
    @ModelAttribute("diseaseTraits")
    public List<DiseaseTrait> populateDiseaseTraits(Model model) {
        return diseaseTraitRepository.findAll();
    }

    // EFO traits
    @ModelAttribute("efoTraits")
    public List<EFOTrait> populateEFOTraits(Model model) {
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
