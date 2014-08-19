package uk.ac.ebi.fgpt.lode.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Simon Jupp
 * @date 02/05/2013
 * Functional Genomics Group EMBL-EBI
 *
 * This class is a wrapper for any resource that can have an optional label
 *
 */
public class LabeledResource {

    private String uri;
    private String label;
    private String description;

    public LabeledResource(String uri, String label, String description) {
        this.uri = uri;
        this.label = label;
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "LabeledResource{" +
                "uri='" + uri + '\'' +
                ", label='" + label + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


    public int hashCode() {
          return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
              // if deriving: appendSuper(super.hashCode()).
              append(uri).
              append(label).
              append(description).
              toHashCode();
      }

      public boolean equals(Object obj) {
          if (obj == null)
              return false;
          if (obj == this)
              return true;
          if (!(obj instanceof LabeledResource))
              return false;

          LabeledResource rhs = (LabeledResource) obj;
          return new EqualsBuilder().
              // if deriving: appendSuper(super.equals(obj)).
              append(uri, rhs.uri).
              append(label, rhs.label).
              append(description, rhs.description).
              isEquals();
      }

}
