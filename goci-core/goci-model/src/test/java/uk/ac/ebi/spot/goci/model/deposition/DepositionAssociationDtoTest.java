package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.Test;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionAssociationListWrapper;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionPublicationListWrapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class DepositionAssociationDtoTest {

    @Test
    public void testReadAssociationResponse(){
        String json = "{\n" + "  \"_embedded\" : {\n" + "    \"associations\" : [ {\n" +
                "      \"study_tag\" : \"30237584_1\",\n" + "      \"variant_id\" : \"rs11749255\",\n" +
                "      \"pvalue\" : \"0.0000003\",\n" + "      \"effect_allele\" : \"A\",\n" +
                "      \"other_allele\" : \"\",\n" + "      \"_links\" : {\n" + "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d514e99dcf21d0001eee221\"\n" +
                "        }\n" + "      }\n" + "    }, {\n" + "      \"study_tag\" : \"30237584_2\",\n" +
                "      \"variant_id\" : \"rs12228810\",\n" + "      \"pvalue\" : \"0.000009\",\n" +
                "      \"effect_allele\" : \"C\",\n" + "      \"other_allele\" : \"\",\n" + "      \"_links\" : {\n" +
                "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d514e99dcf21d0001eee221\"\n" +
                "        }\n" + "      }\n" + "    }, {\n" + "      \"study_tag\" : \"30237584_2\",\n" +
                "      \"variant_id\" : \"rs16934621\",\n" + "      \"pvalue\" : \"0.000005\",\n" +
                "      \"effect_allele\" : \"A\",\n" + "      \"other_allele\" : \"\",\n" + "      \"_links\" : {\n" +
                "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d514e99dcf21d0001eee221\"\n" +
                "        }\n" + "      }\n" + "    }, {\n" + "      \"study_tag\" : \"30237584_2\",\n" +
                "      \"variant_id\" : \"rs3766160\",\n" + "      \"pvalue\" : \"0.000001\",\n" +
                "      \"effect_allele\" : \"A\",\n" + "      \"other_allele\" : \"\",\n" + "      \"_links\" : {\n" +
                "        \"parent\" : {\n" +
                "          \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d514e99dcf21d0001eee221\"\n" +
                "        }\n" + "      }\n" + "    } ]\n" + "  },\n" + "  \"_links\" : {\n" + "    \"self\" : {\n" +
                "      \"href\" : \"http://193.62.54.159/backend/v1/submissions/5d514e99dcf21d0001eee221/associations\"\n" +
                "    }\n" + "  },\n" + "  \"page\" : {\n" + "    \"size\" : 20,\n" + "    \"totalElements\" : 4,\n" +
                "    \"totalPages\" : 1,\n" + "    \"number\" : 0\n" + "  }\n" + "}\n";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JodaModule());
            DepositionAssociationListWrapper
                    associations = objectMapper.readValue(json, DepositionAssociationListWrapper.class);
            assertNotNull(associations);
            assertNotNull(associations.getAssociations().getAssociations().get(0));
            BigDecimal pValue = associations.getAssociations().getAssociations().get(0).getPValue();
            assertNotNull(pValue);
            BigInteger sv = pValue.unscaledValue();
            int exponent = pValue.precision() - pValue.scale() - 1;
            assertEquals(sv.intValue(), 3);
            assertEquals(pValue.scale(), -7);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}