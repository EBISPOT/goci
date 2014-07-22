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
public class ChrEight extends ChromosomeRenderlet{

    private IRI chromIRI = IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI + "_8");
    private int position = 7;

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/8.svg");

    }

    public String getName() {
        return "Chromosome 8";
    }

    public IRI getChromIRI(){
        return chromIRI;
    }

    @Override
    protected int getPosition() {
        return position;
    }
}
