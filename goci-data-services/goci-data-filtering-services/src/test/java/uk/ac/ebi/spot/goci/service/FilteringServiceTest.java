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

        assocs.add(new FilterAssociation("rs798497-A", 2.1, -71, "7", "2756323"));
        assocs.add(new FilterAssociation("rs724016-A", 3.0, -158, "3", "141386728"));
        assocs.add(new FilterAssociation("rs7692995-T", 1.17, -71, "4", "17935011"));
        assocs.add(new FilterAssociation("rs143384-A", 1.83, -121, "20", "35437976"));
        assocs.add(new FilterAssociation("rs8756-A", 5.2, -90, "12", "65965972"));
        assocs.add(new FilterAssociation("rs724016-A", 3.32, -86, "3", "141386728"));
        assocs.add(new FilterAssociation("rs806794-A", 5.98, -74, "6", "26200449"));
        assocs.add(new FilterAssociation("rs12393627-G", 5.78, -12, "X", "2967682"));
        assocs.add(new FilterAssociation("rs42039-T", 4.0, -88, "7", "92615108"));
        assocs.add(new FilterAssociation("rs1812175-A", 2.0, -86, "4", "144653692"));
        assocs.add(new FilterAssociation("rs3118905-A", 1.0, -45, "13", "50531198"));
        assocs.add(new FilterAssociation("rs1155939-A", 1.0, -45, "6", "126544987"));
        assocs.add(new FilterAssociation("rs314263-T", 1.0, -42, "6", "104944870"));
        assocs.add(new FilterAssociation("rs2854207-C", 1.0, -42, "17", "63869747"));
        assocs.add(new FilterAssociation("rs4733724-A", 1.0, -41, "8", "129711482"));
        assocs.add(new FilterAssociation("rs1036821-A", 1.0, -30, "8", "134638240"));
        assocs.add(new FilterAssociation("rs720390-A", 1.0, -29, "3", "185830895"));
        assocs.add(new FilterAssociation("rs7849585-T", 1.0, -29, "9", "136220024"));
        assocs.add(new FilterAssociation("rs2079795-T", 2.0, -46, "17", "61419288"));
        assocs.add(new FilterAssociation("rs9650315-T", 2.0, -41, "8", "56243039"));
        assocs.add(new FilterAssociation("rs6918981-A", 2.0, -30, "6", "34270737"));
        assocs.add(new FilterAssociation("rs224329-T", 2.0, -30, "20", "35431781"));
        assocs.add(new FilterAssociation("rs1776897-T", 2.0, -29, "6", "34227234"));
        assocs.add(new FilterAssociation("rs2280470-A", 2.0, -44, "15", "88852395"));
        assocs.add(new FilterAssociation("rs4868126-T", 2.0, -29, "5", "171856465"));
        assocs.add(new FilterAssociation("rs17721822-A", 2.0, -29, "20", "6488949"));
        assocs.add(new FilterAssociation("rs13273123-A", 2.0, -29, "8", "56188232"));
        assocs.add(new FilterAssociation("rs4240326-A", 3.0, -43, "4", "144918112"));
        assocs.add(new FilterAssociation("rs3760318-A", 3.0, -41, "17", "30920697"));
        assocs.add(new FilterAssociation("rs2271266-T", 4.0, -46, "12", "102112266"));
        assocs.add(new FilterAssociation("rs4800452-T", 4.0, -30, "18", "23147647"));
        assocs.add(new FilterAssociation("rs10748128-T", 4.0, -29, "12", "69433878"));
        assocs.add(new FilterAssociation("rs2284746-C", 4.0, -29, "1", "16980180"));
        assocs.add(new FilterAssociation("rs10948222-C", 4.0, -11, "6", "45276678"));
        assocs.add(new FilterAssociation("rs12459943-A", 4.0, -11, "19", "10748832"));
        assocs.add(new FilterAssociation("rs7498403-C", 4.0, -6, "16", "79551124"));
        assocs.add(new FilterAssociation("rs16961557-G", 4.0, -6, "15", "48755931"));
        assocs.add(new FilterAssociation("rs7916663-A", 4.0, -6, "10", "16706611"));
        assocs.add(new FilterAssociation("rs1420956-A", 4.0, -6, "18", "27587981"));
        assocs.add(new FilterAssociation("rs4448343-A", 5.0, -30, "9", "95504088"));
        assocs.add(new FilterAssociation("rs2070776-A", 6.0, -41, "17", "63930138"));
        assocs.add(new FilterAssociation("rs4735677-A", 6.0, -30, "8", "77235955"));
        assocs.add(new FilterAssociation("rs4665736-T", 6.0, -16, "2", "24964730"));
        assocs.add(new FilterAssociation("rs10874746-T", 6.0, -11, "1", "92858414"));
        assocs.add(new FilterAssociation("rs7652177-?", 6.0, -11, "3", "172251287"));
        assocs.add(new FilterAssociation("rs10492321-A", 6.0, -11, "12", "93586312"));
        assocs.add(new FilterAssociation("rs6449353-T", 7.0, -46, "4", "18031865"));
        assocs.add(new FilterAssociation("rs6728302-A", 8.0, -44, "2", "232189251"));
        assocs.add(new FilterAssociation("rs6919534-A", 8.0, -31, "6", "35279126"));
        assocs.add(new FilterAssociation("rs552707-T", 9.0, -46, "7", "28165684"));

        for(FilterAssociation fa : assocs){
            fa.setPvalue(fa.getPvalueMantissa()*Math.pow(10,(double)fa.getPvalueExponent()));
        }

        filteringService = new FilteringService();
    }


    @Test
    public void testChromGroup(){

        System.out.println("Grouping by chromosome");
        Map<String, List<FilterAssociation>> byChrom = filteringService.groupByChromosomeName(assocs);

        String e = "rs2271266-T";
        assertEquals("By chromosome sort correct", e, byChrom.get("12").get(1).getStrongestAllele());

        for(String k : byChrom.keySet()){
            System.out.print(k + "-");

            for(FilterAssociation a : byChrom.get(k)){
                System.out.print("\t" + a.getStrongestAllele());
            }
            System.out.print("\n");

        }


        System.out.println("Sorting by BP location");
        Map<String, List<FilterAssociation>> byLocation = filteringService.sortByBPLocation(byChrom);

        for(String k : byLocation.keySet()){
            System.out.print(k + "-");

            for(FilterAssociation a : byLocation.get(k)){
                System.out.print("\t" + a.getStrongestAllele());
            }
            System.out.print("\n");

        }

        String l = "rs10748128-T";
        assertEquals("By location sort correct", l, byLocation.get("12").get(1).getStrongestAllele());


        System.out.println("Determining SNPs in LD");
        List<FilterAssociation> filtered = filteringService.filterTopAssociations(byLocation);

        int ldCount = 0;

        for(FilterAssociation f : filtered){

            if(f.getIsTopAssociation()) {
                System.out.print(f.getChromosomeName() + " top -");

                System.out.print("\t" + f.getStrongestAllele());
            }
            else{
                System.out.print(f.getChromosomeName() + " in LD -");
                System.out.print("\t" + f.getStrongestAllele());
                ldCount++;

            }
            System.out.print("\n");

        }

        assertEquals("SNPs in LD", 6, ldCount);

    }
}
