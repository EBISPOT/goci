package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

/**
 * Created by emma on 17/04/2015.
 * @author emma
 */
@Controller
public class PrintController {

    private StudyRepository studyRepository;
    private HousekeepingRepository housekeepingRepository;

    @Autowired
    public PrintController(StudyRepository studyRepository,
                           HousekeepingRepository housekeepingRepository) {
        this.studyRepository = studyRepository;
        this.housekeepingRepository = housekeepingRepository;
    }

    // View a study
    @RequestMapping(value = "/studies/{studyId}/printview", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewPrintableDetailsOfStudy(Model model, @PathVariable Long studyId) {

        // Get Study details
        Study studyToView = studyRepository.findOne(studyId);
        Housekeeping housekeeping = studyToView.getHousekeeping();

        model.addAttribute("study", studyToView);
        model.addAttribute("housekeeping", housekeeping);


        // TODO THIS WILL RETURN VIEW - NEED NEW PRINT VIEW
        return "printview";
    }



}
