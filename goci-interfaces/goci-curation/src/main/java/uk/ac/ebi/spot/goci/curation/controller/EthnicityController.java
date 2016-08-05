package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.model.CountryOfOrigin;
import uk.ac.ebi.spot.goci.curation.model.CountryOfRecruitment;
import uk.ac.ebi.spot.goci.curation.model.EthnicGroup;
import uk.ac.ebi.spot.goci.curation.model.InitialSampleDescription;
import uk.ac.ebi.spot.goci.curation.model.ReplicationSampleDescription;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.StudyEthnicityService;
import uk.ac.ebi.spot.goci.curation.service.StudySampleDescriptionService;
import uk.ac.ebi.spot.goci.model.Country;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.CountryRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by emma on 05/01/15.
 *
 * @author emma
 *         <p>
 *         Ethnicity Controller, interpret user input and transform it into a ethniciy model that is represented to the
 *         user by the associated HTML page. Used to view, add and edit existing ethnicity/sample information.
 */
@Controller
public class EthnicityController {

    // Repositories allowing access to database objects associated with a study
    private EthnicityRepository ethnicityRepository;
    private CountryRepository countryRepository;
    private StudyRepository studyRepository;

    private StudySampleDescriptionService studySampleDescriptionService;
    private CurrentUserDetailsService currentUserDetailsService;
    private StudyEthnicityService ethnicityService;

    @Autowired
    public EthnicityController(EthnicityRepository ethnicityRepository,
                               CountryRepository countryRepository,
                               StudyRepository studyRepository,
                               StudySampleDescriptionService studySampleDescriptionService,
                               CurrentUserDetailsService currentUserDetailsService,
                               StudyEthnicityService ethnicityService) {
        this.ethnicityRepository = ethnicityRepository;
        this.countryRepository = countryRepository;
        this.studyRepository = studyRepository;
        this.studySampleDescriptionService = studySampleDescriptionService;
        this.currentUserDetailsService = currentUserDetailsService;
        this.ethnicityService = ethnicityService;
    }

    /* Ethnicity/Sample information associated with a study */

    // Generate view of ethnicity/sample information linked to a study
    @RequestMapping(value = "/studies/{studyId}/sampledescription",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewStudySampleDescription(Model model, @PathVariable Long studyId) {

        // Two types of ethnicity information which the view needs to form two different tables
        Collection<Ethnicity> initialStudyEthnicityDescriptions = new ArrayList<>();
        Collection<Ethnicity> replicationStudyEthnicityDescriptions = new ArrayList<>();

        String initialType = "initial";
        String replicationType = "replication";

        initialStudyEthnicityDescriptions.addAll(ethnicityRepository.findByStudyIdAndType(studyId, initialType));
        replicationStudyEthnicityDescriptions.addAll(ethnicityRepository.findByStudyIdAndType(studyId,
                                                                                              replicationType));

        // Add all ethnicity/sample information for the study to our model
        model.addAttribute("initialStudyEthnicityDescriptions", initialStudyEthnicityDescriptions);
        model.addAttribute("replicationStudyEthnicityDescriptions", replicationStudyEthnicityDescriptions);

        // Return an empty ethnicity object so curators can add new ethnicity/sample information to study
        model.addAttribute("ethnicity", new Ethnicity());

        // Return an SampleDescription object for each type
        Study study = studyRepository.findOne(studyId);

        if (study.getInitialSampleSize() != null && !study.getInitialSampleSize().isEmpty()) {
            InitialSampleDescription initialSampleDescription = new InitialSampleDescription();
            initialSampleDescription.setInitialSampleDescription(study.getInitialSampleSize());
            model.addAttribute("initialSampleDescription", initialSampleDescription);
        }
        else {
            model.addAttribute("initialSampleDescription", new InitialSampleDescription());
        }

        if (study.getReplicateSampleSize() != null && !study.getReplicateSampleSize().isEmpty()) {
            ReplicationSampleDescription replicationSampleDescription = new ReplicationSampleDescription();
            replicationSampleDescription.setReplicationSampleDescription(study.getReplicateSampleSize());
            model.addAttribute("replicationSampleDescription", replicationSampleDescription);
        }
        else {
            model.addAttribute("replicationSampleDescription", new ReplicationSampleDescription());
        }

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", study);
        return "study_sample_description";
    }


    // Add new sample information to a study
    @RequestMapping(value = "/studies/{studyId}/initialreplicationsampledescription",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addStudyInitialReplcationSampleDescription(@ModelAttribute InitialSampleDescription initialSampleDescription,
                                                             @ModelAttribute ReplicationSampleDescription replicationSampleDescription,
                                                             @PathVariable Long studyId,
                                                             RedirectAttributes redirectAttributes,
                                                             HttpServletRequest request) {

        studySampleDescriptionService.addStudyInitialReplcationSampleDescription(studyId,
                                                                                 initialSampleDescription,
                                                                                 replicationSampleDescription,
                                                                                 currentUserDetailsService.getUserFromRequest(
                                                                                         request));

        // Add save message
        String message = "Changes saved successfully";
        redirectAttributes.addFlashAttribute("changesSaved", message);
        return "redirect:/studies/" + studyId + "/sampledescription";
    }


    // Add new ethnicity/sample information to a study
    @RequestMapping(value = "/studies/{studyId}/sampledescription",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addStudySampleDescription(@ModelAttribute Ethnicity ethnicity,
                                            @PathVariable Long studyId,
                                            RedirectAttributes redirectAttributes, HttpServletRequest request) {

        ethnicityService.addEthnicity(studyId, ethnicity, currentUserDetailsService.getUserFromRequest(request));

        // Add save message
        String message = "Changes saved successfully";
        redirectAttributes.addFlashAttribute("changesSaved", message);
        return "redirect:/studies/" + studyId + "/sampledescription";
    }


    /* Existing ethnicity/sample information */

    // View ethnicity/sample information
    @RequestMapping(value = "/sampledescriptions/{ethnicityId}",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewSampleDescription(Model model, @PathVariable Long ethnicityId) {
        Ethnicity ethnicityToView = ethnicityRepository.findOne(ethnicityId);
        model.addAttribute("ethnicity", ethnicityToView);

    /*  Country of origin, country of recruitment and ethnic group are stored as a string in database
        In order to work with these in view we need to wrap them in a service object
        that returns the values to the view as an array */

        // Country of origin
        String ethnicityCountryOfOrigin = ethnicityToView.getCountryOfOrigin();
        CountryOfOrigin countryOfOrigin = new CountryOfOrigin(); // service object

        if (ethnicityCountryOfOrigin != null) {
            // multiple values separated by comma
            if (ethnicityCountryOfOrigin.contains(",")) {
                String[] countries = ethnicityCountryOfOrigin.split(",");
                countryOfOrigin.setOriginCountryValues(countries);
            }
            // single value
            else {
                String[] countries = new String[1];
                countries[0] = ethnicityCountryOfOrigin;
                countryOfOrigin.setOriginCountryValues(countries);
            }
        }

        model.addAttribute("countryOfOrigin", countryOfOrigin);

        //Country of recruitment
        String ethnicityCountryOfRecruitment = ethnicityToView.getCountryOfRecruitment();
        CountryOfRecruitment countryOfRecruitment = new CountryOfRecruitment(); // service object

        if (ethnicityCountryOfRecruitment != null) {
            // multiple values separated by comma
            if (ethnicityCountryOfRecruitment.contains(",")) {
                String[] countries = ethnicityCountryOfRecruitment.split(",");
                countryOfRecruitment.setRecruitmentCountryValues(countries);
            }
            // single value
            else {
                String[] countries = new String[1];
                countries[0] = ethnicityCountryOfRecruitment;
                countryOfRecruitment.setRecruitmentCountryValues(countries);
            }
        }

        model.addAttribute("countryOfRecruitment", countryOfRecruitment);

        // Ethnic group
        String ethnicityEthnicGroup = ethnicityToView.getEthnicGroup();
        EthnicGroup ethnicGroup = new EthnicGroup(); // service class

        if (ethnicityEthnicGroup != null) {
            // multiple values separated by comma
            if (ethnicityEthnicGroup.contains(",")) {
                String[] groups = ethnicityEthnicGroup.split(",");
                ethnicGroup.setEthnicGroupValues(groups);
            }
            // single value
            else {
                String[] groups = new String[1];
                groups[0] = ethnicityEthnicGroup;
                ethnicGroup.setEthnicGroupValues(groups);
            }
        }

        model.addAttribute("ethnicGroup", ethnicGroup);

        model.addAttribute("study", studyRepository.findOne(ethnicityToView.getStudy().getId()));
        return "edit_sample_description";
    }

    // Edit existing ethnicity/sample information
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/sampledescriptions/{ethnicityId}",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String updateSampleDescription(@ModelAttribute Ethnicity ethnicity,
                                          @ModelAttribute CountryOfOrigin countryOfOrigin,
                                          @ModelAttribute CountryOfRecruitment countryOfRecruitment,
                                          EthnicGroup ethnicGroup,
                                          RedirectAttributes redirectAttributes, HttpServletRequest request) {


        ethnicityService.updateEthnicity(ethnicity, countryOfOrigin, countryOfRecruitment, ethnicGroup, currentUserDetailsService.getUserFromRequest(request));

        // Add save message
        String message = "Changes saved successfully";
        redirectAttributes.addFlashAttribute("changesSaved", message);
        return "redirect:/studies/" + ethnicity.getStudy().getId() + "/sampledescription";
    }


    // Delete checked ethnicity/sample information linked to a study
    @RequestMapping(value = "/studies/{studyId}/sampledescription/delete_checked",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> deleteChecked(@RequestParam(value = "sampleDescriptionIds[]") String[] sampleDescriptionIds) {

        String message = "";
        Integer count = 0;

        // Get all ethnicities
        Collection<Ethnicity> studyEthnicity = new ArrayList<>();
        for (String sampleDescriptionId : sampleDescriptionIds) {
            studyEthnicity.add(ethnicityRepository.findOne(Long.valueOf(sampleDescriptionId)));
        }
        // Delete ethnicity
        for (Ethnicity ethnicity : studyEthnicity) {
            ethnicityRepository.delete(ethnicity);
            count++;
        }

        // Return success message to view
        message = "Successfully deleted " + count + " sample description(s)";

        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        return result;
    }

    // Delete all
    @RequestMapping(value = "/studies/{studyId}/sampledescription/delete_all",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String deleteAllStudySampleDescription(Model model, @PathVariable Long studyId) {

        // Get our study
        Study study = studyRepository.findOne(studyId);

        // Get all study ethnicity's
        Collection<Ethnicity> studyEthnicity = ethnicityRepository.findByStudyId(studyId);

        // Delete ethnicity
        for (Ethnicity ethnicity : studyEthnicity) {
            ethnicityRepository.delete(ethnicity);
        }
        return "redirect:/studies/" + studyId + "/sampledescription";
    }



    /* Model Attributes :
    *  Used for drop-downs in HTML forms
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
        List<Country> countries = countryRepository.findAll();
        Country countryNR = new Country();
        // Added NR as an option for curators
        countryNR.setName("NR");
        countries.add(countryNR);
        return countries;
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
