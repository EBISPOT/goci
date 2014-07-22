package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.model.IRI;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 01/03/12
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */

@ServiceProvider
public class ChrNineteen extends ChromosomeRenderlet{

    private IRI chromIRI = IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI + "_19");
    private int position = 18;

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/19.svg");

    }

    public String getName() {
        return "Chromosome 19";
    }

    public IRI getChromIRI(){
        return chromIRI;
    }

    @Override
    protected int getPosition() {
        return position;
    }
}
