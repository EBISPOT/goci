package uk.ac.ebi.spot.goci.solr.api;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Dani on 27/11/2014.
 */
public class EFOAnnotation {

    @Field("id")
    private String id;

    @Field("uri_key")
    private int idKey;

    @Field("uri")
    private String uri;

    @Field("short_form")
    private String shortForm;

    @Field("label")
    private List<String> label;

    @Field("synonyms")
    private List<String> synonym;

    @Field("description")
    private List<String> description;

    public EFOAnnotation() {
    }

    public EFOAnnotation(String uri, String shortFrom, List<String> label, List<String> synonym, List<String> description) {
        this.uri = uri;
        this.shortForm = shortFrom;
        this.label = label;
        this.synonym = synonym;
        this.description = description;
    }

    public EFOAnnotation(String uri, String shortFrom, Set<String> label, Set<String> synonym, Set<String> description) {
        this.uri = uri;
        this.shortForm = shortFrom;
        this.label = new ArrayList<String>(label);
        this.synonym = new ArrayList<String>(synonym);
        this.description = new ArrayList<String>(description);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getShortForm() {
        return shortForm;
    }

    public void setShortForm(String shortFrom) {
        this.shortForm = shortFrom;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public List<String> getSynonym() {
        return synonym;
    }

    public void setSynonym(List<String> synonym) {
        this.synonym = synonym;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EFOAnnotation{" +
                "uri='" + uri + '\'' +
                ", shortFrom='" + shortForm + '\'' +
                ", label=" + label +
                ", synonym=" + synonym +
                ", description=" + description +
                '}';
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the idKey
     */
    public int getIdKey() {
        return idKey;
    }

    /**
     * @param idKey the idKey to set
     */
    public void setIdKey(int idKey) {
        this.idKey = idKey;
    }
}
