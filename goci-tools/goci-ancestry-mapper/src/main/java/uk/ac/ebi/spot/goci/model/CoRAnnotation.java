package uk.ac.ebi.spot.goci.model;

/**
 * Created by dwelter on 06/10/15.
 */
public class CoRAnnotation {

    private Long id;

    private String countryOfRecruitment;

    private String ontologyURI;

    public CoRAnnotation(Long id, String cor, String uri) {
        this.id = id;
        this.countryOfRecruitment = cor;
        this.ontologyURI = uri;
    }

    public Long getId() {
        return id;
    }

    public String getCountryOfRecruitment() {
        return countryOfRecruitment;
    }

    public String getOntologyURI() {
        return ontologyURI;
    }

}
