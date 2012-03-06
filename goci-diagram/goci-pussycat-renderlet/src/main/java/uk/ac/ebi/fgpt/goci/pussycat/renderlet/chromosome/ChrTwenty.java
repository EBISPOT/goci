package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import net.sourceforge.fluxion.spi.ServiceProvider;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 01/03/12
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */

@ServiceProvider
public class ChrTwenty extends ChromosomeRenderlet{

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/20.svg");

    }

    public String getName() {
        return "Chromosome 20";
    }
}
