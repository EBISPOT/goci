package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p/>
 *         Model of snp cross reference table
 */
@Entity
@Table(name = "GWASSNPXREF")
public class SingleNucleotidePolymorphismXref {

    @Id
    @GeneratedValue
    @NotNull
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

    public Long getSnpID() {
        return snpID;
    }

    public Long getAssociationID() {
        return associationID;
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
