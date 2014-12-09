package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p/>
 *         Model of region cross reference table
 */

@Entity
@Table(name = "GWASREGIONXREF")
public class RegionXref {

    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID")
    private Long id;

    @Column(name = "REGIONID")
    private Long regionID;

    //@Column(name = "GWASSTUDIESSNPID")
   // private Long associationID;

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

    public Long getRegionID() {
        return regionID;
    }

    public Long getAssociationID() {
        return snpID;
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
