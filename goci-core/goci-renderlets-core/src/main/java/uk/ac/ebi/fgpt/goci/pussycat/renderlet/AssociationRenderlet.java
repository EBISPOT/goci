package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGCanvas;

import java.util.HashMap;
import java.util.Map;

/**
 * Renderlet that can generate SVG for OWLIndividuals representing GWAS trait associations
 *
 * @author dwelter
 * @date 18/04/12
 */
public abstract class AssociationRenderlet<C, E> implements Renderlet<C, E> {
    private final Map<BandInformation, SVGArea> renderedBands;
    private Map<C, Map<BandInformation, BandInformation>> previousBandMapByContext;

    private Logger log = LoggerFactory.getLogger(getClass());

    public AssociationRenderlet() {
        this.previousBandMapByContext = new HashMap<C, Map<BandInformation, BandInformation>>();
        this.renderedBands = new HashMap<BandInformation, SVGArea>();
    }

    protected Logger getLog() {
        return log;
    }

    @Override
    public String getName() {
        return "Association renderlet";
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getDescription() {
        return ("Renderlet capable of creating GWAS trait-SNP association visualisations");
    }

    @Override
    public void render(RenderletNexus nexus, C context, E associationEntity) {
        getLog().trace("Association: " + associationEntity);
        if (!previousBandMapByContext.containsKey(context)) {
            previousBandMapByContext.put(context, sortBandsWithData(context));
        }

        try {
            BandInformation band = getBandInformation(context, associationEntity);
            if (!renderedBands.containsKey(band)) {
                // there is no other association in this chromosomal band yet - render
                getLog().trace("This is the first association for band '" + band.getBandName() + "', " +
                                       "rendering new line");

                StringBuilder svg = new StringBuilder();
                String transform = getTransformation(band.getChromosome());
                svg.append("<g ")
                        .append("id='").append(getAssociationID(context, associationEntity)).append("' ")
                        .append("transform='").append(transform).append("' ")
                        .append("class='gwas-trait'>");

                // get rendered svg location of the cytogenetic band
                SVGArea location = nexus.getLocationOfRenderedEntity(band);
                if (location != null) {
                    // starting x and y co-ords derived from cytogenetic band
                    double x1 = location.getX();
                    double y1 = location.getY() + (location.getHeight() / 2);

                    double dotRadius = 0.35 * location.getWidth();

                    // x2 and y2 mark the end of the horizontal line designating the snp location
                    double x2 = location.getWidth();
                    double y2 = 0;

                    // x3 and y3 mark the end of the line - may be fanned to avoid overlaps
                    double x3 = 0.75 * location.getWidth();
                    double y3;

                    // fanning algorithm - calculate diagonal part of the line, if necessary
                    Map<BandInformation, BandInformation> previousBandMap = previousBandMapByContext.get(context);
                    BandInformation previousBand = previousBandMap.get(band);
                    if (previousBand != null && band.getChromosome().equals(previousBand.getChromosome())) {
                        SVGArea previousLocation = getLocationOfPreviousAssociation(nexus, context, associationEntity);
                        if (previousLocation != null) {
                            double previousY = previousLocation.getY() + previousLocation.getHeight();

                            // fan up or down?
                            if (band.getBandName().contains("p")) {
                                // p arm - we need to know how many traits are in this band
                                int traitCount = getNumberOfTraitsInSameBand(context, associationEntity);
                                int rowCount = ((traitCount - 1) / 6) + 2;
                                double blockSize = rowCount * dotRadius;

                                if (y1 + blockSize > previousY) {
                                    // if blockSize takes us down so far it would overlap prevY, move up
                                    y3 = previousY - (y1 + blockSize);
                                }
                                else {
                                    // otherwise, line can be horizontal
                                    y3 = 0;
                                }
                            }
                            else {
                                // q arm - we need to know how many traits were in the previous band (ie. the one above)
                                int traitCount = getNumberOfTraitsInPreviousBand(context, associationEntity);
                                int rowCount = ((traitCount - 1) / 6) + 2;
                                double blockSize = rowCount * dotRadius;

                                if (previousY + blockSize > y1) {
                                    // if the previous blockSize takes us down so far it would overlap y, move down
                                    y3 = (previousY + blockSize) - y1;
                                }
                                else {
                                    // otherwise, line can be horizontal
                                    y3 = 0;
                                }
                            }
                        }
                        else {
                            // no previous location, so line can be horizontal
                            y3 = 0;
                        }
                    }
                    else {
                        // no previous band, or isn't in the same chromosome, so line can be horizontal
                        y3 = 0;
                    }

                    StringBuilder d = new StringBuilder();
                    d.append("m ");
                    d.append(Double.toString(x1)).append(",").append(Double.toString(y1));
                    d.append(" ");
                    d.append(Double.toString(x2)).append(",").append(Double.toString(y2));
                    d.append(" ");
                    d.append(Double.toString(x3)).append(",").append(Double.toString(y3));

                    svg.append("<path ")
                            .append("d='").append(d.toString()).append("' ")
                            .append("style='fill:none;stroke:#211c1d;stroke-width:1.1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none'")
                            .append(" />");
                    svg.append("</g>");

                    SVGArea currentArea = new SVGArea(x1, y1, x2 + x3, y2 + y3, transform, 0);
                    RenderingEvent<E> event =
                            new RenderingEvent<E>(associationEntity,
                                                  svg.toString(),
                                                  currentArea,
                                                  this);
                    nexus.renderingEventOccurred(event);

                    // add band to renderedBands set
                    renderedBands.put(band, currentArea);
                }
                else {
                    getLog().error("Unable to render association '" + associationEntity + "' - " +
                                           "no location for band '" + band.getBandName() + "'");
                }
            }
            else {
                // we've already rendered the required association line, so we don't need to do it again
                // but we do need to log the rendering event for this association individual
                getLog().trace("Already rendered an association line to band '" + band.getBandName() + "', " +
                                       "logging secondary event for association '" + associationEntity + "'");
                SVGArea area = renderedBands.get(band);
                nexus.renderingEventOccurred(new RenderingEvent<E>(associationEntity, "", area, this));
            }
        }
        catch (DataIntegrityViolationException e) {
            getLog().error("Cannot render association '" + associationEntity + "'", e);
        }
    }

    protected Map<BandInformation, BandInformation> getPreviousBandMap(C context) {
        return previousBandMapByContext.get(context);
    }

    /**
     * Fetches the known ID of the association that can be used to subsequently retrieve it based on user interactions
     *
     * @param context     the context
     * @param association the association to lookup the ID for
     * @return the ID of the association
     */
    protected abstract String getAssociationID(C context, E association);

    /**
     * Fetches the band information about the cytogenetic region the current association is located in
     *
     * @param context     the context
     * @param association the association to lookup band information for
     * @return the band information for this association
     * @throws DataIntegrityViolationException
     */
    protected abstract BandInformation getBandInformation(C context, E association)
            throws DataIntegrityViolationException;

    /**
     * For the given association, identifies the cytogenetic band it is located in, then identifies the total number of
     * traits located in the same cytogenetic band and returns the count
     *
     * @param context     the context
     * @param association the association to identify co-located traits for
     * @return the number of traits in the same cytogenetic region as this association
     * @throws DataIntegrityViolationException
     */
    protected abstract int getNumberOfTraitsInSameBand(C context, E association)
            throws DataIntegrityViolationException;

    /**
     * For the given association, identifies the previous cytogenetic band to the one this association is located in,
     * then identifies the total number of traits located in that cytogenetic band and returns the count
     *
     * @param context     the context
     * @param association the association to identify co-located traits for
     * @return the number of traits in the same cytogenetic region as this association
     * @throws DataIntegrityViolationException
     */
    protected abstract int getNumberOfTraitsInPreviousBand(C context, E association)
            throws DataIntegrityViolationException;

    protected abstract SVGArea getLocationOfPreviousAssociation(RenderletNexus nexus, C context, E association)
            throws DataIntegrityViolationException;

    protected abstract Map<BandInformation, BandInformation> sortBandsWithData(C context);

    private String getTransformation(String chromosomeName) {
        int position;
        if (chromosomeName.equals("X")) {
            position = 22;
        }
        else if (chromosomeName.equals("Y")) {
            position = 23;
        }
        else {
            position = Integer.parseInt(chromosomeName) - 1;
        }
        int height = SVGCanvas.canvasHeight;
        int width = SVGCanvas.canvasWidth;

        double chromWidth = (double) width / 12;
        double xCoordinate;
        double yCoordinate = 0;

        if (position < 12) {
            xCoordinate = position * chromWidth;
        }
        else {
            xCoordinate = (position - 12) * chromWidth;
            yCoordinate = (double) height / 2;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("translate(");
        builder.append(Double.toString(xCoordinate));
        builder.append(",");
        builder.append(Double.toString(yCoordinate));
        builder.append(")");

        return builder.toString();
    }
}