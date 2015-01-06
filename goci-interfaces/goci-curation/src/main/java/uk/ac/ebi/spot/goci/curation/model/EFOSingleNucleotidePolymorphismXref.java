package uk.ac.ebi.spot.goci.curation.model;

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
public class EFOSingleNucleotidePolymorphismXref {

    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    private Long traitId;

    private Long associationId;

    // JPA no-args constructor
    public EFOSingleNucleotidePolymorphismXref() {
    }

    public EFOSingleNucleotidePolymorphismXref(Long traitId, Long associationId) {
        this.traitId = traitId;
        this.associationId = associationId;
    }

    public Long getId() {
        return id;
    }

    public Long getTraitId() {
        return traitId;
    }

    public Long getAssociationId() {
        return associationId;
    }

    @Override
    public String toString() {
        return "EFOSingleNucleotidePolymorphismXref{" +
                "id=" + id +
                ", traitId=" + traitId +
                ", associationId=" + associationId +
                '}';
    }
}
