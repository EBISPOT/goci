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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // Trim whitespace from form input
        efoTrait.setTrait(efoTrait.getTrait().trim());
        efoTrait.setUri(efoTrait.getUri().trim());

        // Catch a null or empty value being entered
        if (bindingResult.hasErrors()) {
            model.addAttribute("efoTraits", efoTraitRepository.findAll(sortByTraitAsc()));
            return "efo_traits";
        }

        // Check if URI is a properly formatted URL
        String URL_REGEX = "^((http|https)://|(www|purl)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher match = pattern.matcher(efoTrait.getUri());
        if (!match.find() ) {
            String invalidURIMessage = "The URI value entered \"" + efoTrait.getUri() + "\" is not valid. " +
                    "The URI value should be formatted similar to: http://www.ebi.ac.uk/efo/EFO_1234567.";
            redirectAttributes.addFlashAttribute("efoTraitExists", invalidURIMessage);
            return "redirect:/efotraits";
        }

        // Check format of CURIE
        String[] uriSplit = efoTrait.getUri().split("/");
        String curie = uriSplit[uriSplit.length -1];
        String ontologyPrefix = curie.split("_")[0].toLowerCase();
        ArrayList<String> PREFIX_OUTLIERS = new ArrayList<>(Arrays.asList(
                "orphanet", "hancestro", "ncit"));


        // The CURIE should be formatted as: PREFIX_1234567 for OBO Foundry ontologies
        String CURIE_REGEX = "^(([a-zA-Z])+_(\\d\\d\\d\\d\\d\\d\\d))$";
        Pattern curiePattern = Pattern.compile(CURIE_REGEX);
        Matcher curieMatch = curiePattern.matcher(curie);

        if (!PREFIX_OUTLIERS.contains(ontologyPrefix)) {
            if (!curieMatch.find()) {
                String invalidCurieMessage = "The URI value entered \"" + efoTrait.getUri() + "\" is not valid. " +
                        "The URI value for OBO Foundry ontologies should be formatted similar " +
                        "to: http://www.ebi.ac.uk/efo/EFO_1234567. \n Did you copy-paste the entire URI?";
                redirectAttributes.addFlashAttribute("efoTraitExists", invalidCurieMessage);
                return "redirect:/efotraits";
            }
        }


        // Check if Trait (trait or URI) exists already
        EfoTrait existingEfoTrait = efoTraitRepository.findByTraitIgnoreCase(efoTrait.getTrait());
        List<EfoTrait> existingEfoUri = efoTraitRepository.findByUri(efoTrait.getUri());

        if (existingEfoTrait != null || !existingEfoUri.isEmpty()) {
            String message =
                    "Trait already exists in database:";
            if(existingEfoTrait != null) {
                message = message.concat(" database trait = " + existingEfoTrait.getTrait()
                        + ", trait value entered = " + efoTrait.getTrait() + ";");
                if (!efoTrait.getUri().equals(existingEfoTrait.getUri())) {
                    message = message.concat(" existing trait has URI: " + existingEfoTrait.getUri());
                }
            }
            if(!existingEfoUri.isEmpty()) {
                message = message.concat(" database URI = " + existingEfoUri.get(0).getUri()
                        + ", URI value entered = " + efoTrait.getUri() + ";");

                if (!efoTrait.getTrait().equals(existingEfoUri.get(0).getTrait())) {
                    message = message.concat(" existing URI has label: " + existingEfoUri.get(0).getTrait());
                }
            }
            redirectAttributes.addFlashAttribute("efoTraitExists", message);
        }

        // Save EFO trait
        else {
            efoTrait.setShortForm(deriveShortForm(efoTrait.getUri()));

            efoTraitRepository.save(efoTrait);
            String message = "Trait " + efoTrait.getTrait() + " with URI " + efoTrait.getUri() + " added to database";
            redirectAttributes.addFlashAttribute("efoTraitSaved", message);
        }
        return "redirect:/efotraits";
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
            if(!efoTrait.getUri().contains(efoTrait.getShortForm())){
                efoTrait.setShortForm(deriveShortForm(efoTrait.getUri()));
            }
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


    private String deriveShortForm(String uri){
        String[] elements = uri.split("/");
        int last = elements.length-1;
        String shortForm = elements[last];

        return shortForm;
    }


}
