package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A renderlet that is capable of rendering visualisations of "traits".  A trait is an OWL class from the experimental
 * factor ontology (EFO).  This renderlet retrieves the color encoding for each type of trait from the {@link
 * uk.ac.ebi.fgpt.goci.pussycat.layout.ColourMapper} class
 *
 * @author Dani Welter
 * @date 06/03/12
 */
@ServiceProvider
public abstract class TraitRenderlet<C, E> implements Renderlet<C, E> {
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override
    public String getName() {
        return "GWAS trait renderlet";
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getDescription() {
        return ("This is a renderlet displaying " + getDisplayName());
    }

    @Override
    public void render(RenderletNexus nexus, C reasoner, E trait) {
        getLog().trace("Trait: " + trait);
        try {
            // collect all the bands for which we need to render this trait, and list all associations in that band
            Map<E, Set<E>> bandToAssociationMap = new HashMap<E, Set<E>>();

            for (E association : getAssociationsForTrait(reasoner, trait)) {
                try {
                    // get the band for this association
                    E band = getBandForAssociation(reasoner, association);
                    if (!bandToAssociationMap.containsKey(band)) {
                        bandToAssociationMap.put(band, new HashSet<E>());
                    }
                    bandToAssociationMap.get(band).add(association);
                }
                catch (DataIntegrityViolationException e) {
                    getLog().error("Unable to render trait '" + trait + "' for association '" + association + "'", e);
                }
            }

            for (E band : bandToAssociationMap.keySet()) {
                try {
                    // get the location of any traits previously rendered in this band
                    List<SVGArea> locations = getLocationsOfOtherTraitsinBand(nexus, reasoner, band);

                    StringBuilder svg = new StringBuilder();
                    svg.append("<circle ");

                    // also grab the location of an association (should all be the same for the same band)
                    E association = bandToAssociationMap.get(band).iterator().next();
                    SVGArea associationLocation = nexus.getLocationOfRenderedEntity(association);
                    if (associationLocation.getTransform() != null) {
                        svg.append("transform='").append(associationLocation.getTransform()).append("' ");
                    }

                    double alength = associationLocation.getWidth();
                    double radius = 0.2 * alength;
                    double ax = associationLocation.getX();
                    double ay = associationLocation.getY();
                    double displacement = associationLocation.getHeight();
                    double cx, cy;
                    int size = locations.size();

                    int horizontal = size % 6;
                    int vertical = size / 6;

                    if (size == 0) {
                        cx = ax + alength + radius;
                    }
                    else {
                        if (vertical % 2 == 0) {
                            cx = ax + alength + (((2 * horizontal) + 1) * radius);
                        }
                        else {
                            cx = ax + alength + (((2 * horizontal) + 2) * radius);
                        }
                    }
                    cy = ay + displacement + (vertical * radius);

                    svg.append("cx='").append(Double.toString(cx)).append("' ");
                    svg.append("cy='").append(Double.toString(cy)).append("' ");
                    svg.append("r='").append(Double.toString(radius)).append("' ");

                    String colour = getTraitColour(reasoner, trait);

                    svg.append("fill='").append(colour).append("' ");
                    svg.append("stroke='black' ");
                    svg.append("stroke-width='0.5' ");

                    String traitName = getTraitLabel(reasoner, trait);
                    svg.append("gwasname='").append(traitName).append("' ");

                    String traitAttribute = getTraitAttribute(reasoner, trait);
                    getLog().trace("Setting CSS class for trait '" + trait + "' to " + traitAttribute);
                    svg.append("class='gwas-trait ").append(traitAttribute).append("'");
                    svg.append("fading='false' ");

                    StringBuilder associationAttribute = new StringBuilder();
                    Iterator<E> associationIt = bandToAssociationMap.get(band).iterator();
                    while (associationIt.hasNext()) {
                        associationAttribute.append(getTraitAssociationAttribute(reasoner, associationIt.next()));
                        if (associationIt.hasNext()) {
                            associationAttribute.append(",");
                        }
                    }
                    getLog().trace(
                            "Setting gwasassociation attribute for trait '" + trait + "' to " +
                                    associationAttribute.toString());
                    svg.append("gwasassociation='").append(associationAttribute.toString()).append("' ");
                    svg.append("/>");

                    SVGArea currentArea = new SVGArea(cx, cy, 2 * radius, 2 * radius, 0);

                    // this area is a conjunction of trait + band, so store as a List<OWLNamedIndividual> with 2 elements
                    RenderingEvent<List<E>> event =
                            new RenderingEvent<List<E>>(Arrays.asList(trait, band),
                                                        svg.toString(),
                                                        currentArea,
                                                        this);
                    nexus.renderingEventOccurred(event);
                }
                catch (DataIntegrityViolationException e) {
                    getLog().error("Cannot render trait '" + trait + "' in band '" + band + "'");
                }
            }
        }
        catch (DataIntegrityViolationException e) {
            getLog().error("Cannot render trait '" + trait + "'", e);
        }
    }

    protected abstract Set<E> getAssociationsForTrait(C reasoner, E trait) throws DataIntegrityViolationException;

    protected abstract E getBandForAssociation(C reasoner, E association) throws DataIntegrityViolationException;

    protected abstract List<SVGArea> getLocationsOfOtherTraitsinBand(RenderletNexus nexus, C reasoner, E band)
            throws DataIntegrityViolationException;

    protected abstract String getTraitAttribute(C reasoner, E trait) throws DataIntegrityViolationException;

    protected abstract String getTraitAssociationAttribute(C reasoner, E association)
            throws DataIntegrityViolationException;

    protected abstract String getTraitLabel(C reasoner, E individual);

    protected abstract String getTraitColour(C reasoner, E trait);
}



