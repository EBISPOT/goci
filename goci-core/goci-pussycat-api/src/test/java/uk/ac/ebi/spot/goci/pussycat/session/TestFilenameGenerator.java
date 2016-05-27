package uk.ac.ebi.spot.goci.pussycat.session;

import org.junit.Test;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.spot.goci.pussycat.lang.Filter;
import uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static uk.ac.ebi.spot.goci.pussycat.lang.Filtering.refine;
import static uk.ac.ebi.spot.goci.pussycat.lang.Filtering.template;

/**
 * Created by dwelter on 02/09/15.
 */
public class TestFilenameGenerator {

    @Test
    public void testGenerator() {

        int e1 = -8;
        int m1 = 5;
        double p1 = m1 * Math.pow(10, e1);

        Association association = template(Association.class);
        Filter f1 = refine(association).on(association.getPvalue()).hasValues(0.0, p1);

        Filter f2 = refine(association).on(association.getPvalue()).hasValues(0.0, p1);


        TestSession session = new TestSession();


        String n1 = session.generateFilename(f1);
        System.out.println(n1);
        String n2 = session.generateFilename(f2);
        System.out.println(n2);
        String n3 = session.generateFilename();
        System.out.println(n3);

        assertEquals(n1, n2);

        assertNotNull(n3);

    }

    public class TestSession extends AbstractSVGIOPussycatSession {
        @Override
        public String getSessionID() {
            return "foo";
        }

        @Override
        public String performRendering(RenderletNexus renderletNexus, Filter... filters)
                throws PussycatSessionNotReadyException {
            return "";
        }

        @Override
        public Set<URI> getRelatedTraits(String traitName) {
            return Collections.emptySet();
        }
    }


}
