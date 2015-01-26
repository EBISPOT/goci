package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Created by emma on 09/01/15.
 *
 * @author emma
 *         DiseaseTrait Controller, interpret user input and transform it into a disease trait
 *         model that is represented to the user by the associated HTML page. Used to view, add and edit
 *         existing disease/trait information.
 */

@Controller
@RequestMapping("/diseasetraits")
public class DiseaseTraitController {

    // Repositories allowing access to disease traits in database
    private DiseaseTraitRepository diseaseTraitRepository;
    private StudyRepository studyRepository;

    @Autowired
    public DiseaseTraitController(DiseaseTraitRepository diseaseTraitRepository, StudyRepository studyRepository) {
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.studyRepository = studyRepository;
    }

    //Return all disease traits
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String allDiseaseTraits(Model model) {

        model.addAttribute("diseaseTraits", diseaseTraitRepository.findAll());

        // Return an empty DiseaseTrait object so user can add a new one
        model.addAttribute("diseaseTrait", new DiseaseTrait());

        return "disease_traits";
    }

    // Add a new disease trait
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addDiseaseTrait(@Valid @ModelAttribute DiseaseTrait diseaseTrait, BindingResult bindingResult, Model model) {

        // Catch a null or empty value being entered
        if (bindingResult.hasErrors()) {
            model.addAttribute("diseaseTraits", diseaseTraitRepository.findAll());
            return "disease_traits";
        }

        // Save disease trait
        else {
            diseaseTraitRepository.save(diseaseTrait);
            return "redirect:/diseasetraits";
        }
    }

    // Edit disease trait

    @RequestMapping(value = "/{diseaseTraitId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewDiseaseTrait(Model model, @PathVariable Long diseaseTraitId) {

        DiseaseTrait diseaseTraitToView = diseaseTraitRepository.findOne(diseaseTraitId);
        model.addAttribute("diseaseTrait", diseaseTraitToView);
        return "edit_disease_trait";
    }

    @RequestMapping(value = "/{diseaseTraitId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String editDiseaseTrait(@Valid @ModelAttribute DiseaseTrait diseaseTrait, BindingResult bindingResult, Model model) {

        // Catch a null or empty value being entered
        if (bindingResult.hasErrors()) {
            return "edit_disease_trait";
        }

        // Save edited disease trait
        else {
            diseaseTraitRepository.save(diseaseTrait);
            return "redirect:/diseasetraits";
        }
    }

    // Delete a disease trait

    @RequestMapping(value = "/{diseaseTraitId}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewDiseaseTraitToDelete(Model model, @PathVariable Long diseaseTraitId) {

        DiseaseTrait diseaseTraitToView = diseaseTraitRepository.findOne(diseaseTraitId);
        model.addAttribute("diseaseTrait", diseaseTraitToView);
        return "delete_disease_trait";
    }


    @RequestMapping(value = "/{diseaseTraitId}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String deleteDiseaseTrait(@PathVariable Long diseaseTraitId) {

        // Need to find any studies linked to this diseaseTrait
        Collection<Study> studiesWithDiseaseTrait = studyRepository.findByDiseaseTraitId(diseaseTraitId);

        // For each study remove this disease trait and save
        if (!studiesWithDiseaseTrait.isEmpty()) {
            for (Study study : studiesWithDiseaseTrait) {
                study.setDiseaseTrait(null);
                studyRepository.save(study);
            }
        }

        // Delete disease trait
        diseaseTraitRepository.delete(diseaseTraitId);

        return "redirect:/diseasetraits";
    }


}
