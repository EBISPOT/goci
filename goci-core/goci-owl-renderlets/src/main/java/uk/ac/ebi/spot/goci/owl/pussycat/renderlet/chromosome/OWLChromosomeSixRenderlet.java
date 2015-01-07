package uk.ac.ebi.spot.goci.owl.pussycat.renderlet.chromosome;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.ebi.spot.goci.lang.OntologyConstants;

import java.net.URI;
import java.net.URL;

/**
 * Created by IntelliJ IDEA. User: dwelter Date: 01/03/12 Time: 10:33 To change this template use File | Settings | File
 * Templates.
 */

@ServiceProvider
public class OWLChromosomeSixRenderlet extends OWLChromosomeRenderlet {
    private URI chromosomeURI = URI.create(OntologyConstants.CHROMOSOME_CLASS_IRI + "_6");
    private int position = 5;

    @Override public String getName() {
        return "Chromosome 6";
    }

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/6.svg");
    }

    @Override
    protected URI getChromosomeURI() {
        return chromosomeURI;
    }

    @Override
    protected int getPosition() {
        return position;
    }
}
