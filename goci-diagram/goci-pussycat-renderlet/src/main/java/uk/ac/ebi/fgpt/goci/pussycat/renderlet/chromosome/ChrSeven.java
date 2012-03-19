package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.model.IRI;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 01/03/12
 * Time: 10:33
 * To change this template use File | Settings | File Templates.
 */

@ServiceProvider
public class ChrSeven extends ChromosomeRenderlet{

    private IRI chromIRI = IRI.create("http://www.ebi.ac.uk/efo/gwas-diagram/EFO_GD00011");
    private int position = 6;

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/7.svg");

    }

    @Override
    protected IRI getIRI() {
        return chromIRI;
    }

    @Override
    protected int getPosition() {
        return position;
    }

    public String getName() {
        return "Chromosome 7";
    }
}
