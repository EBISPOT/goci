package uk.ac.ebi.spot.goci.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.EfoColourMap;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.pussycat.layout.ColourMapper;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dwelter on 03/02/17.
 */

@Controller
public class EnsemblController {

    @Value("${ols.server}")
    private String olsServer;

    @Value("${ols.efo.terms}")
    private String olsEfoTerms;

    @Value("${ols.shortForm}")
    private String olsShortForm;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }


    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    @Autowired
    public EnsemblController(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository){
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
    }

    @RequestMapping(value = "/api/snpLocation/{range}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<PagedResources<SingleNucleotidePolymorphism>> search(@PathVariable String range,
                                                                           @PageableDefault(size = 20, page = 0) Pageable pageable) {

        String chrom = range.split(":")[0];
        String locs = range.split(":")[1];

        int start = Integer.parseInt(locs.split("-")[0]);
        int end = Integer.parseInt(locs.split("-")[1]);

        Page<SingleNucleotidePolymorphism>
                snps =
                singleNucleotidePolymorphismRepository.findByLocationsChromosomeNameAndLocationsChromosomePositionBetween(
                        chrom,
                        start,
                        end,
                        pageable);

        Resource<SingleNucleotidePolymorphism> snpResource = new Resource(snps);

        return new ResponseEntity(snpResource, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/parentMapping/{efoTerm}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<EfoColourMap> getColourMapping(@PathVariable String efoTerm) throws IOException {

        Map<String, String> ancestors = getAncestors(efoTerm);

        EfoColourMap colour = getTraitColour(ancestors.get("ancestors"), ancestors.get("iri"), ancestors.get("label"));

        return new ResponseEntity<EfoColourMap>(colour, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/parentMappings",
                    method = RequestMethod.POST,
                    consumes = "application/json",
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<List<EfoColourMap>> getColourMappings(@RequestBody List<String> efoTerms) throws IOException {
        List<EfoColourMap> colours = new ArrayList<>();

        for(String efoTerm : efoTerms) {
            Map<String, String> ancestors = getAncestors(efoTerm);
            colours.add(getTraitColour(ancestors.get("ancestors"), ancestors.get("iri"), ancestors.get("label")));
        }

        return new ResponseEntity<List<EfoColourMap>>(colours, HttpStatus.OK);
    }

    public Map<String, String> getAncestors(String efoTerm)  throws IOException{
        Map<String, String> result = new HashMap<>();
        String uri = olsServer.concat(olsEfoTerms);


        if(efoTerm.contains("http")){
            uri = uri.concat("?").concat(efoTerm);
        }
        else {
           uri = uri.concat(olsShortForm).concat(efoTerm);
        }


        RestTemplate restTemplate = new RestTemplate();
        String efoObject = restTemplate.getForObject(uri, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(efoObject);
        JsonNode responseNode = node.get("_embedded").get("terms").get(0);
        String ancestors_link = responseNode.get("_links").get("hierarchicalAncestors").get("href").asText().trim();
        ancestors_link = java.net.URLDecoder.decode(ancestors_link, "UTF-8");

        String label = responseNode.get("label").asText().trim();
        String iri = responseNode.get("iri").asText().trim();

        String ancestorsObject = restTemplate.getForObject(ancestors_link, String.class);

        result.put("iri", iri);
        result.put("label", label);
        result.put("ancestors", ancestorsObject);
        return result;
    }

    protected EfoColourMap getTraitColour(String ancestors, String uri, String trait) throws IOException {
        Map<String, String> allTypes = processAncestors(ancestors);

        Set<String> available = ColourMapper.COLOUR_MAP.keySet();

        List<String> multiple = new ArrayList<>();
        for (String type : allTypes.keySet()) {
            if (type != null) {
                if (available.contains(type)) {
                    multiple.add(type);
                }
            }
        }

        if(multiple.size() == 0){
            // if we got to here, no color available
            getLog().error("Could not identify a suitable colour category for trait " + trait);
            return new EfoColourMap(uri, trait, ColourMapper.OTHER, "experimental factor", ColourMapper.COLOUR_MAP.get(ColourMapper.OTHER));
        }
        else if (multiple.size() == 1){
            return new EfoColourMap(uri, trait, multiple.get(0), allTypes.get(multiple.get(0)), ColourMapper.COLOUR_MAP.get(multiple.get(0)));
        }
        else{
            getLog().info("More than one parent for trait " + trait);
            int size = 0;
            String current = null;
            for(String term : multiple){
                String ancs = getAncestors(term).get("ancestors");
                int count = processAncestors(ancs).keySet().size();

                if(count > size){
                    size = count;
                    current = term;
                }
            }
            return new EfoColourMap(uri, trait, current, allTypes.get(current), ColourMapper.COLOUR_MAP.get(current));
        }
    }

    private Map<String, String> processAncestors(String ancestors) throws IOException {
        Map<String, String> allAncestors = new HashMap();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(ancestors);

        JsonNode terms = node.get("_embedded").get("terms");

        for(JsonNode term : terms){
            String iri = term.get("iri").asText().trim();
            String name = term.get("label").asText().trim();

            if(allAncestors.get(iri) == null) {
                allAncestors.put(iri, name);
            }
        }

        return allAncestors;
    }
}
