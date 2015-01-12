package uk.ac.ebi.spot.goci.ui.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by emma on 08/12/14.
 *
 * @author emma
 *         <p/>
 *         Model of EFO assocaition cross reference table
 */
@Entity
@Table(name = "GWASEFOSNPXREF")
public class EFOSingleNucleotidePolymorphismXref {

    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID")
    private Long id;

    @Column(name = "TRAITID")
    private Long traitID;

    @Column(name = "GWASSTUDIESSNPID")
    private Long associationID;

    // JPA no-args constructor
    public EFOSingleNucleotidePolymorphismXref() {
    }

    public EFOSingleNucleotidePolymorphismXref(Long traitID, Long associationID) {
        this.traitID = traitID;
        this.associationID = associationID;
    }

    public Long getId() {
        return id;
    }

    public Long getTraitID() {
        return traitID;
    }

    public Long getAssociationID() {
        return associationID;
    }

    @Override
    public String toString() {
        return "EFOSingleNucleotidePolymorphismXref{" +
                "id=" + id +
                ", traitID=" + traitID +
                ", associationID=" + associationID +
                '}';
    }
}
