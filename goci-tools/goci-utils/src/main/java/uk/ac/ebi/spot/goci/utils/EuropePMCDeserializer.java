package uk.ac.ebi.spot.goci.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.repository.PublicationRepository;
import uk.ac.ebi.spot.goci.service.junidecode.Junidecode;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by cinzia on 20/09/2017.
 */
public class EuropePMCDeserializer extends StdDeserializer<EuropePMCData> {

    public EuropePMCDeserializer() {
        this(null);
    }

    public EuropePMCDeserializer(Class<?> vc) {
        super(vc);
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public ArrayList<Author> getAuthorsInfo(JsonNode info) {
        ArrayList<Author> authors = new ArrayList<Author>();
        JsonNode root = info.get("resultList").get("result").get(0);
        Boolean noAuthor = true;

        getLog().debug("Authors: ");
        ArrayNode authorList = (ArrayNode) root.get("authorList").get("author");
        for (JsonNode author : authorList) {
            Author newAuthor = new Author();
            String fullname;
            if (author.has("collectiveName")) {
                fullname = author.get("collectiveName").asText();
                newAuthor.setFullname(fullname);
                newAuthor.setFullnameStandart(Junidecode.unidecode(fullname));
            } else {
                noAuthor = false;
                fullname = author.get("fullName").asText();
                newAuthor.setFullname(fullname);
                newAuthor.setFullnameStandart(Junidecode.unidecode(fullname));
                newAuthor.setLastName(author.has("lastName") ? author.get("lastName").asText() : null);
                newAuthor.setFirstName(author.has("firstName") ? author.get("firstName").asText() : null);
                newAuthor.setInitials(author.has("initials") ? author.get("initials").asText() : null);
                if (author.has("affiliation")){
                    if (author.get("affiliation").asText().length() > 700) {
                        newAuthor.setAffiliation(author.get("affiliation").asText().substring(0,699));
                    }
                    else { newAuthor.setAffiliation(author.get("affiliation").asText());}
                }
            }

            if (author.has("authorId")){
                newAuthor.setOrcid(author.get("authorId").get("value").asText());
            }

            authors.add(newAuthor);
        }
        // investigatorList
        if (noAuthor) {
            if (root.has("investigatorList")) {
                getLog().debug("Investigator: ");
                ArrayNode investigatorList = (ArrayNode) root.get("investigatorList").get("investigator");
                for (JsonNode author : investigatorList) {
                    Author newAuthor = new Author();
                    String fullname = author.get("fullName").asText();
                    newAuthor.setFullname(fullname);
                    newAuthor.setFullnameStandart(Junidecode.unidecode(fullname));
                    newAuthor.setLastName(author.has("lastName") ? author.get("lastName").asText() : null);
                    newAuthor.setFirstName(author.has("firstName") ? author.get("firstName").asText() : null);
                    newAuthor.setInitials(author.has("initials") ? author.get("initials").asText() : null);
                    if (author.has("affiliation")){
                        if (author.get("affiliation").asText().length() > 700) {
                            newAuthor.setAffiliation(author.get("affiliation").asText().substring(0,699));
                        }
                        else { newAuthor.setAffiliation(author.get("affiliation").asText());}
                    }
                    getLog().debug(newAuthor.getFullname());
                    if (author.has("authorId")) {
                        newAuthor.setOrcid(author.get("authorId").get("value").asText());
                    }
                    authors.add(newAuthor);
                }
            }
        }

        return authors;
    }

    public Publication getPublicatonInfo(JsonNode info) {
        Publication publication = new Publication();
        JsonNode root = info.get("resultList").get("result").get(0);

        System.out.println("Publication");
        publication.setPubmedId(root.get("pmid").asText());
        //publication.setPublicationDate(root.get("firstPublicationDate").asText());
        publication.setPublication(root.get("journalInfo").get("journal").get("medlineAbbreviation").asText());
        publication.setTitle(root.get("title").asText());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        /** Priority to eletronicDate. **/
        String datePublication="";
        if (root.has("electronicPublicationDate")) {
            datePublication = root.get("electronicPublicationDate").asText();
        }
        else {
            if (root.get("journalInfo").has("printPublicationDate")) {
                datePublication = root.get("journalInfo").get("printPublicationDate").asText();
            }
        }

        if (datePublication.contains("/")) {
            datePublication = datePublication.replace("/", "-");
        }

        java.util.Date studyDate = null;
        try {
            studyDate = format.parse(datePublication);
        }
        catch (ParseException e1) {
            e1.printStackTrace();
        }

        publication.setPublicationDate(new Date(studyDate.getTime()));

        return publication;
    }

    @Override
    public EuropePMCData deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        EuropePMCData record = new EuropePMCData();
        JsonNode node = jp.getCodec().readTree(jp);
        //JsonNode node = jp.getCodec().readTree(jp);
        //node.get("resultList");
        if (node.get("resultList").get("result").size() > 0) {
            record.setError(false);
            record.setPublication(getPublicatonInfo(node));
            ArrayList<Author> listAuthors = getAuthorsInfo(node);
            record.setAuthors(listAuthors);
            Author firstAuthor = listAuthors.get(0);
            record.setFirstAuthor(firstAuthor);
        }
        else { record.setError(true);}

        System.out.println("Deserialize done");

        return record;
    }

}