package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.model.IRI;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 01/03/12
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */

@ServiceProvider
public class ChrY extends ChromosomeRenderlet{

    private IRI chromIRI = IRI.create("http://www.ebi.ac.uk/efo/gwas-diagram/EFO_GD00028");

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/Y.svg");

    }

    @Override
    protected IRI getIRI() {
        return chromIRI;
    }

    public String getName() {
        return "Chromosome Y";
    }
}
