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
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.PublicationRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/{publicationId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudy(Model model, @PathVariable Long publicationId) {

        Publication publication = publicationRepository.findByPubmedId(publicationId.toString());
        model.addAttribute("publication", publication);
        return "publication";
    }

    // Curation statuses
    @ModelAttribute("curationstatuses")
    public List<CurationStatus> populateCurationStatuses() {
        return curationStatusRepository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "status").ignoreCase()));
    }

    // Curation statuses
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
    Map<String, String> updateSelectedCurator(@RequestBody String data,
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
}
