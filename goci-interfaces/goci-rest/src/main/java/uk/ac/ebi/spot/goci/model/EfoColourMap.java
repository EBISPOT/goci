package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dwelter on 16/02/17.
 */

public class EfoColourMap {

    private String uri, trait, parentUri, parent, colour, colourLabel, message;


    @JsonCreator
    public EfoColourMap(@JsonProperty("uri") String uri,
                        @JsonProperty("trait") String trait,
                        @JsonProperty("parentUri") String parentUri,
                        @JsonProperty("parent") String parent,
                        @JsonProperty("colour") String colour,
                        @JsonProperty("colourLabel") String colourLabel,
                        @JsonProperty("message") String message){
        this.uri = uri;
        this.trait = trait;
        this.parentUri = parentUri;
        this.parent = parent;
        this.colour = colour;
        this.colourLabel = colourLabel;
        this.message = message;
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

    public String getColourLabel() {
        return colourLabel;
    }

    public void setColourLabel(String colourLabel) {
        this.colourLabel = colourLabel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
