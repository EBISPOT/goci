package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import net.sourceforge.fluxion.spi.ServiceProvider;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 01/03/12
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */

@ServiceProvider
public class ChrTwentytwo extends ChromosomeRenderlet{

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/22.svg");

    }

    public String getName() {
        return "Chromosome 22";
    }
}
