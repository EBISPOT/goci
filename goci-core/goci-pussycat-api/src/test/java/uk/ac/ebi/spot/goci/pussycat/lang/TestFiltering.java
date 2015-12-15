package uk.ac.ebi.spot.goci.pussycat.lang;

import org.junit.Test;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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


        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
        Date from = null;
        Date to = null;
        try {
            from = df1.parse("2005-01-01");
            to = df1.parse("2010-01-01");
        }
        catch (ParseException e) {
            e.printStackTrace();
        }


        String fromValue = df2.format(from).toString();
        String toValue = df2.format(to).toString();

        System.out.println(fromValue);
        System.out.println(toValue);


        Study study = template(Study.class);
        Filter dateFilter = refine(study).on(study.getPublicationDate()).hasRange(fromValue, toValue);

        Filter dateFilter2 = refine(study).on(study.getPublicationDate()).hasRange(fromValue, toValue);

        assertEquals("Filter type does not match expected",
                     Study.class,
                     dateFilter.getFilteredType());

        assertEquals("Filtered method does not match expected",
                     "getPublicationDate",
                     dateFilter.getFilteredMethod().getName());


        assertEquals("Filtered value does not match expected",
                     "2010-01-01T00:00:00.0",
                     dateFilter.getFilteredRange().to());

        assertEquals("Hashcodes of the two date filters differ",
                     dateFilter.hashCode(),
                     dateFilter2.hashCode());
    }


}
