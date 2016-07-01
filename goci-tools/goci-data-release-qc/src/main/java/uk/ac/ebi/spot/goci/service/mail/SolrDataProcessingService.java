package uk.ac.ebi.spot.goci.service.mail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ebi.spot.goci.model.PublishedStudy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dani on 01/07/16.
 */
public class SolrDataProcessingService {

    private String json;

    public SolrDataProcessingService(String json) {
        this.json = json;
    }

    public List<PublishedStudy> processJson() throws IOException {

        List<PublishedStudy> studies = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        JsonNode responseNode = node.get("response");
        JsonNode docs = responseNode.get("docs");


        for (JsonNode doc : docs) {

            PublishedStudy study =  processStudyJson(doc);

            studies.add(study);
        }

        return studies;
    }

    public PublishedStudy processStudyJson(JsonNode doc) throws IOException {
        
        
        String catalogReleaseDate = getCatalogReleaseDate(doc);
        

        String pubmedid = getPubmedId(doc);

        String author = getAuthor(doc);
        
        String publicationDate = getPublicationDate(doc);
        
        String journal = getJournal(doc);

        String title =  getTitle(doc);
        

        String trait = getTrait(doc);
        
        String associationCount = getAssocCount(doc);


//        Map<String, String> traits = getEfoTraits(doc);

//            traits.get("trait"));
//
//            traits.get("uri"));

        return new PublishedStudy(author, title, pubmedid, publicationDate, catalogReleaseDate, journal, associationCount, trait);


    }

    private String getAssocCount(JsonNode doc) {
        String count;

        if(doc.get("associationCount") != null){
            count = doc.get("associationCount").asText().trim();
        }
        else{
            count = "";
        }
        return count;
    }

    private String getTrait(JsonNode doc) {
        String traitName;
        if (doc.get("traitName_s") != null) {
            traitName = doc.get("traitName_s").asText().trim();
        }
        else {
            traitName = "";
        }
        return traitName;
    }

    private String getTitle(JsonNode doc) {
        String title;
        if (doc.get("title") != null) {
            title = doc.get("title").asText().trim();
        }
        else {
            title = "";
        }
        return title;
    }


    private String getJournal(JsonNode doc) {
        String journal;
        if (doc.get("publication") != null) {
            journal = doc.get("publication").asText().trim();
        }
        else {
            journal = "";
        }
        return journal;

    }

    private String getAuthor(JsonNode doc) {
        String author;
        if (doc.get("author_s") != null) {
            author = doc.get("author_s").asText().trim();
        }
        else {
            author = "";
        }
        return author;
    }

    private String getPubmedId(JsonNode doc) {
        String pmid;
        if (doc.get("pubmedId") != null) {
            pmid = doc.get("pubmedId").asText().trim();
        }
        else {
            pmid = "";
        }
        return pmid;
    }

    private String getPublicationDate(JsonNode doc) {
        String date;
        if (doc.get("publicationDate") != null) {
            date = doc.get("publicationDate").asText().trim().substring(0, 10);

        }
        else {
            date = "";
        }
        return date;
    }

    private String getCatalogReleaseDate(JsonNode doc) {
        String date;
        if (doc.get("catalogPublishDate") != null) {
            date = doc.get("catalogPublishDate").asText().trim().substring(0, 10);
        }
        else {
            date = "";
        }
        return date;
    }

    private Map<String, String> getEfoTraits(JsonNode doc) {
        Map<String, String> traits = new HashMap<>();

        String trait = "";
        String uri = "";

        if(doc.get("efoLink") != null){
            for(JsonNode efoLink : doc.get("efoLink")){
                String[] data = efoLink.asText().trim().split("\\|");

                if(trait == ""){
                    trait = data[0];
                    uri = data[2];
                }
                else{
                    trait = trait.concat(", ").concat(data[0]);
                    uri = uri.concat(", ").concat(data[2]);
                }
            }
        }

        traits.put("trait", trait);
        traits.put("uri", uri);

        return traits;
    }
}
