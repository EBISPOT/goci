package uk.ac.ebi.spot.goci.curation.service.deposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSampleDto;

import java.io.IOException;

import static org.junit.Assert.*;

public class DepositionSampleServiceTest {

    @Test
    public void testBuildDescription() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DepositionSampleService service = new DepositionSampleService();
            String json = "{\n" + "\t\"study_tag\": \"Study1\",\n" + "\t\"size\": 500,\n" +
                    "\t\"sample_description\": \"Sample description\",\n" +
                    "\t\"ancestry_category\": \"European\",\n" + "\t\"ancestry\": \"Italian\",\n" +
                    "\t\"ancestry_description\": \"Val Borbera\",\n" + "\t\"country_recruitement\": \"Italy\",\n" +
                    "\t\"stage\": \"discovery\"\n" + "}";
            DepositionSampleDto dto = null;
            dto = mapper.readValue(json, DepositionSampleDto.class);
            String description = service.buildDescription(dto);
            assertEquals("500 Italian ancestry individuals", description);

            json = "{\n" + "\t\"study_tag\": \"Study1\",\n" + "\t\"size\": 200,\n" +
                    "\t\"sample_description\": \"Sample description\",\n" +
                    "\t\"ancestry_category\": \"European\",\n" + "\t\"ancestry\": \"French|Spanish\",\n" +
                    "\t\"country_recruitement\": \"France|Spain\",\n" + "\t\"stage\": \"replication\"\n" +
                    "}";
            dto = mapper.readValue(json, DepositionSampleDto.class);
            description = service.buildDescription(dto);
            assertEquals("200 French ancestry, Spanish ancestry individuals", description);
            
            json = "{\n" + "\t\"study_tag\": \"Study2\",\n" + "\t\"size\": 500,\n" + "\t\"cases\": 300,\n" +
                    "\t\"controls\": 200,\n" + "\t\"sample_description\": \"Sample description\",\n" +
                    "\t\"ancestry_category\": \"Asian unspecified\",\n" +
                    "\t\"country_recruitement\": \"NR\",\n" + "\t\"stage\": \"discovery\"\n" + "}";
            dto = mapper.readValue(json, DepositionSampleDto.class);
            description = service.buildDescription(dto);
            assertEquals("300 Asian ancestry cases, 200 Asian ancestry controls", description);

            json = "{\n" + "\t\"study_tag\": \"Study1\",\n" + "\t\"size\": 200,\n" +
                    "\t\"sample_description\": \"Sample description\",\n" +
                    "\t\"ancestry_category\": \"African American or Afro-Caribbean\",\n" +
                    "\t\"country_recruitement\": \"U.S.\",\n" + "\t\"stage\": \"replication\"\n" + "}";
            dto = mapper.readValue(json, DepositionSampleDto.class);
            description = service.buildDescription(dto);
            assertEquals("200 African American or Afro-Caribbean individuals",
                    description);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}