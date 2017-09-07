package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

/**
 * Created by dwelter on 21/06/17.
 */

@Entity
public class GenotypingTechnology {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String genotypingTechnology;

    @ManyToMany(mappedBy = "genotypingTechnologies")
    @JsonBackReference
    @RestResource(exported = false)
    private Collection<Study> studies;

    // JPA no-args constructor
    public GenotypingTechnology() {
    }

    public GenotypingTechnology(String genotypingTechnology,
                          Collection<Study> studies) {
        this.genotypingTechnology = genotypingTechnology;
        this.studies = studies;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGenotypingTechnology() {
        return genotypingTechnology;
    }

    public void setGenotypingTechnology(String genotypingTechnology) {
        this.genotypingTechnology = genotypingTechnology;
    }

//    @JsonIgnore
    public Collection<Study> getStudies() {
        return studies;
    }

    public void setStudies(Collection<Study> studies) {
        this.studies = studies;
    }
}
