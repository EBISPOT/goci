package uk.ac.ebi.fgpt.goci.lang;

import org.junit.Test;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

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
        Filter<SingleNucleotidePolymorphism, String> filter =
                refine(template).on(template.getRSID()).hasValue("rs123456");

        assertEquals("Filter type does not match expected",
                     SingleNucleotidePolymorphism.class,
                     filter.getFilteredType());

        assertEquals("Filtered method does not match expected",
                     "getRSID",
                     filter.getFilteredMethod().getName());

        assertEquals("Filtered value does not match expected",
                     "rs123456",
                     filter.getFilteredValues().get(0));

        TraitAssociation template2 = template(TraitAssociation.class);
        Filter<TraitAssociation, Float> filter2 =
                refine(template2).on(template2.getPValue()).hasValue(Float.valueOf("10E-8"));

        assertEquals("Filter type does not match expected",
                     TraitAssociation.class,
                     filter2.getFilteredType());

        assertEquals("Filtered method does not match expected",
                     "getPValue",
                     filter2.getFilteredMethod().getName());

        assertEquals("Filtered value does not match expected",
                     0.00000001f,
                     (float)filter2.getFilteredValues().get(0));
    }
}
