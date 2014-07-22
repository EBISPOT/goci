package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 02/03/12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class ChromTest2 extends TestCase {

    @Test
    public void testChromosomes(){

        ChromosomeRenderlet chrom;

        ArrayList<ChromosomeRenderlet> all = new ArrayList<ChromosomeRenderlet>();

        chrom = new ChrOne();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrTwo();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrThree();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrFour();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrFive();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrSix();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrSeven();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrEight();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrNine();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrTen();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrEleven();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrTwelve();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrThirteen();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrFourteen();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrFifteen();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrSixteen();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrSeventeen();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrEighteen();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrNineteen();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrTwenty();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrTwentyone();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrTwentytwo();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrX();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new ChrY();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        assertEquals(24, all.size());
    }
}
