package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * Created by dwelter on 29/08/17.
 */


@Controller
@RequestMapping("/efotraits")
public class EfoTraitController {

    // Repositories allowing access to EFO traits in database
    private EfoTraitRepository efoTraitRepository;
    private StudyRepository studyRepository;

    @Autowired
    public EfoTraitController(EfoTraitRepository efoTraitRepository, StudyRepository studyRepository) {
        this.efoTraitRepository = efoTraitRepository;
        this.studyRepository = studyRepository;
    }

    //Return all Efo traits
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String allEfoTraits(Model model) {

        Sort sort = sortByTraitAsc();
        List<EfoTrait> allEfoTraits = efoTraitRepository.findAll(sort);
        model.addAttribute("efoTraits", allEfoTraits);
        model.addAttribute("totalEfoTraits", allEfoTraits.size());

        // Return an empty EfoTrait object so user can add a new one
        model.addAttribute("efoTrait", new EfoTrait());

        return "efo_traits";
    }

    // Add a new EFO trait
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addEfoTrait(@Valid @ModelAttribute EfoTrait efoTrait,
                                  BindingResult bindingResult,
                                  Model model, RedirectAttributes redirectAttributes) {

        // Check if it exists already
        EfoTrait existingEfoTrait = efoTraitRepository.findByTraitIgnoreCase(efoTrait.getTrait());
        List<EfoTrait> existingEfoUri = efoTraitRepository.findByUri(efoTrait.getUri());
        boolean uriDuplicate = false;
        String existingTrait = null;
        if (existingEfoTrait != null || !existingEfoUri.isEmpty()) {
            if(existingEfoTrait != null) {
                existingTrait = existingEfoTrait.getTrait();
            }
            else{
//                existingTrait = existingEfoUri.get(0).getTrait();
                if(existingEfoUri.size() > 0){
                    uriDuplicate = true;
                }
            }
        }

        // Catch a null or empty value being entered
        if (bindingResult.hasErrors()) {
            model.addAttribute("efoTraits", efoTraitRepository.findAll(sortByTraitAsc()));
            return "efo_traits";
        }

        else if (existingTrait != null || uriDuplicate) {
            String message =
                    "Trait already exists in database:";

            if(existingTrait != null){
                message = message.concat(" database trait = " + existingTrait + ", value entered = " +
                                         efoTrait.getTrait() + ";");
            }
            if(uriDuplicate){
                message = message.concat(" database URI = " + existingEfoUri.get(0).getUri() + ", value entered = " +
                                                 efoTrait.getUri() + ";");
            }
            if(!efoTrait.getTrait().equals(existingEfoUri.get(0).getTrait())){
                message = message.concat(" existing URI has label: " + existingEfoUri.get(0).getTrait());
            }
            redirectAttributes.addFlashAttribute("efoTraitExists", message);
            return "redirect:/efotraits";
        }

        // Save EFO trait
        else {
            String[] elements = efoTrait.getUri().split("/");
            int last = elements.length-1;
            String shortForm = elements[last];

            efoTrait.setShortForm(shortForm);


            efoTraitRepository.save(efoTrait);
            String message = "Trait " + efoTrait.getTrait() + " with URI " + efoTrait.getUri() + " added to database";
            redirectAttributes.addFlashAttribute("efoTraitSaved", message);
            return "redirect:/efotraits";
        }
    }

    // Edit EFO trait

    @RequestMapping(value = "/{efoTraitId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewEfoTrait(Model model, @PathVariable Long efoTraitId) {

        EfoTrait efoTraitToView = efoTraitRepository.findOne(efoTraitId);
        model.addAttribute("efoTrait", efoTraitToView);
        return "edit_efo_trait";
    }

    @RequestMapping(value = "/{efoTraitId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String editEfoTrait(@Valid @ModelAttribute EfoTrait efoTrait,
                                   BindingResult bindingResult, @PathVariable Long efoTraitId) {

        // Catch a null or empty value being entered
        if (bindingResult.hasErrors()) {
            return "edit_efo_trait";
        }

        // Save edited EFO trait
        else {
            efoTraitRepository.save(efoTrait);
            return "redirect:/efotraits";
        }
    }

    // Delete a EFO trait

    @RequestMapping(value = "/{efoTraitId}/delete",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewEfoTraitToDelete(Model model, @PathVariable Long efoTraitId) {

        EfoTrait efoTraitToView = efoTraitRepository.findOne(efoTraitId);
        Collection<Study> studiesLinkedToTrait = studyRepository.findByEfoTraitsId(efoTraitId);

        model.addAttribute("studies", studiesLinkedToTrait);
        model.addAttribute("totalStudies", studiesLinkedToTrait.size());
        model.addAttribute("efoTrait", efoTraitToView);
        return "delete_efo_trait";
    }


    @RequestMapping(value = "/{efoTraitId}/delete",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String deleteEfoTrait(@PathVariable Long efoTraitId, RedirectAttributes redirectAttributes) {

        // Need to find any studies linked to this EfoTrait
        Collection<Study> studiesWithEfoTrait = studyRepository.findByEfoTraitsId(efoTraitId);

        // For each study remove this Efo trait and save
        if (!studiesWithEfoTrait.isEmpty()) {
            String message = "Trait is used in " + studiesWithEfoTrait.size() + " study/studies, cannot delete!";
            redirectAttributes.addFlashAttribute("efoTraitUsed", message);
            return "redirect:/efotraits/" + efoTraitId + "/delete";
        }

        else {
            // Delete EFO trait
            efoTraitRepository.delete(efoTraitId);
            return "redirect:/efotraits";
        }
    }


    /* Model Attributes :
    *  Used for dropdowns in HTML forms
    */

    // EFO Traits
    @ModelAttribute("efoTraits")
    public List<EfoTrait> populateEfoTraits() {
        return efoTraitRepository.findAll(sortByTraitAsc());
    }

    // Returns a Sort object which sorts EFO traits in ascending order by trait
    private Sort sortByTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "trait").ignoreCase());
    }


}
