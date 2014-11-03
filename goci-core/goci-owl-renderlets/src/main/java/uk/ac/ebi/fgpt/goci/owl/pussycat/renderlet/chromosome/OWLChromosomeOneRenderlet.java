package uk.ac.ebi.fgpt.goci.owl.pussycat.renderlet.chromosome;


import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;

import java.net.URI;
import java.net.URL;

/**
 * Created by IntelliJ IDEA. User: dwelter Date: 01/03/12 Time: 10:31 To change this template use File | Settings | File
 * Templates.
 */

@ServiceProvider
public class OWLChromosomeOneRenderlet extends OWLChromosomeRenderlet {
    private URI chromosomeURI = URI.create(OntologyConstants.CHROMOSOME_CLASS_IRI + "_1");
    private int position = 0;

    @Override public String getName() {
        return "Chromosome 1";
    }

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/1.svg");
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
