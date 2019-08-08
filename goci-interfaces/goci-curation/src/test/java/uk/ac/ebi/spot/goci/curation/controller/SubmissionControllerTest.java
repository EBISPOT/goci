package uk.ac.ebi.spot.goci.curation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.LocalDate;
import org.junit.Test;
import uk.ac.ebi.spot.goci.model.DepositionPublication;
import uk.ac.ebi.spot.goci.model.DepositionSubmission;
import uk.ac.ebi.spot.goci.model.DepositionSubmissionList;
import uk.ac.ebi.spot.goci.model.DepositionSubmissionWrapper;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class SubmissionControllerTest {

    @Test
    public void testReadSubmissionsListJson() {
        //String submissionWrapperJson = "{\n" + "  \"_embedded\" : {\n" + "    \"submissions\" : [ {\n" + "      " +
//                "\"submissionId\" : \"5d403e35fc5fea00012696ff\",\n" + "      \"publication\" : {\n" + "        " +
//                "\"publicationId\" : \"5d403399218e4cbdf409e2fd\",\n" + "        \"pmid\" : \"21044949\",\n" + "
//                \"title\" : \"ELF1 is associated with systemic lupus erythematosus in Asian populations.\",\n" + "        \"journal\" : \"Hum Mol Genet\",\n" + "        \"firstAuthor\" : \"Yang J\",\n" + "        \"publicationDate\" : \"2010-11-02\",\n" + "        \"correspondingAuthor\" : {\n" + "          \"authorName\" : \"Jing Yang\"\n" + "        },\n" + "        \"status\" : \"ELIGIBLE\"\n" + "      },\n" + "      \"files\" : [ {\n" + "        \"fileUploadId\" : \"5d4052cafc5fea0001269713\",\n" + "        \"status\" : \"VALID\",\n" + "        \"fileName\" : \"file\",\n" + "        \"fileSize\" : 13600\n" + "      } ],\n" + "      \"study_count\" : 2,\n" + "      \"sample_count\" : 7,\n" + "      \"association_count\" : 4,\n" + "      \"submission_status\" : \"VALID\",\n" + "      \"metadata_status\" : \"VALID\",\n" + "      \"summary_statistics_status\" : \"NA\",\n" + "      \"created\" : {\n" + "        \"user\" : {\n" + "          \"name\" : \"Test User\",\n" + "          \"email\" : \"test@test.com\"\n" + "        },\n" + "        \"timestamp\" : \"2019-07-30T12:55:17.384Z\"\n" + "      },\n" + "      \"_links\" : {\n" + "        \"self\" : {\n" + "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d403e35fc5fea00012696ff\"\n" + "        },\n" + "        \"files\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/uploads\"\n" + "        },\n" + "        \"studies\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/studies\"\n" + "        },\n" + "        \"samples\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/samples\"\n" + "        },\n" + "        \"associations\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/associations\"\n" + "        }\n" + "      }\n" + "    }, {\n" + "      \"submissionId\" : \"5d4051fcfc5fea0001269700\",\n" + "      \"publication\" : {\n" + "        \"publicationId\" : \"5d403399218e4cbdf409e2fd\",\n" + "        \"pmid\" : \"21044949\",\n" + "        \"title\" : \"ELF1 is associated with systemic lupus erythematosus in Asian populations.\",\n" + "        \"journal\" : \"Hum Mol Genet\",\n" + "        \"firstAuthor\" : \"Yang J\",\n" + "        \"publicationDate\" : \"2010-11-02\",\n" + "        \"correspondingAuthor\" : {\n" + "          \"authorName\" : \"Jing Yang\"\n" + "        },\n" + "        \"status\" : \"ELIGIBLE\"\n" + "      },\n" + "      \"files\" : [ {\n" + "        \"fileUploadId\" : \"5d405215fc5fea0001269703\",\n" + "        \"status\" : \"VALID\",\n" + "        \"fileName\" : \"file\",\n" + "        \"fileSize\" : 13616\n" + "      } ],\n" + "      \"study_count\" : 2,\n" + "      \"sample_count\" : 7,\n" + "      \"association_count\" : 4,\n" + "      \"submission_status\" : \"VALID\",\n" + "      \"metadata_status\" : \"VALID\",\n" + "      \"summary_statistics_status\" : \"NA\",\n" + "      \"created\" : {\n" + "        \"user\" : {\n" + "          \"name\" : \"Test User\",\n" + "          \"email\" : \"test@test.com\"\n" + "        },\n" + "        \"timestamp\" : \"2019-07-30T14:19:40.595Z\"\n" + "      },\n" + "      \"_links\" : {\n" + "        \"self\" : {\n" + "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d4051fcfc5fea0001269700\"\n" + "        },\n" + "        \"files\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d4051fcfc5fea0001269700/uploads\"\n" + "        },\n" + "        \"studies\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d4051fcfc5fea0001269700/studies\"\n" + "        },\n" + "        \"samples\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d4051fcfc5fea0001269700/samples\"\n" + "        },\n" + "        \"associations\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d4051fcfc5fea0001269700/associations\"\n" + "        }\n" + "      }\n" + "    }, {\n" + "      \"submissionId\" : \"5d4052e7fc5fea0001269721\",\n" + "      \"publication\" : {\n" + "        \"publicationId\" : \"5d403399218e4cbdf409e2fe\",\n" + "        \"pmid\" : \"17690259\",\n" + "        \"title\" : \"Common sequence variants in the LOXL1 gene confer susceptibility to exfoliation glaucoma.\",\n" + "        \"journal\" : \"Science\",\n" + "        \"firstAuthor\" : \"Thorleifsson G\",\n" + "        \"publicationDate\" : \"2007-08-09\",\n" + "        \"correspondingAuthor\" : {\n" + "          \"authorName\" : \"Gudmar Thorleifsson\"\n" + "        },\n" + "        \"status\" : \"ELIGIBLE\"\n" + "      },\n" + "      \"files\" : [ {\n" + "        \"fileUploadId\" : \"5d4052fbfc5fea0001269724\",\n" + "        \"status\" : \"VALID\",\n" + "        \"fileName\" : \"file\",\n" + "        \"fileSize\" : 13600\n" + "      }, {\n" + "        \"fileUploadId\" : \"5d405866fc5fea0001269734\",\n" + "        \"status\" : \"INVALID\",\n" + "        \"fileName\" : \"file\",\n" + "        \"fileSize\" : 13607,\n" + "        \"errors\" : [ \"Samples: Column 'Number of individuals' in row '5' lacks mandatory value\" ]\n" + "      }, {\n" + "        \"fileUploadId\" : \"5d4058b4fc5fea0001269737\",\n" + "        \"status\" : \"INVALID\",\n" + "        \"fileName\" : \"file\",\n" + "        \"fileSize\" : 13604,\n" + "        \"errors\" : [ \"Associations: Column 'p-value' in row '2' contains incorrect value. Accepted values should be in the range: '0.0-1.0E-5'\" ]\n" + "      }, {\n" + "        \"fileUploadId\" : \"5d4059a0fc5fea000126973a\",\n" + "        \"status\" : \"VALID\",\n" + "        \"fileName\" : \"file\",\n" + "        \"fileSize\" : 13600\n" + "      } ],\n" + "      \"study_count\" : 4,\n" + "      \"sample_count\" : 14,\n" + "      \"association_count\" : 8,\n" + "      \"submission_status\" : \"VALID\",\n" + "      \"metadata_status\" : \"VALID\",\n" + "      \"summary_statistics_status\" : \"NA\",\n" + "      \"created\" : {\n" + "        \"user\" : {\n" + "          \"name\" : \"Test User\",\n" + "          \"email\" : \"test@test.com\"\n" + "        },\n" + "        \"timestamp\" : \"2019-07-30T14:23:35.253Z\"\n" + "      },\n" + "      \"_links\" : {\n" + "        \"self\" : {\n" + "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d4052e7fc5fea0001269721\"\n" + "        },\n" + "        \"files\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d4052e7fc5fea0001269721/uploads\"\n" + "        },\n" + "        \"studies\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d4052e7fc5fea0001269721/studies\"\n" + "        },\n" + "        \"samples\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d4052e7fc5fea0001269721/samples\"\n" + "        },\n" + "        \"associations\" : {\n" + "          \"href\" : \"http://193.62.54.159/v1/submissions/5d4052e7fc5fea0001269721/associations\"\n" + "        }\n" + "      }\n" + "    } ]\n" + "  },\n" + "  \"_links\" : {\n" + "    \"self\" : {\n" + "      \"href\" : \"http://193.62.54.159/backend/v1/submissions\"\n" + "    }\n" + "  },\n" + "  \"page\" : {\n" + "    \"size\" : 20,\n" + "    \"totalElements\" : 3,\n" + "    \"totalPages\" : 1,\n" + "    \"number\" : 0\n" + "  }\n" + "}";
        String submissionWrapperJson = "{\n" + "  \"_embedded\" : {\n" + "    \"submissions\" : [ {\n" + "      " +
                "\"submissionId\" : \"5d49486089a92e27c0c9ab82\",\n" + "      \"publication\" : {\n" + "        \"publicationId\" : \"47412849\",\n" + "        \"pmid\" : \"31299468\",\n" + "        \"title\" : \"Genetic effects on efficacy to fluticasone propionate/salmeterol treatment in COPD.\",\n" + "        \"journal\" : \"Respir Med\",\n" + "        \"firstAuthor\" : \"Condreay LD\",\n" + "        \"status\" : \"EXPORTED\"\n" + "      },\n" + "      \"files\" : [ ],\n" + "      \"study_count\" : 0,\n" + "      \"sample_count\" : 0,\n" + "      \"association_count\" : 0,\n" + "      \"submission_status\" : \"STARTED\",\n" + "      \"created\" : {\n" + "        \"user\" : {\n" + "          \"name\" : \"Test User\",\n" + "          \"email\" : \"test@test.com\"\n" + "        },\n" + "        \"timestamp\" : \"2019-08-06T10:29:04.068+01:00\"\n" + "      },\n" + "      \"_links\" : {\n" + "        \"self\" : {\n" + "          \"href\" : \"http://localhost:8081/v1/submissions/5d49486089a92e27c0c9ab82\"\n" + "        }\n" + "      }\n" + "    } ]\n" + "  },\n" + "  \"_links\" : {\n" + "    \"self\" : {\n" + "      \"href\" : \"http://localhost:8081/v1/submissions\"\n" + "    }\n" + "  },\n" + "  \"page\" : {\n" + "    \"size\" : 20,\n" + "    \"totalElements\" : 1,\n" + "    \"totalPages\" : 1,\n" + "    \"number\" : 0\n" + "  }\n" + "}";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JodaModule());
            DepositionSubmissionList wrapper = objectMapper.readValue(submissionWrapperJson, DepositionSubmissionList.class);
            assertNotNull(wrapper);
            assertNotNull(wrapper.getWrapper().getSubmissions());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testCreateSubmissionWrapper() {
        DepositionSubmissionList wrapper = new DepositionSubmissionList();
        wrapper.setWrapper(new DepositionSubmissionWrapper());
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
        String response = "{\n" + "  \"submissionId\" : \"5d403e35fc5fea00012696ff\",\n" + "  \"publication\" : {\n" + "    \"publicationId\" : \"5d403399218e4cbdf409e2fd\",\n" + "    \"pmid\" : \"21044949\",\n" + "    \"title\" : \"ELF1 is associated with systemic lupus erythematosus in Asian populations.\",\n" + "    \"journal\" : \"Hum Mol Genet\",\n" + "    \"firstAuthor\" : \"Yang J\",\n" + "    \"publicationDate\" : \"2010-11-02\",\n" + "    \"correspondingAuthor\" : {\n" + "      \"authorName\" : \"Jing Yang\"\n" + "    },\n" + "    \"status\" : \"ELIGIBLE\"\n" + "  },\n" + "  \"files\" : [ {\n" + "    \"fileUploadId\" : \"5d4052cafc5fea0001269713\",\n" + "    \"status\" : \"VALID\",\n" + "    \"fileName\" : \"file\",\n" + "    \"fileSize\" : 13600\n" + "  } ],\n" + "  \"study_count\" : 2,\n" + "  \"sample_count\" : 7,\n" + "  \"association_count\" : 4,\n" + "  \"submission_status\" : \"VALID\",\n" + "  \"metadata_status\" : \"VALID\",\n" + "  \"summary_statistics_status\" : \"NA\",\n" + "  \"created\" : {\n" + "    \"user\" : {\n" + "      \"name\" : \"Test User\",\n" + "      \"email\" : \"test@test.com\"\n" + "    },\n" + "    \"timestamp\" : \"2019-07-30T12:55:17.384Z\"\n" + "  },\n" + "  \"_links\" : {\n" + "    \"self\" : {\n" + "      \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d403e35fc5fea00012696ff\"\n" + "    },\n" + "    \"files\" : {\n" + "      \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/uploads\"\n" + "    },\n" + "    \"studies\" : {\n" + "      \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/studies\"\n" + "    },\n" + "    \"samples\" : {\n" + "      \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/samples\"\n" + "    },\n" + "    \"associations\" : {\n" + "      \"href\" : \"http://193.62.54.159/v1/submissions/5d403e35fc5fea00012696ff/associations\"\n" + "    }\n" + "  }\n" + "}";
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
}