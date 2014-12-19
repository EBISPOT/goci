package uk.ac.ebi.spot.goci.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.spot.goci.ui.repository.TraitRepository;

/**
* Created by dwelter on 18/12/14.
*/


@Controller
public class TraitMvcController {
    private TraitRepository traitRepository;

    @Autowired
    public TraitMvcController(TraitRepository traitRepository) {
        this.traitRepository = traitRepository;
    }

    @RequestMapping(value = "/traits", produces = MediaType.TEXT_HTML_VALUE)
    String traits(Model model) {
        model.addAttribute("traits", traitRepository.findAll());
        return "traits";
    }

    @RequestMapping(value = "/traits/{trait}", produces = MediaType.TEXT_HTML_VALUE)
    String traits(Model model,
                @PathVariable
                String trait) {
        model.addAttribute("traits", traitRepository.findByTrait(trait));
        return "traits";
    }
}
