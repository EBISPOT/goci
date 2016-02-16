package uk.ac.ebi.spot.goci.model;

/**
 * Created by Dani on 15/02/2016.
 */
public class Trait {

    private String trait;
    private String efoTerm;
    private String uri;
    private String parent;

    public Trait(String trait, String efoTerm, String uri){
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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
