package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

/**
 * Created by emma on 19/12/14.
 *
 * @author emma
 *         <p>
 *         Model object representing a countryName
 */
@Entity
public class Country {
    @Id
    @GeneratedValue
    private Long id;

    private String majorArea;

    private String region;

    private String countryName;

    @ManyToMany(mappedBy = "countryOfOrigin")
    @JsonBackReference
    @RestResource(exported = false)
    private Collection<Ancestry> ancestriesOrigin;

    @ManyToMany(mappedBy = "countryOfRecruitment")
    @JsonBackReference
    @RestResource(exported = false)
    private Collection<Ancestry> ancestriesRecruitment;


    // JPA no-args constructor
    public Country() {
    }

    public Country(String majorArea, String region, String countryName,
                   Collection<Ancestry> ancestriesOrigin,
                   Collection<Ancestry> ancestriesRecruitment) {
        this.majorArea = majorArea;
        this.region = region;
        this.countryName = countryName;
        this.ancestriesOrigin = ancestriesOrigin;
        this.ancestriesRecruitment = ancestriesRecruitment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMajorArea() {
        return majorArea;
    }

    public void setMajorArea(String majorArea) {
        this.majorArea = majorArea;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", majorArea='" + majorArea + '\'' +
                ", region='" + region + '\'' +
                ", countryName='" + countryName + '\'' +
                '}';
    }

    public Collection<Ancestry> getAncestriesOrigin() {
        return ancestriesOrigin;
    }

    public void setAncestriesOrigin(Collection<Ancestry> ancestriesOrigin) {
        this.ancestriesOrigin = ancestriesOrigin;
    }

    public Collection<Ancestry> getAncestriesRecruitment() {
        return ancestriesRecruitment;
    }

    public void setAncestriesRecruitment(Collection<Ancestry> ancestriesRecruitment) {
        this.ancestriesRecruitment = ancestriesRecruitment;
    }
}

