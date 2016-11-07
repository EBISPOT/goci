package uk.ac.ebi.spot.goci.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.PlatformRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

/**
 * Created by dwelter on 04/11/16.
 */

@Controller
@RequestMapping("/api")
public class RestController {

    // Repositories allowing access to database objects associated with a study
    private StudyRepository studyRepository;
    private DiseaseTraitRepository diseaseTraitRepository;
    private EfoTraitRepository efoTraitRepository;
    private PlatformRepository platformRepository;
    private AssociationRepository associationRepository;
    private EthnicityRepository ethnicityRepository;

    private static final int MAX_PAGE_ITEM_DISPLAY = 25;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public RestController(StudyRepository studyRepository,
                          DiseaseTraitRepository diseaseTraitRepository,
                          EfoTraitRepository efoTraitRepository,
                          PlatformRepository platformRepository,
                          AssociationRepository associationRepository,
                          EthnicityRepository ethnicityRepository){
        this.studyRepository = studyRepository;
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.platformRepository = platformRepository;
        this.associationRepository = associationRepository;
        this.ethnicityRepository = ethnicityRepository;

    }

//    @RequestMapping(path = "/studies/{study}", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
//    public String getStudy(@PathVariable("study") String studyId){
////        Page<Study> studyPage = studyRepository.findAll(constructPageSpecification(page - 1, sort));
//    }

    @RequestMapping(value = "/{studyId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudy(Model model, @PathVariable Long studyId) {
        Study studyToView = studyRepository.findOne(studyId);
        model.addAttribute("study", studyToView);
        return "study";
    }



}
