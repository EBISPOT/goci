package uk.ac.ebi.spot.goci.ui.model;

import javax.persistence.*;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Model of region information associated with a SNP
 */
@Entity
@Table(name = "GWASREGION")
public class Region {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "REGION")
    private String region;

    // JPA no-args constructor
    public Region() {
    }

    public Region(String region) {
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", region='" + region + '\'' +
                '}';
    }
}
