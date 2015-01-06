package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         Model of region cross reference table
 */

@Entity
@Table(name = "GWASREGIONXREF")
public class RegionXref {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "REGIONID")
    private Long regionID;

    @Column(name = "GWASSNPID")
    private Long snpID;


    // JPA no-args constructor
    public RegionXref() {
    }

    public RegionXref(Long regionID, Long associationID) {
        this.regionID = regionID;
        this.snpID = associationID;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegionID() {
        return regionID;
    }

    public void setRegionID(Long regionID) {
        this.regionID = regionID;
    }

    public Long getSnpID() {
        return snpID;
    }

    public void setSnpID(Long snpID) {
        this.snpID = snpID;
    }

    @Override
    public String toString() {
        return "RegionXref{" +
                "id=" + id +
                ", regionID=" + regionID +
                ", associationID='" + snpID + '\'' +
                '}';
    }
}
