package uk.ac.ebi.spot.goci.curation.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmail;
import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmailToCurator;

import static org.junit.Assert.assertEquals;

/**
 * Created by emma on 09/03/2016.
 *
 * @author emma
 *         <p>
 *         Test for uk/ac/ebi/spot/goci/curation/model/mail/CurationSystemEmail.java
 */
@RunWith(MockitoJUnitRunner.class)
public class TestCurationSystemEmail {

    private CurationSystemEmail email;

    @Before
    public void setUp() throws Exception {
        email = new CurationSystemEmailToCurator();
        email.setBody("This is a test.");
    }

    @Test
    public void testAddBody() {
        String newTextToAdd = " Will this test pass?";
        email.addToBody(newTextToAdd);
        assertEquals("This is a test. Will this test pass?", email.getBody());
    }
}
