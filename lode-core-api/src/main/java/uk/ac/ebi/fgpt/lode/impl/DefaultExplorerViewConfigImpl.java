package uk.ac.ebi.fgpt.lode.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import uk.ac.ebi.fgpt.lode.model.ExplorerViewConfiguration;
import uk.ac.ebi.fgpt.lode.model.LabeledResource;
import java.net.URI;

import java.util.*;

/**
 * @author Simon Jupp
 * @date 02/05/2013
 * Functional Genomics Group EMBL-EBI
 */
public class DefaultExplorerViewConfigImpl implements ExplorerViewConfiguration {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${lode.explorer.toprelationship}")
    private String topRelationships = null;

    @Value("${lode.explorer.ignore.relationship}")
    private String ignoreRelationships = null;

    @Value("${lode.explorer.ignore.types}")
    private String ignoreTypes = null;

    @Value("${lode.explorer.label}")
    private String labels = null;

    @Value("${lode.explorer.description}")
    private String descriptions = null;

    @Value("${lode.explorer.depict}")
    private String depict = null;

    @Value("${lode.explorer.ignore.blanknode}")
    private  boolean ignoreBlankNodes = false;

    @Value("${lode.explorer.max.objects}")
    private int objectMaxSample = -1;

    public int getObjectMaxSample() {
        return objectMaxSample;
    }

    public URI getDepictRelation() {
        return URI.create(depict);
    }

    public Set<URI> getLabelRelations() {
        HashSet<URI> labelProps = new HashSet<URI>();
        for (String s : labels.split(",")) {
            labelProps.add(URI.create(s));
        }
        return labelProps;
    }

    public Set<URI> getDescriptionRelations() {
        HashSet<URI> descriptionProps = new HashSet<URI>();
        for (String s : descriptions.split(",")) {
            descriptionProps.add(URI.create(s));
        }
        return descriptionProps;
    }

    public List<URI> getTopRelationships() {

        ArrayList<URI> labeledResources = new ArrayList<URI>();
        for (String s : topRelationships.split(",")) {
            labeledResources.add(URI.create(s));
        }
        return labeledResources;
    }

    public Set<URI> getIgnoreRelationships() {
        HashSet<URI> labeledResources = new HashSet<URI>();
        for (String s : ignoreRelationships.split(",")) {
            labeledResources.add(URI.create(s));
        }
        return labeledResources;
    }

    public Set<URI> getIgnoreTypes() {
        HashSet<URI> labeledResources = new HashSet<URI>();
        for (String s : ignoreTypes.split(",")) {
            labeledResources.add(URI.create(s));
        }
        return labeledResources;
    }

    public boolean ignoreBlankNodes() {
        return ignoreBlankNodes;
    }
}
