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
public class ChrFive extends ChromosomeRenderlet{

    private IRI chromIRI = IRI.create("http://www.ebi.ac.uk/efo/gwas-diagram/EFO_GD00009");

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/5.svg");

    }

    public String getName() {
        return "Chromosome 5";
    }

    public IRI getIRI(){
        return chromIRI;
    }
}
