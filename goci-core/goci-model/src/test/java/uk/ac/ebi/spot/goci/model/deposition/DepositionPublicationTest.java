package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
        try{
            Resource resource = new ClassPathResource("publication.json");
            assertTrue(resource.exists());
        ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            DepositionPublication publication = objectMapper.readValue(resource.getFile(), DepositionPublication.class);
            assertNotNull(publication);
            assertNotNull(publication.getStatus());
            assertNotNull(publication.getPublicationDate().year());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testReadAllPublications(){
        try{
            Resource resource = new ClassPathResource("publications.json");
            assertTrue(resource.exists());

        ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            DepositionPublicationListWrapper
                    publication = objectMapper.readValue(resource.getFile(), DepositionPublicationListWrapper.class);
            assertNotNull(publication);
            assertNotNull(publication.getPublications().getPublications().get(0).getPublicationDate().year());
            assertNotNull(publication.getPublications().getPublications().get(0).getStatus());
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