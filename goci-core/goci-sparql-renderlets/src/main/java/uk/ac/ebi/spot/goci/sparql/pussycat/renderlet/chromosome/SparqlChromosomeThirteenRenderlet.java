package uk.ac.ebi.spot.goci.sparql.pussycat.renderlet.chromosome;

import uk.ac.ebi.spot.goci.spi.ServiceProvider;
import uk.ac.ebi.spot.goci.ontology.OntologyConstants;

import java.net.URI;
import java.net.URL;

/**
 * Created by IntelliJ IDEA. User: dwelter Date: 01/03/12 Time: 10:34 To change this template use File | Settings | File
 * Templates.
 */

@ServiceProvider
public class SparqlChromosomeThirteenRenderlet extends SparqlChromosomeRenderlet {
    private URI chromosomeURI = URI.create(OntologyConstants.GWAS_ONTOLOGY_SCHEMA_IRI + "/Chromosome_13");
    private int position = 12;

    @Override public String getName() {
        return "Chromosome 13";
    }

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/13.svg");
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
