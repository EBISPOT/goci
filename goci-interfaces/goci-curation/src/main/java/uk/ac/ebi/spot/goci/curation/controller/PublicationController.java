package uk.ac.ebi.spot.goci.curation.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.exception.NoStudyDirectoryException;
import uk.ac.ebi.spot.goci.curation.model.Assignee;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.model.StudyFileSummary;
import uk.ac.ebi.spot.goci.curation.service.*;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/publication")
public class PublicationController {
    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private CurationStatusRepository curationStatusRepository;
    @Autowired
    private CuratorRepository curatorRepository;
    @Autowired
    private StudyOperationsService studyOperationsService;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private CurrentUserDetailsService userDetailsService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DiseaseTraitRepository diseaseTraitRepository;
    @Autowired
    private EfoTraitRepository efoTraitRepository;
    @Autowired
    private AssociationRepository associationRepository;
    @Autowired
    private StudyAssociationBatchDeletionEventService studyAssociationBatchDeletionEventService;
    @Autowired
    private AssociationDeletionService associationDeletionService;
    @Autowired
    private StudyDeletionService studyDeletionService;
    @Autowired
    private StudyFileService studyFileService;

    @RequestMapping(value = "/{publicationId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudy(Model model, @PathVariable Long publicationId) {

        Publication publication = publicationRepository.findByPubmedId(publicationId.toString());
        Set<String> studiesWithFiles = new HashSet<>();
        for(Study study: publication.getStudies()){
            List<StudyFileSummary> studyFiles = studyFileService.getStudyFiles(study.getId());
            if(studyFiles != null && studyFiles.size() != 0){
                studiesWithFiles.add(study.getId().toString());
            }
        }
        model.addAttribute("publication", publication);
        model.addAttribute("studyFiles", studiesWithFiles);
        return "publication";
    }

    // Disease Traits
    @ModelAttribute("diseaseTraits")
    public List<DiseaseTrait> populateDiseaseTraits() {
        return diseaseTraitRepository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "trait").ignoreCase()));
    }

    // EFO traits
    @ModelAttribute("efoTraits")
    public List<EfoTrait> populateEFOTraits() {
        return efoTraitRepository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "trait").ignoreCase()));
    }

    // Curation statuses
    @ModelAttribute("curationstatuses")
    public List<CurationStatus> populateCurationStatuses() {
        return curationStatusRepository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "status").ignoreCase()));
    }

    // Curators
    @ModelAttribute("curators")
    public List<Curator> populateCurators() {
        return curatorRepository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "lastName").ignoreCase()));
    }

    @RequestMapping(value = "/status_update",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> updateSelectedStatuses(@RequestBody String data,
                                               HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        try {
            SecureUser user = userDetailsService.getUserFromRequest(request);
            JsonNode jsonObject = objectMapper.readTree(data);
            StatusAssignment status = new StatusAssignment();
            status.setStatusId(jsonObject.get("status").asLong());
            // Find the study and the curator user wishes to assign
           ArrayNode studyIds = (ArrayNode) jsonObject.get("ids");
           studyIds.forEach(node -> {
               Long studyId = node.asLong();
               Study study = studyRepository.getOne(studyId);
               studyOperationsService.assignStudyStatus(study, status, user);
               result.put(studyId.toString(), "Updated");
           });
            // Return success message to view
//            message = "Successfully updated " + studyIds.size() + " study statuses";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/assign_curator",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> updateSelectedCurators(@RequestBody String data,
                                               HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        try {
            SecureUser user = userDetailsService.getUserFromRequest(request);
            JsonNode jsonObject = objectMapper.readTree(data);
            Assignee assignee = new Assignee();
            assignee.setCuratorId(jsonObject.get("curator").asLong());
            // Find the study and the curator user wishes to assign
            ArrayNode studyIds = (ArrayNode) jsonObject.get("ids");
            studyIds.forEach(node -> {
                Long studyId = node.asLong();
                Study study = studyRepository.getOne(studyId);
                studyOperationsService.assignStudyCurator(study, assignee, user);
                result.put(studyId.toString(), "Updated");
            });
            // Return success message to view
//            message = "Successfully updated " + studyIds.size() + " study statuses";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/update_background_traits",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> updateBackgroundTraits(@RequestBody String data,
                                              HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        try {
            SecureUser user = userDetailsService.getUserFromRequest(request);
            JsonNode jsonObject = objectMapper.readTree(data);
            Long backgroundTraitId = jsonObject.get("backgroundTrait").asLong();
            DiseaseTrait diseaseTrait = diseaseTraitRepository.getOne(backgroundTraitId);
            // Find the study and the curator user wishes to assign
            ArrayNode studyIds = (ArrayNode) jsonObject.get("ids");
            studyIds.forEach(node -> {
                Long studyId = node.asLong();
                Study study = studyRepository.getOne(studyId);
                study.setBackgroundTrait(diseaseTrait);
                studyRepository.save(study);
                result.put(studyId.toString(), "Updated");
            });
            // Return success message to view
//            message = "Successfully updated " + studyIds.size() + " study statuses";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/update_disease_traits",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> updateDiseaseTraits(@RequestBody String data,
                                              HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        try {
            SecureUser user = userDetailsService.getUserFromRequest(request);
            JsonNode jsonObject = objectMapper.readTree(data);
            Long backgroundTraitId = jsonObject.get("diseaseTrait").asLong();
            DiseaseTrait diseaseTrait = diseaseTraitRepository.getOne(backgroundTraitId);
            // Find the study and the curator user wishes to assign
            ArrayNode studyIds = (ArrayNode) jsonObject.get("ids");
            studyIds.forEach(node -> {
                Long studyId = node.asLong();
                Study study = studyRepository.getOne(studyId);
                study.setDiseaseTrait(diseaseTrait);
                studyRepository.save(study);
                result.put(studyId.toString(), "Updated");
            });
            // Return success message to view
//            message = "Successfully updated " + studyIds.size() + " study statuses";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/update_efo_traits",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> updateEfoTraits(@RequestBody String data,
                                           HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        try {
            SecureUser user = userDetailsService.getUserFromRequest(request);
            JsonNode jsonObject = objectMapper.readTree(data);
            ArrayNode backgroundTraitId = (ArrayNode) jsonObject.get("efoTraits");
            List<EfoTrait> efoTraits = new ArrayList<>();
            backgroundTraitId.forEach(node -> {
                Long efoId = node.asLong();
                EfoTrait trait = efoTraitRepository.findOne(efoId);
                efoTraits.add(trait);
            });
            // Find the study and the curator user wishes to assign
            ArrayNode studyIds = (ArrayNode) jsonObject.get("ids");
            studyIds.forEach(node -> {
                Long studyId = node.asLong();
                Study study = studyRepository.getOne(studyId);
                study.setEfoTraits(efoTraits);
                studyRepository.save(study);
                result.put(studyId.toString(), "Updated");
            });
            // Return success message to view
//            message = "Successfully updated " + studyIds.size() + " study statuses";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/update_background_efo_traits",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> updateBackgroundEfoTraits(@RequestBody String data,
                                        HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        try {
            SecureUser user = userDetailsService.getUserFromRequest(request);
            JsonNode jsonObject = objectMapper.readTree(data);
            ArrayNode backgroundTraitId = (ArrayNode) jsonObject.get("backgroundEfoTraits");
            List<EfoTrait> efoTraits = new ArrayList<>();
            backgroundTraitId.forEach(node -> {
                Long efoId = node.asLong();
                EfoTrait trait = efoTraitRepository.findOne(efoId);
                efoTraits.add(trait);
            });
            // Find the study and the curator user wishes to assign
            ArrayNode studyIds = (ArrayNode) jsonObject.get("ids");
            studyIds.forEach(node -> {
                Long studyId = node.asLong();
                Study study = studyRepository.getOne(studyId);
                study.setMappedBackgroundTraits(efoTraits);
                studyRepository.save(study);
                result.put(studyId.toString(), "Updated");
            });
            // Return success message to view
//            message = "Successfully updated " + studyIds.size() + " study statuses";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/delete_studies",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> deleteStudies(@RequestBody String data,
                                                  HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        try {
            SecureUser user = userDetailsService.getUserFromRequest(request);
            JsonNode jsonObject = objectMapper.readTree(data);
            // Find the study and the curator user wishes to assign
            ArrayNode studyIds = (ArrayNode) jsonObject.get("ids");
            studyIds.forEach(node -> {
                Long studyId = node.asLong();
                Collection<Association> studyAssociations = associationRepository.findByStudyId(studyId);
                studyAssociationBatchDeletionEventService.createBatchUploadEvent(studyId, studyAssociations.size(), user);
                studyAssociations.forEach(association -> associationDeletionService.deleteAssociation(association, user));
                studyDeletionService.deleteStudy(studyRepository.getOne(studyId), user);
                result.put(studyId.toString(), "Deleted");
            });
            // Return success message to view
//            message = "Successfully updated " + studyIds.size() + " study statuses";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/add_sum_stats",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> addSummaryStats(@RequestBody String data,
                                      HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        try {
            SecureUser user = userDetailsService.getUserFromRequest(request);
            JsonNode jsonObject = objectMapper.readTree(data);
            // Find the study and the curator user wishes to assign
            ArrayNode studyIds = (ArrayNode) jsonObject.get("ids");
            studyIds.forEach(node -> {
                Long studyId = node.asLong();
                Study study = studyRepository.getOne(studyId);
                study.setFullPvalueSet(true);
                studyRepository.save(study);
                result.put(studyId.toString(), "Added");
            });
            // Return success message to view
//            message = "Successfully updated " + studyIds.size() + " study statuses";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
