package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

/**
 * Created by dwelter on 08/03/16.
 */
@Entity
public class Platform {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String manufacturer;

    @JsonIgnore
    @ManyToMany(mappedBy = "platforms")
    private Collection<Study> studies;

    // JPA no-args constructor
    public Platform() {
    }

    public Platform(String manufacturer,
                    Collection<Study> studies) {
        this.manufacturer = manufacturer;
        this.studies = studies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Collection<Study> getStudies() {
        return studies;
    }

    public void setStudies(Collection<Study> studies) {
        this.studies = studies;
    }

    @Override
    public String toString() {
        return manufacturer;
    }
}
