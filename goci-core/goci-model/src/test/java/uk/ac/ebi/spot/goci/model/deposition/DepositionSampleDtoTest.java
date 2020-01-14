package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.Test;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionSampleListWrapper;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionStudyListWrapper;

import java.io.IOException;

import static org.junit.Assert.*;

public class DepositionSampleDtoTest {
    @Test
    public void testReadSamplesResponse() {
        String json = "{\n" + "  \"_embedded\" : {\n" + "    \"samples\" : [ {\n" +
                "      \"study_tag\" : \"30237584_1\",\n" + "      \"size\" : 657,\n" + "      \"cases\" : 226,\n" +
                "      \"controls\" : 431,\n" + "      \"sample_description\" : \"\",\n" +
                "      \"ancestry_category\" : \"European\",\n" + "      \"ancestry\" : \"\",\n" +
                "      \"ancestry_description\" : \"\",\n" + "      \"countryRecruitement\" : \"Germany\",\n" +
                "      \"stage\" : \"Discovery\",\n" + "      \"_links\" : {\n" + "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d515077dcf21d0001eee22b\"\n" +
                "        }\n" + "      }\n" + "    }, {\n" + "      \"study_tag\" : \"30237584_1\",\n" +
                "      \"size\" : 537,\n" + "      \"cases\" : 143,\n" + "      \"controls\" : 394,\n" +
                "      \"sample_description\" : \"\",\n" +
                "      \"ancestry_category\" : \"Hispanic or Latin American\",\n" +
                "      \"ancestry\" : \"Hispanic\",\n" + "      \"ancestry_description\" : \"\",\n" +
                "      \"countryRecruitement\" : \"Germany\",\n" + "      \"stage\" : \"Discovery\",\n" +
                "      \"_links\" : {\n" + "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d515077dcf21d0001eee22b\"\n" +
                "        }\n" + "      }\n" + "    }, {\n" + "      \"study_tag\" : \"30237584_1\",\n" +
                "      \"size\" : 263,\n" + "      \"cases\" : 71,\n" + "      \"controls\" : 192,\n" +
                "      \"sample_description\" : \"\",\n" + "      \"ancestry_category\" : \"European\",\n" +
                "      \"ancestry\" : \"\",\n" + "      \"ancestry_description\" : \"\",\n" +
                "      \"countryRecruitement\" : \"Germany\",\n" + "      \"stage\" : \"Replication\",\n" +
                "      \"_links\" : {\n" + "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d515077dcf21d0001eee22b\"\n" +
                "        }\n" + "      }\n" + "    }, {\n" + "      \"study_tag\" : \"30237584_1\",\n" +
                "      \"size\" : 322,\n" + "      \"cases\" : 83,\n" + "      \"controls\" : 192,\n" +
                "      \"sample_description\" : \"\",\n" +
                "      \"ancestry_category\" : \"Hispanic or Latin American\",\n" +
                "      \"ancestry\" : \"Hispanic\",\n" + "      \"ancestry_description\" : \"\",\n" +
                "      \"countryRecruitement\" : \"Germany\",\n" + "      \"stage\" : \"Replication\",\n" +
                "      \"_links\" : {\n" + "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d515077dcf21d0001eee22b\"\n" +
                "        }\n" + "      }\n" + "    }, {\n" + "      \"study_tag\" : \"30237584_2\",\n" +
                "      \"size\" : 920,\n" + "      \"cases\" : 297,\n" + "      \"controls\" : 623,\n" +
                "      \"sample_description\" : \"\",\n" + "      \"ancestry_category\" : \"European\",\n" +
                "      \"ancestry\" : \"\",\n" + "      \"ancestry_description\" : \"\",\n" +
                "      \"countryRecruitement\" : \"Germany\",\n" + "      \"stage\" : \"Discovery\",\n" +
                "      \"_links\" : {\n" + "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d515077dcf21d0001eee22b\"\n" +
                "        }\n" + "      }\n" + "    }, {\n" + "      \"study_tag\" : \"30237584_2\",\n" +
                "      \"size\" : 859,\n" + "      \"cases\" : 226,\n" + "      \"controls\" : 633,\n" +
                "      \"sample_description\" : \"\",\n" +
                "      \"ancestry_category\" : \"Hispanic or Latin American\",\n" +
                "      \"ancestry\" : \"Hispanic\",\n" + "      \"ancestry_description\" : \"\",\n" +
                "      \"countryRecruitement\" : \"Germany\",\n" + "      \"stage\" : \"Discovery\",\n" +
                "      \"_links\" : {\n" + "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d515077dcf21d0001eee22b\"\n" +
                "        }\n" + "      }\n" + "    }, {\n" + "      \"study_tag\" : \"30237584_2\",\n" +
                "      \"size\" : 2417,\n" + "      \"cases\" : 1946,\n" + "      \"controls\" : 471,\n" +
                "      \"sample_description\" : \"\",\n" + "      \"ancestry_category\" : \"European\",\n" +
                "      \"ancestry\" : \"\",\n" + "      \"ancestry_description\" : \"\",\n" +
                "      \"countryRecruitement\" : \"Germany\",\n" + "      \"stage\" : \"Replication\",\n" +
                "      \"_links\" : {\n" + "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d515077dcf21d0001eee22b\"\n" +
                "        }\n" + "      }\n" + "    } ]\n" + "  },\n" + "  \"_links\" : {\n" + "    \"self\" : {\n" +
                "      \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d515077dcf21d0001eee22b/samples\"\n" +
                "    }\n" + "  },\n" + "  \"page\" : {\n" + "    \"size\" : 20,\n" + "    \"totalElements\" : 7,\n" +
                "    \"totalPages\" : 1,\n" + "    \"number\" : 0\n" + "  }\n" + "}";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JodaModule());
            DepositionSampleListWrapper samples = objectMapper.readValue(json, DepositionSampleListWrapper.class);
            assertNotNull(samples);
//            assertNotNull(publication.getPublications().get(0).getPublicationDate().year());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}