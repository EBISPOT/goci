package uk.ac.ebi.spot.goci.curation.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.PublicationRepository;

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
        return curatorRepository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "userName").ignoreCase()));
    }

    @RequestMapping(value = "/status_update",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> updateSelectedStatuses(@RequestParam(value = "data") String data,
                                      HttpServletRequest request) {
        String message = null;
        try {
            JsonNode jsonObject = objectMapper.readTree(data);
            // Find the study and the curator user wishes to assign
            JsonNode studyId = jsonObject.get("studyIds");
            // Return success message to view
//            message = "Successfully updated " + studyIds.size() + " study statuses";
            Map<String, String> result = new HashMap<>();
            result.put("message", message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
