package uk.ac.ebi.spot.goci.sparql.pussycat.renderlet.chromosome;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.ebi.spot.goci.ontology.OntologyConstants;

import java.net.URI;
import java.net.URL;

/**
 * Created by IntelliJ IDEA. User: dwelter Date: 01/03/12 Time: 10:47 To change this template use File | Settings | File
 * Templates.
 */

@ServiceProvider
public class SparqlChromosomeTwentyRenderlet extends SparqlChromosomeRenderlet {
    private URI chromosomeURI = URI.create(OntologyConstants.CHROMOSOME_CLASS_IRI + "_20");
    private int position = 19;

    @Override public String getName() {
        return "Chromosome 20";
    }

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/20.svg");
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
