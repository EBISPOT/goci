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

}