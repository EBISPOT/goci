package uk.ac.ebi.spot.goci.pussycat.lang;

import org.junit.Test;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import static org.junit.Assert.assertEquals;
import static uk.ac.ebi.spot.goci.pussycat.lang.Filtering.refine;
import static uk.ac.ebi.spot.goci.pussycat.lang.Filtering.template;

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
        Filter<SingleNucleotidePolymorphism, String> filter =
                refine(template).on(template.getRsId()).hasValue("rs123456");

        assertEquals("Filter type does not match expected",
                     SingleNucleotidePolymorphism.class,
                     filter.getFilteredType());

        assertEquals("Filtered method does not match expected",
                     "getRsId",
                     filter.getFilteredMethod().getName());

        assertEquals("Filtered value does not match expected",
                     "rs123456",
                     filter.getFilteredValues().get(0));

        Association template2 = template(Association.class);
        Filter<Association, Float> filter2 =
                refine(template2).on(template2.getPvalueMantissa()).hasValue(Float.valueOf("10"));

        assertEquals("Filter type does not match expected",
                     Association.class,
                     filter2.getFilteredType());

        assertEquals("Filtered method does not match expected",
                     "getPvalueMantissa",
                     filter2.getFilteredMethod().getName());

        assertEquals(Float.valueOf("10"),
                     filter2.getFilteredValues().get(0),
                     0.0d);
    }
}
