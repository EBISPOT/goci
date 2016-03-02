package uk.ac.ebi.spot.goci.model;

/**
 * Created by Dani on 15/02/2016.
 */
public class TraitEntity {

    private String trait;
    private String efoTerm;
    private String uri;
    private String parentName;
    private String parentUri;

    public TraitEntity(String trait, String efoTerm, String uri){
        this.trait = trait;
        this.efoTerm = efoTerm;
        this.uri = uri;
    }

    public String getTrait() {
        return trait;
    }

    public String getEfoTerm() {
        return efoTerm;
    }

    public String getUri() {
        return uri;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentUri() {
        return parentUri;
    }

    public void setParentUri(String parentUri) {
        this.parentUri = parentUri;
    }
}
