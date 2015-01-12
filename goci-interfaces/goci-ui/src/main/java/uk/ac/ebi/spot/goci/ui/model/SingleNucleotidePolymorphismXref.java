package uk.ac.ebi.spot.goci.ui.model;

import javax.persistence.*;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p>
 *         Model of snp cross reference table
 */
@Entity
@Table(name = "GWASSNPXREF")
public class SingleNucleotidePolymorphismXref {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "SNPID")
    private Long snpID;

    @Column(name = "GWASSTUDIESSNPID")
    private Long associationID;

    // JPA no-args constructor
    public SingleNucleotidePolymorphismXref() {
    }

    public SingleNucleotidePolymorphismXref(Long snpID, Long associationID) {
        this.snpID = snpID;
        this.associationID = associationID;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSnpID() {
        return snpID;
    }

    public void setSnpID(Long snpID) {
        this.snpID = snpID;
    }

    public Long getAssociationID() {
        return associationID;
    }

    public void setAssociationID(Long associationID) {
        this.associationID = associationID;
    }

    @Override
    public String toString() {
        return "SingleNucleotidePolymorphismXref{" +
                "id=" + id +
                ", snpID='" + snpID + '\'' +
                ", associationID='" + associationID + '\'' +
                '}';
    }
}
