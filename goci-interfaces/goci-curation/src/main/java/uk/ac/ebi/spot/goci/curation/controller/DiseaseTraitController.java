package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.goci.curation.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.curation.repository.DiseaseTraitRepository;

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

    @Autowired
    public DiseaseTraitController(DiseaseTraitRepository diseaseTraitRepository) {
        this.diseaseTraitRepository = diseaseTraitRepository;
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
    public String addDiseaseTrait(@ModelAttribute DiseaseTrait diseaseTrait)  {

        // Save disease trait
        diseaseTraitRepository.save(diseaseTrait);
        return "redirect:/diseasetraits";
    }

    // Edit disease trait

    @RequestMapping(value = "/{diseaseTraitId}" , produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewDiseaseTrait(Model model, @PathVariable Long diseaseTraitId)  {

        DiseaseTrait diseaseTraitToView = diseaseTraitRepository.findOne(diseaseTraitId);
        model.addAttribute("diseaseTrait", diseaseTraitToView);
        return "edit_disease_trait";
    }

    @RequestMapping(value = "/{diseaseTraitId}" , produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String editDiseaseTrait(@ModelAttribute DiseaseTrait diseaseTrait)  {

        // Save edited disease trait
        diseaseTraitRepository.save(diseaseTrait);
        return "redirect:/diseasetraits";
    }


}
