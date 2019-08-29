package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionSubmissionList;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionSubmissionListWrapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DepositionSubmissionTest {

    @Test
    public void testReadSubmissions() {
        try {
            Resource resource = new ClassPathResource("submissions.json");
            assertTrue(resource.exists());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            DepositionSubmissionListWrapper submissionList =
                    objectMapper.readValue(resource.getFile(), DepositionSubmissionListWrapper.class);
            assertNotNull(submissionList);
            assertNotNull(submissionList.getWrapper().getSubmissions().get(0).getCreated().getTimestamp().toString()
                    .equals("2019-08-12T11:33:45.005Z"));
            assertNotNull(submissionList.getWrapper().getSubmissions().get(0).getStatus());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testReadSubmission() {
        try {
            Resource resource = new ClassPathResource("submission.json");
            assertTrue(resource.exists());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            DepositionSubmission submission =
                    objectMapper.readValue(resource.getFile(), DepositionSubmission.class);
            assertNotNull(submission);
            assertNotNull(submission.getCreated().getTimestamp().toString()
                    .equals("2019-08-12T11:41:43.861Z"));
            assertNotNull(submission.getStatus());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testReadSubmissionsListJson() {
//        String submissionWrapperJson = "{\n" + "  \"_embedded\" : {\n" + "    \"submissions\" : [ {\n" + "      " +
//                "\"submissionId\" : \"5d49486089a92e27c0c9ab82\",\n" + "      \"publication\" : {\n" + "
//                \"publicationId\" : \"47412849\",\n" + "        \"pmid\" : \"31299468\",\n" + "        \"title\" :
//                \"Genetic effects on efficacy to fluticasone propionate/salmeterol treatment in COPD.\",\n" + "
//                \"journal\" : \"Respir Med\",\n" + "        \"firstAuthor\" : \"Condreay LD\",\n" + "
//                \"status\" : \"EXPORTED\"\n" + "      },\n" + "      \"files\" : [ ],\n" + "      \"study_count\" :
//                0,\n" + "      \"sample_count\" : 0,\n" + "      \"association_count\" : 0,\n" + "
//                \"submission_status\" : \"STARTED\",\n" + "      \"created\" : {\n" + "        \"user\" : {\n" + "
//                \"name\" : \"Test User\",\n" + "          \"email\" : \"test@test.com\"\n" + "        },\n" + "
//                \"timestamp\" : \"2019-08-06T10:29:04.068+01:00\"\n" + "      },\n" + "      \"_links\" : {\n" + "
//                \"self\" : {\n" + "          \"href\" :
//                \"http://localhost:8081/v1/submissions/5d49486089a92e27c0c9ab82\"\n" + "        }\n" + "      }\n"
//                + "    } ]\n" + "  },\n" + "  \"_links\" : {\n" + "    \"self\" : {\n" + "      \"href\" :
//                \"http://localhost:8081/v1/submissions\"\n" + "    }\n" + "  },\n" + "  \"page\" : {\n" + "
//                \"size\" : 20,\n" + "    \"totalElements\" : 1,\n" + "    \"totalPages\" : 1,\n" + "    \"number\"
//                : 0\n" + "  }\n" + "}";
        String submissionWrapperJson = "[{\"submissionId\":\"5d52900385a5b345f8f70d5a\"," +
                "\"publication\":{\"pmid\":\"31349112\",\"title\":\"Association of PPP2R1A with Alzheimer's disease " +
                "and specific cognitive domains.\",\"journal\":\"Neurobiol Aging\",\"firstAuthor\":\"Miron J\"," +
                "\"publicationDate\":\"2019-07-02\",\"correspondingAuthor\":{\"authorName\":\"null null\"}," +
                "\"status\":\"UNDER_SUBMISSION\"},\"status\":\"STARTED\",\"studies\":[],\"associations\":[]," +
                "\"samples\":[],\"notes\":[],\"created\":{\"user\":{\"name\":\"Test User\",\"email\":\"test@test" +
                ".com\"},\"timestamp\":\"2019-08-13T11:22:16.772+01:00\"}}]";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JodaModule());
            List<DepositionSubmission> wrapper =
                    Arrays.asList(objectMapper.readValue(submissionWrapperJson, DepositionSubmission[].class));
            assertNotNull(wrapper);
            assertEquals(wrapper.size(), 1);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testCreateSubmissionWrapper() {
        DepositionSubmissionListWrapper wrapper = new DepositionSubmissionListWrapper();
        wrapper.setWrapper(new DepositionSubmissionList());
        DepositionSubmission submission = new DepositionSubmission();
        wrapper.getWrapper().getSubmissions().add(submission);
        try {
            String json = new ObjectMapper().writeValueAsString(wrapper);
            assertNotNull(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateSubmissionResponse() {
        String response = "{\n" + "  \"submissionId\" : \"5d403e35fc5fea00012696ff\",\n" + "  \"publication\" : {\n" +
                "    \"publicationId\" : \"5d403399218e4cbdf409e2fd\",\n" + "    \"pmid\" : \"21044949\",\n" +
                "    \"title\" : \"ELF1 is associated with systemic lupus erythematosus in Asian populations.\",\n" +
                "    \"journal\" : \"Hum Mol Genet\",\n" + "    \"firstAuthor\" : \"Yang J\",\n" +
                "    \"publicationDate\" : \"2010-11-02\",\n" + "    \"correspondingAuthor\" : {\n" +
                "      \"authorName\" : \"Jing Yang\"\n" + "    },\n" + "    \"status\" : \"ELIGIBLE\"\n" + "  },\n" +
                "  \"files\" : [ {\n" + "    \"fileUploadId\" : \"5d4052cafc5fea0001269713\",\n" +
                "    \"status\" : \"VALID\",\n" + "    \"fileName\" : \"file\",\n" + "    \"fileSize\" : 13600\n" +
                "  } ],\n" + "  \"study_count\" : 2,\n" + "  \"sample_count\" : 7,\n" +
                "  \"association_count\" : 4,\n" + "  \"submission_status\" : \"VALID\",\n" +
                "  \"metadata_status\" : \"VALID\",\n" + "  \"summary_statistics_status\" : \"NA\",\n" +
                "  \"created\" : {\n" + "    \"user\" : {\n" + "      \"name\" : \"Test User\",\n" +
                "      \"email\" : \"test@test.com\"\n" + "    },\n" +
                "    \"timestamp\" : \"2019-07-30T12:55:17.384Z\"\n" + "  },\n" + "  \"_links\" : {\n" +
                "    \"self\" : {\n" +
                "      \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d403e35fc5fea00012696ff\"\n" +
                "    },\n" + "    \"files\" : {\n" +
                "      \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/uploads\"\n" +
                "    },\n" + "    \"studies\" : {\n" +
                "      \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/studies\"\n" +
                "    },\n" + "    \"samples\" : {\n" +
                "      \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/samples\"\n" +
                "    },\n" + "    \"associations\" : {\n" +
                "      \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/associations\"\n" +
                "    }\n" + "  }\n" + "}";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JodaModule());
            DepositionSubmission submission = objectMapper.readValue(response, DepositionSubmission.class);
            assertNotNull(submission);
            assertNotNull(submission.getCreated().getTimestamp().year());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}