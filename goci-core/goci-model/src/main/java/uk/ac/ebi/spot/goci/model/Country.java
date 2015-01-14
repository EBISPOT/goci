package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by emma on 19/12/14.
 *
 * @author emma
 *         <p>
 *         Model object representing a country
 */
@Entity
public class Country {
    @Id
    @GeneratedValue
    private Long id;

    private String majorArea;

    private String region;

    private String name;

    // JPA no-args constructor
    public Country() {
    }

    public Country(String majorArea, String region, String name) {
        this.majorArea = majorArea;
        this.region = region;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", majorArea='" + majorArea + '\'' +
                ", region='" + region + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

