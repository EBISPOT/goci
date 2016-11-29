package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import uk.ac.ebi.spot.goci.curation.model.AncestralGroup;
import uk.ac.ebi.spot.goci.curation.model.InitialSampleDescription;
import uk.ac.ebi.spot.goci.curation.model.ReplicationSampleDescription;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.EventsViewService;
import uk.ac.ebi.spot.goci.curation.service.StudyAncestryService;
import uk.ac.ebi.spot.goci.curation.service.StudySampleDescriptionService;
import uk.ac.ebi.spot.goci.model.Country;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.CountryRepository;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by emma on 05/01/15.
 *
 * @author emma
 *         <p>
 *         Ancestry Controller, interpret user input and transform it into a ancestraliy model that is represented to the
 *         user by the associated HTML page. Used to view, add and edit existing ancestry/sample information.
 */
@Controller
public class AncestryController {

    // Repositories allowing access to database objects associated with a study
    private AncestryRepository ancestryRepository;
    private CountryRepository countryRepository;
    private StudyRepository studyRepository;

    private StudySampleDescriptionService studySampleDescriptionService;
    private CurrentUserDetailsService currentUserDetailsService;
    private StudyAncestryService ancestryService;
    private EventsViewService eventsViewService;

    @Autowired
    public AncestryController(AncestryRepository ancestryRepository,
                              CountryRepository countryRepository,
                              StudyRepository studyRepository,
                              StudySampleDescriptionService studySampleDescriptionService,
                              CurrentUserDetailsService currentUserDetailsService,
                              StudyAncestryService ancestryService,
                              @Qualifier("ancestryEventsViewService") EventsViewService eventsViewService) {
        this.ancestryRepository = ancestryRepository;
        this.countryRepository = countryRepository;
        this.studyRepository = studyRepository;
        this.studySampleDescriptionService = studySampleDescriptionService;
        this.currentUserDetailsService = currentUserDetailsService;
        this.ancestryService = ancestryService;
        this.eventsViewService = eventsViewService;
    }

    /* Ancestry/Sample information associated with a study */

    // Generate view of ancestry/sample information linked to a study
    @RequestMapping(value = "/studies/{studyId}/sampledescription",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewStudySampleDescription(Model model, @PathVariable Long studyId) {

        // Two types of ancestry information which the view needs to form two different tables
        Collection<Ancestry> initialStudyAncestryDescriptions = new ArrayList<>();
        Collection<Ancestry> replicationStudyAncestryDescriptions = new ArrayList<>();

        String initialType = "initial";
        String replicationType = "replication";

        initialStudyAncestryDescriptions.addAll(ancestryRepository.findByStudyIdAndType(studyId, initialType));
        replicationStudyAncestryDescriptions.addAll(ancestryRepository.findByStudyIdAndType(studyId,
                                                                                            replicationType));

        // Add all ancestry/sample information for the study to our model
        model.addAttribute("initialStudyAncestryDescriptions", initialStudyAncestryDescriptions);
        model.addAttribute("replicationStudyAncestryDescriptions", replicationStudyAncestryDescriptions);

        // Return an empty ancestry object so curators can add new ancestry/sample information to study
        model.addAttribute("ancestry", new Ancestry());

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


    // Add new ancestry/sample information to a study
    @RequestMapping(value = "/studies/{studyId}/sampledescription",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addStudySampleDescription(@ModelAttribute Ancestry ancestry,
                                            @PathVariable Long studyId,
                                            RedirectAttributes redirectAttributes, HttpServletRequest request) {

        ancestryService.addAncestry(studyId, ancestry, currentUserDetailsService.getUserFromRequest(request));

        // Add save message
        String message = "Changes saved successfully";
        redirectAttributes.addFlashAttribute("changesSaved", message);
        return "redirect:/studies/" + studyId + "/sampledescription";
    }


    /* Existing ancestry/sample information */

    // View ancestry/sample information
    @RequestMapping(value = "/sampledescriptions/{ancestryId}",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewSampleDescription(Model model, @PathVariable Long ancestryId) {
        Ancestry ancestryToView = ancestryRepository.findOne(ancestryId);
        model.addAttribute("ancestry", ancestryToView);

    /*  Country of origin, country of recruitment and ancestral group are stored as a string in database
        In order to work with these in view we need to wrap them in a service object
        that returns the values to the view as an array */

        // Country of origin
        String ancestryCountryOfOrigin = ancestryToView.getCountryOfOrigin();
        CountryOfOrigin countryOfOrigin = new CountryOfOrigin(); // service object

        if (ancestryCountryOfOrigin != null) {
            // multiple values separated by comma
            if (ancestryCountryOfOrigin.contains(",")) {
                String[] countries = ancestryCountryOfOrigin.split(",");
                countryOfOrigin.setOriginCountryValues(countries);
            }
            // single value
            else {
                String[] countries = new String[1];
                countries[0] = ancestryCountryOfOrigin;
                countryOfOrigin.setOriginCountryValues(countries);
            }
        }

        model.addAttribute("countryOfOrigin", countryOfOrigin);

        //Country of recruitment
        String ancestryCountryOfRecruitment = ancestryToView.getCountryOfRecruitment();
        CountryOfRecruitment countryOfRecruitment = new CountryOfRecruitment(); // service object

        if (ancestryCountryOfRecruitment != null) {
            // multiple values separated by comma
            if (ancestryCountryOfRecruitment.contains(",")) {
                String[] countries = ancestryCountryOfRecruitment.split(",");
                countryOfRecruitment.setRecruitmentCountryValues(countries);
            }
            // single value
            else {
                String[] countries = new String[1];
                countries[0] = ancestryCountryOfRecruitment;
                countryOfRecruitment.setRecruitmentCountryValues(countries);
            }
        }

        model.addAttribute("countryOfRecruitment", countryOfRecruitment);

        // Ancestral group
        String ancestryAncestralGroup = ancestryToView.getAncestralGroup();
        AncestralGroup ancestralGroup = new AncestralGroup(); // service class

        if (ancestryAncestralGroup != null) {
            // multiple values separated by comma
            if (ancestryAncestralGroup.contains(",")) {
                String[] groups = ancestryAncestralGroup.split(",");
                ancestralGroup.setAncestralGroupValues(groups);
            }
            // single value
            else {
                String[] groups = new String[1];
                groups[0] = ancestryAncestralGroup;
                ancestralGroup.setAncestralGroupValues(groups);
            }
        }

        model.addAttribute("ancestralGroup", ancestralGroup);

        model.addAttribute("study", studyRepository.findOne(ancestryToView.getStudy().getId()));
        return "edit_sample_description";
    }

    // Edit existing ancestry/sample information
    // @ModelAttribute is a reference to the object holding the data entered in the form
    @RequestMapping(value = "/sampledescriptions/{ancestryId}",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String updateSampleDescription(@ModelAttribute Ancestry ancestry,
                                          @ModelAttribute CountryOfOrigin countryOfOrigin,
                                          @ModelAttribute CountryOfRecruitment countryOfRecruitment,
                                          AncestralGroup ancestralGroup,
                                          RedirectAttributes redirectAttributes, HttpServletRequest request) {


        ancestryService.updateAncestry(ancestry,
                                         countryOfOrigin,
                                         countryOfRecruitment,
                                         ancestralGroup,
                                         currentUserDetailsService.getUserFromRequest(request));

        // Add save message
        String message = "Changes saved successfully";
        redirectAttributes.addFlashAttribute("changesSaved", message);
        return "redirect:/studies/" + ancestry.getStudy().getId() + "/sampledescription";
    }


    // Delete checked ancestry/sample information linked to a study
    @RequestMapping(value = "/studies/{studyId}/sampledescription/delete_checked",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> deleteChecked(@RequestParam(value = "sampleDescriptionIds[]") String[] sampleDescriptionIds,
                                      HttpServletRequest request) {

        String message = "";

        // Get all ancestries
        Collection<Ancestry> studyAncestry = new ArrayList<>();
        for (String sampleDescriptionId : sampleDescriptionIds) {
            studyAncestry.add(ancestryRepository.findOne(Long.valueOf(sampleDescriptionId)));
        }

        // Delete ancestry
        ancestryService.deleteChecked(studyAncestry, currentUserDetailsService.getUserFromRequest(request));

        // Return success message to view
        message = "Successfully deleted " + studyAncestry.size() + " sample description(s)";
        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        return result;
    }

    // Delete all
    @RequestMapping(value = "/studies/{studyId}/sampledescription/delete_all",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public Callable<String> deleteAllStudySampleDescription(@PathVariable Long studyId, HttpServletRequest request) {

        return () -> {
            ancestryService.deleteAll(studyId, currentUserDetailsService.getUserFromRequest(request));
            return "redirect:/studies/" + studyId + "/sampledescription";
        };
    }

    @RequestMapping(value = "/studies/{studyId}/ancestry_tracking",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String getStudyEvents(Model model, @PathVariable Long studyId) {
        model.addAttribute("events", eventsViewService.createViews(studyId));
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "ancestry_events";
    }


    // Ancestry Types
    @ModelAttribute("ancestryTypes")
    public List<String> populateAncestryTypes(Model model) {
        List<String> types = new ArrayList<>();
        types.add("initial");
        types.add("replication");
        return types;
    }

    // Ancestry Types
    @ModelAttribute("ancestralGroups")
    public List<String> populateAncestralGroups(Model model) {
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
        types.add("Middle Eastern/North African");
        types.add("Oceanian");
        types.add("Native American");
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