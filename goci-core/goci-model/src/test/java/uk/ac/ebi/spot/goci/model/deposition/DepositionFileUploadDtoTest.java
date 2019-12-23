package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.Test;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionFileUploadListWrapper;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DepositionFileUploadDtoTest {
    @Test
    public void testReadFileUploadResponse() {
        String json = "{\n" + "  \"_embedded\" : {\n" + "    \"fileUploads\" : [ {\n" +
                "      \"fileUploadId\" : \"5d52af6207d5180001ecd186\",\n" + "      \"status\" : \"VALID\",\n" +
                "      \"type\" : \"METADATA\",\n" + "      \"fileName\" : \"file\",\n" +
                "      \"fileSize\" : 13654,\n" + "      \"_links\" : {\n" + "        \"self\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d514e99dcf21d0001eee221/uploads" +
                "/5d52af6207d5180001ecd186\"\n" +
                "        }\n" + "      }\n" + "    } ]\n" + "  },\n" + "  \"_links\" : {\n" + "    \"self\" : {\n" +
                "      \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d514e99dcf21d0001eee221/uploads\"\n" +
                "    }\n" + "  }\n" + "}";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JodaModule());
            DepositionFileUploadListWrapper files = objectMapper.readValue(json, DepositionFileUploadListWrapper.class);
            assertNotNull(files);
//            assertNotNull(publication.getPublications().get(0).getPublicationDate().year());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}