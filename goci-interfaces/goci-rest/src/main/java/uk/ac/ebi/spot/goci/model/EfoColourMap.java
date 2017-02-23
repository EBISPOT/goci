package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dwelter on 16/02/17.
 */

public class EfoColourMap {

    private String uri, trait, parentUri, parent, colour;


    @JsonCreator
    public EfoColourMap(@JsonProperty("uri") String uri,
                        @JsonProperty("trait") String trait,
                        @JsonProperty("parentUri") String parentUri,
                        @JsonProperty("parent") String parent,
                        @JsonProperty("colour") String colour){
        this.uri = uri;
        this.trait = trait;
        this.parentUri = parentUri;
        this.parent = parent;
        this.colour = colour;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTrait() {
        return trait;
    }

    public void setTrait(String trait) {
        this.trait = trait;
    }

    public String getParentUri() {
        return parentUri;
    }

    public void setParentUri(String parentUri) {
        this.parentUri = parentUri;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
