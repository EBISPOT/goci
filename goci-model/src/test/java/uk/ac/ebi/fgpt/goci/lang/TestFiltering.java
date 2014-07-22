package uk.ac.ebi.fgpt.goci.lang;

import org.junit.Test;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;

import static org.junit.Assert.assertEquals;
import static uk.ac.ebi.fgpt.goci.lang.Filtering.*;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 03/06/14
 */
public class TestFiltering {


    @Test
    public void testFilter() {
        SingleNucleotidePolymorphism template = template(SingleNucleotidePolymorphism.class);
        Filter filter = refine(template).on(template.getRSID()).hasValue("rs123456");

        assertEquals("Filter type does not match expected",
                     SingleNucleotidePolymorphism.class,
                     filter.getFilteredType());

    }
}
