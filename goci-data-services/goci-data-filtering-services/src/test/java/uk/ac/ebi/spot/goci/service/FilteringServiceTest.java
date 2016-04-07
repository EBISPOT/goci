package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.model.FilterAssociation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwelter on 07/04/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class FilteringServiceTest {

    private List<FilterAssociation> assocs;

    private FilteringService filteringService;


    @Before
    public void setUp() {
         assocs = new ArrayList<>();


        assocs.add(new FilterAssociation(1, "rs724016-A", 3, -158, "3", "141386728"));
        assocs.add(new FilterAssociation(2, "rs143384-A", 1, -121, "20", "35437976"));
        assocs.add(new FilterAssociation(3, "rs8756-A", 5, -90, "12", "65965972"));
        assocs.add(new FilterAssociation(4, "rs42039-T", 4, -88, "7", "92615108"));
        assocs.add(new FilterAssociation(5, "rs724016-A", 3, -86, "3", "141386728"));
        assocs.add(new FilterAssociation(6, "rs1812175-A", 2, -86, "4", "144653692"));
        assocs.add(new FilterAssociation(7, "rs806794-A", 5, -74, "6", "26200449"));
        assocs.add(new FilterAssociation(8, "rs12393627-G", 5, -12, "X", "2967682"));
        assocs.add(new FilterAssociation(9, "rs7692995-T", 1, -71, "4", "17935011"));
        assocs.add(new FilterAssociation(10, "rs798497-A", 2, -71, "7", "2756323"));

        filteringService = new FilteringService(assocs);

    }


    @Test
    public void testChromSort(){
        filteringService.sortByChromosomeName();

    }
}
