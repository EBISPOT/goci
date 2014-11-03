package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import junit.framework.TestCase;
import org.junit.Test;
import uk.ac.ebi.fgpt.goci.owl.pussycat.renderlet.chromosome.*;

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

        OWLChromosomeRenderlet chrom;

        ArrayList<OWLChromosomeRenderlet> all = new ArrayList<OWLChromosomeRenderlet>();

        chrom = new OWLChromosomeOneRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeTwoRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeThreeRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeFourRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeFiveRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeSixRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeSevenRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeEightRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeNineRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeTenRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeElevenRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeTwelveRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeThirteenRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeFourteenRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeFifteenRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeSixteenRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeSeventeenRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeEighteenRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeNineteenRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeTwentyRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeTwentyOneRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeTwentyTwoRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeXRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        chrom = new OWLChromosomeYRenderlet();
        System.out.println(chrom.getDisplayName() + "\t" + chrom.getSVGFile());
        all.add(chrom);

        assertEquals(24, all.size());
    }
}
