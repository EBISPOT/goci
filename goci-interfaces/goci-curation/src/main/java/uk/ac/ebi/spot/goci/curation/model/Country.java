package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;

/**
 * Created by emma on 19/12/14.
 * @author emma
 *
 * Model object representing a country
 */
@Entity
@Table(name = "GWASCOUNTRIES")
public class Country {


    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name ="MAJORAREA")
    private String majorArea;

    @Column(name ="REGION")
    private String region;

    @Column(name="COUNTRY")
    private String country;

    // JPA no-args constructor
    public Country() {
    }

    public Country(String majorArea, String region, String country) {
        this.majorArea = majorArea;
        this.region = region;
        this.country = country;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", majorArea='" + majorArea + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}

