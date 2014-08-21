package uk.ac.ebi.fgpt.goci.sparql.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.ColourMapper;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.TraitRenderlet;
import uk.ac.ebi.fgpt.goci.sparql.pussycat.query.QueryManager;
import uk.ac.ebi.fgpt.goci.sparql.pussycat.query.SparqlTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
@ServiceProvider
public class SparqlTraitRenderlet extends TraitRenderlet<SparqlTemplate, URI> {
    @Override public boolean canRender(RenderletNexus nexus, Object renderingContext, Object renderingEntity) {
        if (renderingContext instanceof SparqlTemplate && renderingEntity instanceof URI) {
            SparqlTemplate template = (SparqlTemplate) renderingContext;
            URI uri = (URI) renderingEntity;
            return template.ask(uri, URI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI));
        }
        else {
            return false;
        }
    }

    protected Set<URI> getAssociationsForTrait(SparqlTemplate sparqlTemplate, URI trait)
            throws DataIntegrityViolationException {
        return QueryManager.getCachingInstance().getAssociationsForTrait(sparqlTemplate, trait);
    }

    protected URI getBandForAssociation(SparqlTemplate sparqlTemplate, URI association)
            throws DataIntegrityViolationException {
        URI bandIndividual =
                QueryManager.getCachingInstance().getCytogeneticBandForAssociation(sparqlTemplate, association);
        getLog().trace("Band for association '" + association + "' is '" + bandIndividual + "'");
        return bandIndividual;
    }

    protected List<SVGArea> getLocationsOfOtherTraitsinBand(RenderletNexus nexus,
                                                            SparqlTemplate sparqlTemplate,
                                                            URI band)
            throws DataIntegrityViolationException {
        Set<URI> allTraits =
                QueryManager.getCachingInstance()
                        .getTraitsLocatedInCytogeneticBand(sparqlTemplate, band);
        getLog().trace("Identified " + allTraits.size() + " traits in band '" + band + "'");

        List<SVGArea> locations = new ArrayList<SVGArea>();

        // fetch the location of all trait + band pairs that have been rendered so far
        for (URI nextTrait : allTraits) {
            SVGArea location = nexus.getLocationOfRenderedEntity(Arrays.asList(nextTrait, band));
            if (location != null) {
                locations.add(location);
            }
        }

        // now sort
        Collections.sort(locations, new Comparator<SVGArea>() {
            @Override public int compare(SVGArea a1, SVGArea a2) {
                Double comp;
                double dY = a2.getY() - a1.getY();
                if (dY == 0) {
                    double dX = a2.getX() - a1.getX();
                    comp = dX > 0 ? Math.ceil(dX) : Math.floor(dX);
                }
                else {
                    comp = dY > 0 ? Math.ceil(dY) : Math.floor(dY);
                }
                return comp.intValue();
            }
        });

        getLog().trace("Sorted locations for " + allTraits.size() + " traits - " +
                               locations.size() + " have been rendered");

        return locations;
    }

    protected String getTraitAttribute(SparqlTemplate sparqlTemplate, URI trait)
            throws DataIntegrityViolationException {
        // todo - query for rdfs:type and return
        return null;
    }

    protected String getTraitAssociationAttribute(SparqlTemplate sparqlTemplate, URI association)
            throws DataIntegrityViolationException {
        return association.getFragment();
    }

    protected String getTraitLabel(SparqlTemplate sparqlTemplate, URI individual) {
        // todo - get rdfs:label
        return null;
    }

    protected String getTraitColour(SparqlTemplate sparqlTemplate, URI trait) {
        String colour;
        URI type = null;
        // todo - query for rdfs:type
        if (ColourMapper.COLOUR_MAP.containsKey(type.toString())) {
            colour = ColourMapper.COLOUR_MAP.get(type.toString());
        }
        else {
            colour = "magenta";
            getLog().error("Could not identify a suitable colour category for trait " + trait);
        }
        return colour;
    }
}
