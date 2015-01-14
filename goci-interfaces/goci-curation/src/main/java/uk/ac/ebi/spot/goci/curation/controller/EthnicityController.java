package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.model.Country;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.CountryRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 05/01/15.
 *
 * @author emma
 *         Ethnicity Controller, interpret user input and transform it into a ethniciy
 *         model that is represented to the user by the associated HTML page. Used to view, add and edit
 *         existing ethnicity/sample information.
 */
@Controller
public class EthnicityController {

    // Repositories allowing access to database objects associated with a study
    private EthnicityRepository ethnicityRepository;
    private CountryRepository countryRepository;
    private StudyRepository studyRepository;

    @Autowired
    public EthnicityController(EthnicityRepository ethnicityRepository, CountryRepository countryRepository, StudyRepository studyRepository) {
        this.ethnicityRepository = ethnicityRepository;
        this.countryRepository = countryRepository;
        this.studyRepository = studyRepository;
    }


    /* Ethnicity/Sample information associated with a study */

    // Generate view of ethnicity/sample information linked to a study
    @RequestMapping(value = "/studies/{studyId}/sampledescription", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudySampleDescription(Model model, @PathVariable String studyId) {


        // Two types of ethnicity information which in the view needs to form two different tables
        Collection<Ethnicity> initialStudyEthnicityDescriptions = new ArrayList<>();
        Collection<Ethnicity> replicationStudyEthnicityDescriptions = new ArrayList<>();

        String initialType = "initial";
        String replicationType = "replication";

        initialStudyEthnicityDescriptions.addAll(ethnicityRepository.findByStudyIdAndType(Long.parseLong(studyId), initialType));
        replicationStudyEthnicityDescriptions.addAll(ethnicityRepository.findByStudyIdAndType(Long.parseLong(studyId), replicationType));

        // Add all ethnicity/sample information for the study to our model
        model.addAttribute("initialStudyEthnicityDescriptions", initialStudyEthnicityDescriptions);
        model.addAttribute("replicationStudyEthnicityDescriptions", replicationStudyEthnicityDescriptions);

        // Return an empty ethnicity object so curators can add new ethnicity/sample information to study
        model.addAttribute("ethnicity", new Ethnicity());

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(Long.valueOf(studyId).longValue()));
        return "study_sample_description";
    }


    // Add new ethnicity/sample information to a study
    @RequestMapping(value = "/studies/{studyId}/sampledescription", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addStudySampleDescription(@ModelAttribute Ethnicity ethnicity, @PathVariable String studyId) {
        Study study = studyRepository.findOne(Long.parseLong(studyId));

        // Set the study for our ethnicity
        ethnicity.setStudy(study);

        // Save our ethnicity/sample information
        Ethnicity updatedEthnicity = ethnicityRepository.save(ethnicity);
        return "redirect:/studies/" + studyId + "/sampledescription";
    }


    /* Existing ethnicity/sample information */

    // View ethnicity/sample information
    @RequestMapping(value = "/sampledescriptions/{ethnicityId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewSampleDescription(Model model, @PathVariable Long ethnicityId) {
        Ethnicity ethnicityToView = ethnicityRepository.findOne(ethnicityId);
        model.addAttribute("ethnicity", ethnicityToView);
        return "edit_sample_description";
    }

    // Edit existing ethnicity/sample information
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/sampledescriptions/{ethnicityId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String updateSampleDescription(@ModelAttribute Ethnicity ethnicity) {

        // Saves the new information returned from form
        Ethnicity updatedEthnicity = ethnicityRepository.save(ethnicity);
        return "redirect:/studies/" + updatedEthnicity.getStudy().getId() + "/sampledescription";
    }


    /* Model Attributes :
    *  Used for dropdowns in HTML forms
    *
    */

    // Ethnicity Types
    @ModelAttribute("ethnicityTypes")
    public List<String> populateEthnicityTypes(Model model) {
        List<String> types = new ArrayList<>();
        types.add("initial");
        types.add("replication");
        return types;
    }


    // Ethnicity Types
    @ModelAttribute("ethnicGroups")
    public List<String> populateEthnicGroups(Model model) {
        List<String> types = new ArrayList<>();
        types.add("European");
        types.add("Sub-Saharan African");
        types.add("African unspecified");
        types.add("African unspecified");
        types.add("South Asian");
        types.add("South East Asian");
        types.add("Central Asian");
        types.add("East Asian");
        types.add("Asian unspecified");
        types.add("African American/Afro-Caribbean");
        types.add("Middle East/North African");
        types.add("Oceania");
        types.add("American Indian");
        types.add("Hispanic/Latin American");
        types.add("Other");
        types.add("NR");
        return types;
    }

    // Countries
    @ModelAttribute("countries")
    public List<Country> populateCountries(Model model) {
        return countryRepository.findAll();
    }

    // Sample size match
    @ModelAttribute("sampleSizesMatchOptions")
    public List<String> populateSampleSizesMatchOptions(Model model) {

        List<String> sampleSizesMatchOptions = new ArrayList<String>();
        sampleSizesMatchOptions.add("Y");
        sampleSizesMatchOptions.add("N");
        return sampleSizesMatchOptions;
    }

}
