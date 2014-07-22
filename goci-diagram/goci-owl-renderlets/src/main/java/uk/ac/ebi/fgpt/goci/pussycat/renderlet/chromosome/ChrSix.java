package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.model.IRI;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 01/03/12
 * Time: 10:33
 * To change this template use File | Settings | File Templates.
 */

@ServiceProvider
public class ChrSix extends ChromosomeRenderlet{

    private IRI chromIRI = IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI + "_6");
    private int position = 5;

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/6.svg");

    }

    @Override
    protected IRI getChromIRI() {
        return chromIRI;
    }

    @Override
    protected int getPosition() {
        return position;
    }

    public String getName() {
        return "Chromosome 6";
    }
}
