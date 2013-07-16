package uk.ac.ebi.fgpt.lode.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Simon Jupp
 * @date 02/05/2013
 * Functional Genomics Group EMBL-EBI
 *
 * This object wraps a set of statements about a particular resource.
 * The primary function of this object it to serve the View component
 * for rendering the data.
 * It captures the relation URI, and it's label. It has a collection of
 * objects related by the relation, and includes optional information
 * about the object type, including the object label and description.
 *
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
public class RelatedResourceDescription {

    String propertyUri;
    String propertyLabel;
    Set<LabeledResource> objectTypes = new HashSet<LabeledResource>();
    Set<LabeledResource> objectInstance = new HashSet<LabeledResource>();

    public String getPropertyUri() {
        return propertyUri;
    }

    public void setPropertyUri(String propertyUri) {
        this.propertyUri = propertyUri;
    }

    public String getPropertyLabel() {
        return propertyLabel;
    }

    public void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }

    public Set<LabeledResource> getRelatedObjectTypes() {
        return objectTypes;
    }

    public Set<LabeledResource> getRelatedObjects() {
        return objectInstance;
    }

    @Override
    public String toString() {
        return "RelatedResourceDescription{" +
                "propertyUri='" + propertyUri + '\'' +
                ", propertyLabel='" + propertyLabel + '\'' +
                ", objectTypes=" + objectTypes +
                ", objectInstance=" + objectInstance +
                '}';
    }
}
