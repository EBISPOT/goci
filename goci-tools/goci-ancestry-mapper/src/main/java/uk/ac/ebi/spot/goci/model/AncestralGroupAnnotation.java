package uk.ac.ebi.spot.goci.model;

/**
 * Created by dwelter on 06/10/15.
 */
public class AncestralGroupAnnotation {

    private Long id;

    private String ancestralGroup;

    private String ontologyLabel;

    private String ontologyURI;


    public AncestralGroupAnnotation(Long id, String ancestralGroup, String ontologyLabel, String ontologyURI) {
        this.id = id;
        this.ancestralGroup = ancestralGroup;
        this.ontologyLabel = ontologyLabel;
        this.ontologyURI = ontologyURI;
    }


    public Long getId() {
        return id;
    }

    public String getAncestralGroup() {
        return ancestralGroup;
    }

    public String getOntologyURI() {
        return ontologyURI;
    }

    public String getOntologyLabel() {
        return ontologyLabel;
    }
}
