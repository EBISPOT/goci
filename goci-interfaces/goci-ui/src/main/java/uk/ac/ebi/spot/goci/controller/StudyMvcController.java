package uk.ac.ebi.spot.goci.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/11/14
 */
@Controller
public class StudyMvcController {
    private StudyRepository studyRepository;

    @Autowired
    public StudyMvcController(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    @RequestMapping(value = "/studies", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    String studies(Model model) {
        model.addAttribute("studies", this.studyRepository.findAll());
        return "studies";
    }

    @RequestMapping(value = "/studies/{id}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    String study(Model model, @PathVariable String id) {
        model.addAttribute("study", this.studyRepository.findOne(Long.parseLong(id)));
        return "study_edit";
    }

//    @RequestMapping(value = "/studies/{id}", method = RequestMethod.POST)
//    public String studyUpdate(@ModelAttribute Study study, @PathVariable String id) {
//        Study saved = this.studyRepository.save(study);
//        return "redirect:/studies/" + saved.getId();
//    }

    @RequestMapping(value = "/studies/new", method = RequestMethod.GET)
    public String studyForm(Model model) {
//        model.addAttribute("study", new Study());
        return "study_edit";
    }

//    @RequestMapping(value = "/studies/new", method = RequestMethod.POST)
//    public String studySubmit(@ModelAttribute Study study, Model model) {
//        Study saved = this.studyRepository.save(study);
//        model.addAttribute("study", saved);
//        return "redirect:/studies/" + saved.getId();
//    }
}
