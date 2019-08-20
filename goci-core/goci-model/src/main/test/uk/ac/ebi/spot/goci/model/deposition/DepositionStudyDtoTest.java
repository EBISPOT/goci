package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.Test;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionStudyListWrapper;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DepositionStudyDtoTest {
    @Test
    public void testReadStudiesResponse() {
        String json = "{\n" + "  \"_embedded\" : {\n" + "    \"studies\" : [ {\n" +
                "      \"study_tag\" : \"30237584_1\",\n" +
                "      \"genotyping_technology\" : \"Genome-wide genotyping array\",\n" +
                "      \"array_manufacturer\" : \"Illumina\",\n" + "      \"array_information\" : \"\",\n" +
                "      \"imputation\" : false,\n" + "      \"variant_count\" : 696317,\n" +
                "      \"statistical_model\" : \"\",\n" + "      \"study_description\" : \"Primary validation\",\n" +
                "      \"trait\" : \"Resistant hypertension\",\n" +
                "      \"summary_statistics_file\" : \"EFO_1002006\",\n" + "      \"checksum\" : \"\",\n" +
                "      \"summary_statistics_assembly\" : \"\",\n" + "      \"_links\" : {\n" +
                "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d514e99dcf21d0001eee221\"\n" +
                "        }\n" + "      }\n" + "    }, {\n" + "      \"study_tag\" : \"30237584_2\",\n" +
                "      \"genotyping_technology\" : \"Genome-wide genotyping array\",\n" +
                "      \"array_manufacturer\" : \"Illumina\",\n" + "      \"array_information\" : \"\",\n" +
                "      \"imputation\" : false,\n" + "      \"variant_count\" : 696317,\n" +
                "      \"statistical_model\" : \"\",\n" + "      \"study_description\" : \"Secondary validation\",\n" +
                "      \"trait\" : \"Resistant hypertension\",\n" +
                "      \"summary_statistics_file\" : \"EFO_1002006\",\n" + "      \"checksum\" : \"\",\n" +
                "      \"summary_statistics_assembly\" : \"\",\n" + "      \"_links\" : {\n" +
                "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d514e99dcf21d0001eee221\"\n" +
                "        }\n" + "      }\n" + "    } ]\n" + "  },\n" + "  \"_links\" : {\n" + "    \"self\" : {\n" +
                "      \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d514e99dcf21d0001eee221/studies\"\n" +
                "    }\n" + "  },\n" + "  \"page\" : {\n" + "    \"size\" : 20,\n" + "    \"totalElements\" : 2,\n" +
                "    \"totalPages\" : 1,\n" + "    \"number\" : 0\n" + "  }\n" + "}";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JodaModule());
            DepositionStudyListWrapper studies = objectMapper.readValue(json, DepositionStudyListWrapper.class);
            assertNotNull(studies);
//            assertNotNull(publication.getPublications().get(0).getPublicationDate().year());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}