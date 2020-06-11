package uk.ac.ebi.spot.goci.curation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.deposition.Submission;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.utils.EuropePMCData;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * Created by emma on 09/01/15.
 *
 * @author emma DiseaseTrait Controller, interpret user input and transform it into a disease trait model that is
 *         represented to the user by the associated HTML page. Used to view, add and edit existing disease/trait
 *         information.
 */

@Controller
@RequestMapping("/diseasetraits")
public class DiseaseTraitController {

    // Repositories allowing access to disease traits in database
    private DiseaseTraitRepository diseaseTraitRepository;
    private StudyRepository studyRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public DiseaseTraitController(DiseaseTraitRepository diseaseTraitRepository, StudyRepository studyRepository, ObjectMapper objectMapper) {
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.studyRepository = studyRepository;
        this.objectMapper = objectMapper;
    }

    //Return all disease traits
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE,  method = RequestMethod.GET)
    public String allDiseaseTraits(Model model) {

        Sort sort = sortByTraitAsc();
        List<DiseaseTrait> allDiseaseTraits = diseaseTraitRepository.findAll(sort);
        model.addAttribute("diseaseTraits", allDiseaseTraits);
        model.addAttribute("totaldiseaseTraits", allDiseaseTraits.size());

        // Return an empty DiseaseTrait object so user can add a new one
        model.addAttribute("diseaseTrait", new DiseaseTrait());

        return "disease_traits";
    }

    // Add a new disease trait
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addDiseaseTrait(@Valid @ModelAttribute DiseaseTrait diseaseTrait,
                                  BindingResult bindingResult,
                                  Model model, RedirectAttributes redirectAttributes) {

        // Check if it exists already
        DiseaseTrait existingDiseaseTrait = diseaseTraitRepository.findByTraitIgnoreCase(diseaseTrait.getTrait());
        String existingTrait = null;
        if (existingDiseaseTrait != null) {
            existingTrait = existingDiseaseTrait.getTrait();
        }

        // Catch a null or empty value being entered
        if (bindingResult.hasErrors()) {
            model.addAttribute("diseaseTraits", diseaseTraitRepository.findAll(sortByTraitAsc()));
            return "disease_traits";
        }

        else if (existingTrait != null && !existingTrait.isEmpty()) {
            String message =
                    "Trait already exists in database: database value = " + existingTrait + ", value entered = " +
                            diseaseTrait.getTrait();
            redirectAttributes.addFlashAttribute("diseaseTraitExists", message);
            return "redirect:/diseasetraits";
        }

        // Save disease trait
        else {
            diseaseTraitRepository.save(diseaseTrait);
            String message = "Trait " + diseaseTrait.getTrait() + " added to database";
            redirectAttributes.addFlashAttribute("diseaseTraitSaved", message);
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
    public String editDiseaseTrait(@Valid @ModelAttribute DiseaseTrait diseaseTrait,
                                   BindingResult bindingResult, @PathVariable Long diseaseTraitId) {

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

    @RequestMapping(value = "/{diseaseTraitId}/delete",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewDiseaseTraitToDelete(Model model, @PathVariable Long diseaseTraitId) {

        DiseaseTrait diseaseTraitToView = diseaseTraitRepository.findOne(diseaseTraitId);
        Collection<Study> studiesLinkedToTrait = studyRepository.findByDiseaseTraitId(diseaseTraitId);

        model.addAttribute("studies", studiesLinkedToTrait);
        model.addAttribute("totalStudies", studiesLinkedToTrait.size());
        model.addAttribute("diseaseTrait", diseaseTraitToView);
        return "delete_disease_trait";
    }


    @RequestMapping(value = "/{diseaseTraitId}/delete",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String deleteDiseaseTrait(@PathVariable Long diseaseTraitId, RedirectAttributes redirectAttributes) {

        // Need to find any studies linked to this diseaseTrait
        Collection<Study> studiesWithDiseaseTrait = studyRepository.findByDiseaseTraitId(diseaseTraitId);

        // For each study remove this disease trait and save
        if (!studiesWithDiseaseTrait.isEmpty()) {
            String message = "Trait is used in " + studiesWithDiseaseTrait.size() + " study/studies, cannot delete!";
            redirectAttributes.addFlashAttribute("diseaseTraitUsed", message);
            return "redirect:/diseasetraits/" + diseaseTraitId + "/delete";
        }

        else {
            // Delete disease trait
            diseaseTraitRepository.delete(diseaseTraitId);
            return "redirect:/diseasetraits";
        }
    }

    /*            {"mData": "newdiseasetrait", "sDefaultContent": "", "sTitle": "New Disease Trait", "bSearchable": true},
            {"mData": "match", "sDefaultContent": "", "sTitle": "Best Match", "bSearchable": true},
            {"mData": "matchscore", "sDefaultContent": "", "sTitle": "Best Match Score", "bSearchable": true} ],
*/
    @RequestMapping(value="/check_new_traits", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<List<Map<String, String>>> checkDiseaseTraits(Model model, @RequestBody String[] data) {
        List<Map<String, String>> results = new ArrayList<>();
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        List<DiseaseTrait> traits = diseaseTraitRepository.findAll();
        try {
//            ArrayNode jsonObject = (ArrayNode) objectMapper.readTree(data);
//            jsonObject.forEach(newTrait -> {
            for(String newTrait: data){
                if(!newTrait.trim().equals("")) {
                    Map<String, String> row = new HashMap<>();
                    row.put("newdiseasetrait", newTrait);
                    Integer highScore = 0;
                    String bestMatch = "";
                    for (DiseaseTrait trait : traits) {
                        Integer ldScore = levenshteinDistance.apply(trait.getTrait().toLowerCase(),
                                newTrait.toLowerCase().trim());
                        ldScore = 100 - ldScore > 0 ? 100 - ldScore : 0;
                        if (ldScore > highScore) {
                            highScore = ldScore;
                            bestMatch = trait.getTrait();
                        }
                    }
                    row.put("match", bestMatch);
                    row.put("matchscore", highScore.toString());
                    results.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");

        return new ResponseEntity<>(results,responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value="/create_disease_traits", produces = MediaType.APPLICATION_JSON_VALUE, method =
            RequestMethod.POST)
    public @ResponseBody Map<String, String> createDiseaseTraits(Model model, @RequestBody String[] data) {
        Map<String, String> result = new HashMap<>();
        try{
            for(String newTrait: data){
                newTrait = newTrait.trim();
                if(!newTrait.equals("")) {
                    DiseaseTrait dbTrait = diseaseTraitRepository.findByTraitIgnoreCase(newTrait);
                    if (dbTrait == null) {
                        dbTrait = new DiseaseTrait();
                        dbTrait.setTrait(newTrait);
                        diseaseTraitRepository.save(dbTrait);
                        result.put(newTrait, "Created");
                    } else {
                        result.put(newTrait, "Exists");
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
        /* Model Attributes :
    *  Used for dropdowns in HTML forms
    */

    // Disease Traits
    @ModelAttribute("diseaseTraits")
    public List<DiseaseTrait> populateDiseaseTraits() {
        return diseaseTraitRepository.findAll(sortByTraitAsc());
    }

    // Returns a Sort object which sorts disease traits in ascending order by trait
    private Sort sortByTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "trait").ignoreCase());
    }

}
