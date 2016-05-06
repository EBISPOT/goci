package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.goci.model.FilterAssociation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by dwelter on 07/04/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class FilteringServiceTest {

    private List<FilterAssociation> assocs;

    @Autowired
    private FilteringService filteringService;


    @Before
    public void setUp() {
         assocs = new ArrayList<>();

        assocs.add(new FilterAssociation(10, "rs798497-A", 2, -71, "7", "2756323"));
        assocs.add(new FilterAssociation(1, "rs724016-A", 3, -158, "3", "141386728"));
        assocs.add(new FilterAssociation(9, "rs7692995-T", 1, -71, "4", "17935011"));
        assocs.add(new FilterAssociation(2, "rs143384-A", 1, -121, "20", "35437976"));
        assocs.add(new FilterAssociation(3, "rs8756-A", 5, -90, "12", "65965972"));
        assocs.add(new FilterAssociation(5, "rs724016-A", 3, -86, "3", "141386728"));
        assocs.add(new FilterAssociation(7, "rs806794-A", 5, -74, "6", "26200449"));
        assocs.add(new FilterAssociation(8, "rs12393627-G", 5, -12, "X", "2967682"));
        assocs.add(new FilterAssociation(4, "rs42039-T", 4, -88, "7", "92615108"));
        assocs.add(new FilterAssociation(6, "rs1812175-A", 2, -86, "4", "144653692"));
        assocs.add(new FilterAssociation(11, "rs3118905-A", 1, -45, "13", "50531198"));
        assocs.add(new FilterAssociation(12, "rs1155939-A", 1, -45, "6", "126544987"));
        assocs.add(new FilterAssociation(13, "rs314263-T", 1, -42, "6", "104944870"));
        assocs.add(new FilterAssociation(14, "rs2854207-C", 1, -42, "17", "63869747"));
        assocs.add(new FilterAssociation(15, "rs4733724-A", 1, -41, "8", "129711482"));
        assocs.add(new FilterAssociation(16, "rs1036821-A", 1, -30, "8", "134638240"));
        assocs.add(new FilterAssociation(17, "rs720390-A", 1, -29, "3", "185830895"));
        assocs.add(new FilterAssociation(18, "rs7849585-T", 1, -29, "9", "136220024"));
        assocs.add(new FilterAssociation(19, "rs2079795-T", 2, -46, "17", "61419288"));
        assocs.add(new FilterAssociation(20, "rs9650315-T", 2, -41, "8", "56243039"));
        assocs.add(new FilterAssociation(21, "rs6918981-A", 2, -30, "6", "34270737"));
        assocs.add(new FilterAssociation(22, "rs224329-T", 2, -30, "20", "35431781"));
        assocs.add(new FilterAssociation(23, "rs1776897-T", 2, -29, "6", "34227234"));
        assocs.add(new FilterAssociation(24, "rs2280470-A", 2, -44, "15", "88852395"));
        assocs.add(new FilterAssociation(25, "rs4868126-T", 2, -29, "5", "171856465"));
        assocs.add(new FilterAssociation(26, "rs17721822-A", 2, -29, "20", "6488949"));
        assocs.add(new FilterAssociation(27, "rs13273123-A", 2, -29, "8", "56188232"));
        assocs.add(new FilterAssociation(28, "rs4240326-A", 3, -43, "4", "144918112"));
        assocs.add(new FilterAssociation(29, "rs3760318-A", 3, -41, "17", "30920697"));
        assocs.add(new FilterAssociation(30, "rs2271266-T", 4, -46, "12", "102112266"));
        assocs.add(new FilterAssociation(31, "rs4800452-T", 4, -30, "18", "23147647"));
        assocs.add(new FilterAssociation(32, "rs10748128-T", 4, -29, "12", "69433878"));
        assocs.add(new FilterAssociation(33, "rs2284746-C", 4, -29, "1", "16980180"));
        assocs.add(new FilterAssociation(34, "rs10948222-C", 4, -11, "6", "45276678"));
        assocs.add(new FilterAssociation(35, "rs12459943-A", 4, -11, "19", "10748832"));
        assocs.add(new FilterAssociation(36, "rs7498403-C", 4, -6, "16", "79551124"));
        assocs.add(new FilterAssociation(37, "rs16961557-G", 4, -6, "15", "48755931"));
        assocs.add(new FilterAssociation(38, "rs7916663-A", 4, -6, "10", "16706611"));
        assocs.add(new FilterAssociation(39, "rs1420956-A", 4, -6, "18", "27587981"));
        assocs.add(new FilterAssociation(40, "rs4448343-A", 5, -30, "9", "95504088"));
        assocs.add(new FilterAssociation(41, "rs2070776-A", 6, -41, "17", "63930138"));
        assocs.add(new FilterAssociation(42, "rs4735677-A", 6, -30, "8", "77235955"));
        assocs.add(new FilterAssociation(43, "rs4665736-T", 6, -16, "2", "24964730"));
        assocs.add(new FilterAssociation(44, "rs10874746-T", 6, -11, "1", "92858414"));
        assocs.add(new FilterAssociation(45, "rs7652177-?", 6, -11, "3", "172251287"));
        assocs.add(new FilterAssociation(46, "rs10492321-A", 6, -11, "12", "93586312"));
        assocs.add(new FilterAssociation(47, "rs6449353-T", 7, -46, "4", "18031865"));
        assocs.add(new FilterAssociation(48, "rs6728302-A", 8, -44, "2", "232189251"));
        assocs.add(new FilterAssociation(49, "rs6919534-A", 8, -31, "6", "35279126"));
        assocs.add(new FilterAssociation(50, "rs552707-T", 9, -46, "7", "28165684"));


        filteringService = new FilteringService();
    }


    @Test
    public void testChromGroup(){
        System.out.println("Grouping by chromosome");
        Map<String, List<FilterAssociation>> byChrom = filteringService.groupByChromosomeName(assocs);

        Integer e = 30;
        assertEquals("By chromosome sort correct", e, byChrom.get("12").get(1).getRowNumber());

        for(String k : byChrom.keySet()){
            System.out.print(k + "-");

            for(FilterAssociation a : byChrom.get(k)){
                System.out.print("\t" + a.getRowNumber());
            }
            System.out.print("\n");

        }


        System.out.println("Sorting by BP location");
        Map<String, List<FilterAssociation>> byLocation = filteringService.sortByBPLocation(byChrom);

        for(String k : byLocation.keySet()){
            System.out.print(k + "-");

            for(FilterAssociation a : byLocation.get(k)){
                System.out.print("\t" + a.getRowNumber());
            }
            System.out.print("\n");

        }

        Integer l = 32;
        assertEquals("By location sort correct", l, byLocation.get("12").get(1).getRowNumber());


        System.out.println("Determining SNPs in LD");
        List<FilterAssociation> filtered = filteringService.filterTopAssociations(byLocation);

        int ldCount = 0;

        for(FilterAssociation f : filtered){

            if(f.getIsTopAssociation()) {
                System.out.print(f.getChromosomeName() + " top -");

                System.out.print("\t" + f.getRowNumber());
            }
            else{
                System.out.print(f.getChromosomeName() + " in LD -");
                System.out.print("\t" + f.getRowNumber());
                ldCount++;

            }
            System.out.print("\n");

        }

        assertEquals("SNPs in LD", 6, ldCount);

    }
}
