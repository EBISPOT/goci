package uk.ac.ebi.spot.goci.curation.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.text.similarity.*;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
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
import uk.ac.ebi.spot.goci.curation.service.deposition.DepositionSubmissionService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.Submission;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.EuropepmcPubMedSearchService;
import uk.ac.ebi.spot.goci.utils.EuropePMCData;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/publication")
public class PublicationController {
    @Value("${deposition.ui.uri}")
    private String depositionUiURL;
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
    @Autowired
    private EuropepmcPubMedSearchService europepmcPubMedSearchService;
    @Autowired
    private DepositionSubmissionService submissionService;
    @Autowired
    private GenotypingTechnologyRepository genotypingTechnologyRepository;


    @RequestMapping(value = "/match", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> matchPublication(Model model, @RequestBody String pubmedId) {
        Map<String, Object> results = new HashMap<>();
        CosineDistance cosScore = new CosineDistance();
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        JaroWinklerSimilarity jwDistance = new JaroWinklerSimilarity();
        EuropePMCData europePMCResult = europepmcPubMedSearchService.createStudyByPubmed(pubmedId);
        Map<String, String> searchProps = new HashMap<>();
        List<Map<String, String>> data = new ArrayList<>();
        if(!europePMCResult.getError()) {
            try {
                searchProps.put("pubMedID", europePMCResult.getPublication().getPubmedId());
                searchProps.put("author", europePMCResult.getFirstAuthor().getFullname());
                searchProps.put("title", europePMCResult.getPublication().getTitle());
                searchProps.put("doi", europePMCResult.getDoi());
                results.put("search", searchProps);
                String searchTitle = europePMCResult.getPublication().getTitle();
                String searchAuthor = europePMCResult.getFirstAuthor().getFullname();
                CharSequence searchString = buildSearch(searchAuthor, searchTitle);
                Map<String, Submission> submissionMap = submissionService.getSubmissionsBasic();
                for (Map.Entry<String, Submission> e : submissionMap.entrySet()) {
                    Map<String, String> props = new HashMap<>();
                    Submission submission = e.getValue();
                    String matchTitle = submission.getTitle();
                    String matchAuthor = submission.getAuthor();
                    CharSequence matchString = buildSearch(matchAuthor, matchTitle);
                    props.put("submissionID", submission.getId());
                    props.put("pubMedID", submission.getPubMedID());
                    props.put("author", submission.getAuthor());
                    props.put("title", submission.getTitle());
                    props.put("doi", submission.getDoi());
                    if(matchString.equals("")){
                        props.put("cosScore", new Integer(0).toString());
                        props.put("levDistance", new Integer(0).toString());
                        props.put("jwScore", new Integer(0).toString());
                    }else{
                        Double score = cosScore.apply(searchString, matchString) * 100;
                        Integer ldScore = levenshteinDistance.apply(searchString, matchString);
                        Double jwScore = jwDistance.apply(searchString, matchString) * 100;
                        props.put("cosScore", normalizeScore(score.intValue()).toString());
                        props.put("levDistance", normalizeScore(ldScore).toString());
                        props.put("jwScore", new Integer(jwScore.intValue()).toString());
                    }
                    data.add(props);
                }
                data.sort((o1, o2) -> Integer.decode(o2.get("cosScore")).compareTo(Integer.decode(o1.get("cosScore"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            results.put("error", "ID " + pubmedId + " not found");
        }
        results.put("data", data);
        model.addAttribute("baseUrl", depositionUiURL);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");

        return new ResponseEntity<>(results,responseHeaders, HttpStatus.OK);
    }

    private CharSequence buildSearch(String author, String title) throws IOException {
        StringBuffer result = new StringBuffer();
        EnglishAnalyzer filter = new EnglishAnalyzer();
        if(author == null){
            author = "";
        }
        if(title == null){
            title = "";
        }
        String search = author.toLowerCase() + " " + title.toLowerCase();
        TokenStream stream = filter.tokenStream("", search.toString());
        stream.reset();
        CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
        while(stream.incrementToken()){
            result.append(term.toString()).append(" ");
        }
        stream.close();
        return result.toString().trim();
    }
    private Integer normalizeScore(int score){
        return 100 - score > 0 ? 100 - score : 0;
    }
    @RequestMapping(value = "/{publicationId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewPublication(Model model, @PathVariable Long publicationId) {

        Publication publication = publicationRepository.findByPubmedId(publicationId.toString());
        Set<String> studiesWithFiles = new HashSet<>();
        for(Study study: publication.getStudies()){
            List<StudyFileSummary> studyFiles = studyFileService.getStudyFiles(study.getId());
            if(studyFiles != null && studyFiles.size() != 0){
                studiesWithFiles.add(study.getId().toString());
            }
        }
        Map<String, String> pubmedMap = submissionService.getSubmissionPubMedIds();
            if(pubmedMap.containsKey(publication.getPubmedId())){
                publication.setActiveSubmission(true);
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
                //studyAssociationBatchDeletionEventService.createBatchUploadEvent(studyId, studyAssociations.size(),
                //        user);
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

    @RequestMapping(value = "/update_genotyping_technology",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> updateGenotypingTechnology(@RequestBody String data,
                                                  HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        try {
            SecureUser user = userDetailsService.getUserFromRequest(request);
            JsonNode jsonObject = objectMapper.readTree(data);
            ArrayNode technologyIds = (ArrayNode) jsonObject.get("technology");
            List<GenotypingTechnology> technologies = new ArrayList<>();
            technologyIds.forEach(node -> {
                String technology = node.asText();
                GenotypingTechnology tech = genotypingTechnologyRepository.findByGenotypingTechnology(technology);
                technologies.add(tech);
            });
            // Find the study and the curator user wishes to assign
            ArrayNode studyIds = (ArrayNode) jsonObject.get("ids");
            studyIds.forEach(node -> {
                Long studyId = node.asLong();
                Study study = studyRepository.getOne(studyId);
                study.setGenotypingTechnologies(technologies);
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


}
