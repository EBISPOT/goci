package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.LocalDate;
import org.junit.Test;
import uk.ac.ebi.spot.goci.model.deposition.DepositionPublication;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionPublicationListWrapper;

import java.io.IOException;

import static org.junit.Assert.*;

public class DepositionPublicationTest {
    @Test
    public void testDepositionPublicationWrite() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JodaModule());
            DepositionPublication publication = new DepositionPublication();
            publication.setPublicationDate(new LocalDate());
            String json = objectMapper.writeValueAsString(publication);
            assertNotNull(json);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testDepositionPublicationRead() {
        String json = "{\n" + "  \"publicationId\" : \"5d403399218e4cbdf409e2fd\",\n" + "  \"pmid\" : \"21044949\",\n" + "  \"title\" : \"ELF1 is associated with systemic lupus erythematosus in Asian populations.\",\n" + "  \"journal\" : \"Hum Mol Genet\",\n" + "  \"firstAuthor\" : \"Yang J\",\n" + "  \"publicationDate\" : \"2010-11-02\",\n" + "  \"correspondingAuthor\" : {\n" + "    \"authorName\" : \"Jing Yang\"\n" + "  },\n" + "  \"status\" : \"ELIGIBLE\",\n" + "  \"_links\" : {\n" + "    \"self\" : {\n" + "      \"href\" : \"http://193.62.54.159/backend/v1/publications/5d403399218e4cbdf409e2fd?pmid=false\"\n" + "    }\n" + "  }\n" + "}";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JodaModule());
            DepositionPublication publication = objectMapper.readValue(json, DepositionPublication.class);
            assertNotNull(publication);
            assertNotNull(publication.getPublicationDate().year());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testReadAllPublications(){
        String json = "{\n" + "  \"_embedded\" : {\n" + "    \"publications\" : [ {\n" +
                "      \"publicationId\" : \"5d526e6285a5b32028ed3d2d\",\n" + "      \"pmid\" : \"31235808\",\n" +
                "      \"title\" : \"Genome-wide analysis of dental caries and periodontitis combining clinical and self-reported data.\",\n" +
                "      \"journal\" : \"Nat Commun\",\n" + "      \"firstAuthor\" : \"Shungin D\",\n" +
                "      \"publicationDate\" : \"2019-06-24\",\n" + "      \"correspondingAuthor\" : {\n" +
                "        \"authorName\" : \"Giulianini F\"\n" + "      },\n" + "      \"status\" : \"EXPORTED\",\n" +
                "      \"_links\" : {\n" + "        \"self\" : {\n" +
                "          \"href\" : \"http://localhost:8081/v1/publications/5d526e6285a5b32028ed3d2d?pmid=false\"\n" +
                "        }\n" + "      }\n" + "    } ]\n" + "  },\n" + "  \"_links\" : {\n" + "    \"first\" : {\n" +
                "      \"href\" : \"http://localhost:8081/v1/publications?page=0&size=20\"\n" + "    },\n" +
                "    \"self\" : {\n" + "      \"href\" : \"http://localhost:8081/v1/publications\"\n" + "    },\n" +
                "    \"next\" : {\n" + "      \"href\" : \"http://localhost:8081/v1/publications?page=1&size=20\"\n" +
                "    },\n" + "    \"last\" : {\n" +
                "      \"href\" : \"http://localhost:8081/v1/publications?page=206&size=20\"\n" + "    }\n" + "  },\n" +
                "  \"page\" : {\n" + "    \"size\" : 20,\n" + "    \"totalElements\" : 4133,\n" +
                "    \"totalPages\" : 207,\n" + "    \"number\" : 0\n" + "  }\n" + "}";

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JodaModule());
            DepositionPublicationListWrapper
                    publication = objectMapper.readValue(json, DepositionPublicationListWrapper.class);
            assertNotNull(publication);
//            assertNotNull(publication.getPublications().get(0).getPublicationDate().year());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

//    @Test
//    public void testWriteAllPublicationsObject(){
//        DepositionPublication publication = new DepositionPublication();
//        DepositionPublicationListWrapper publicationList = new DepositionPublicationListWrapper();
//        publicationList.getPublications().add(publication);
//        Link link = new Link("#");
//        PagedResources<EmbeddedWrappers> resources =
//                new PagedResources<EmbeddedWrappers>(new EmbeddedWrappers(true).wrap(publicationList.getPublications()),
//                        new PagedResources.PageMetadata(1, 1, 1), link);
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.registerModule(new JodaModule());
//            String json = objectMapper.writeValueAsString(resources);
//            assertNotNull(json);
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }
//
//    }
}