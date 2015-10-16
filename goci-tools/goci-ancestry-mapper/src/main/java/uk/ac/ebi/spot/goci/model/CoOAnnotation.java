package uk.ac.ebi.spot.goci.model;

/**
 * Created by dwelter on 06/10/15.
 */
public class CoOAnnotation {

    private Long id;

    private String countryOfOrigin;

    private String ontologyURI;

    public CoOAnnotation(Long id, String coo, String uri){
        this.id = id;
        this.countryOfOrigin = coo;
        this.ontologyURI = uri;
    }

    public String getOntologyURI() {
        return ontologyURI;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public Long getId() {
        return id;
    }


}
