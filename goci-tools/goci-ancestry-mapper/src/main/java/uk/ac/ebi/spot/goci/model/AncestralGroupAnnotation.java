package uk.ac.ebi.spot.goci.model;

/**
 * Created by dwelter on 06/10/15.
 */
public class AncestralGroupAnnotation {

    private Long id;

    private String ethnicGroup;

    private String ontologyURI;


    public AncestralGroupAnnotation(Long id, String ethnicGroup, String ontologyURI){
        this.id = id;
        this.ethnicGroup = ethnicGroup;
        this.ontologyURI = ontologyURI;
    }


    public Long getId() {
        return id;
    }

    public String getEthnicGroup() {
        return ethnicGroup;
    }

    public String getOntologyURI() {
        return ontologyURI;
    }
}
