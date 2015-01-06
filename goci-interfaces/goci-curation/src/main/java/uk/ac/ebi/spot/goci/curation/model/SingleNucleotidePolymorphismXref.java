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
public class SingleNucleotidePolymorphismXref {

    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    private Long snpId;

    private Long associationId;

    // JPA no-args constructor
    public SingleNucleotidePolymorphismXref() {
    }

    public SingleNucleotidePolymorphismXref(Long snpId, Long associationId) {
        this.snpId = snpId;
        this.associationId = associationId;
    }

    public Long getId() {
        return id;
    }

    public Long getSnpId() {
        return snpId;
    }

    public Long getAssociationId() {
        return associationId;
    }

    @Override
    public String toString() {
        return "SingleNucleotidePolymorphismXref{" +
                "id=" + id +
                ", snpId='" + snpId + '\'' +
                ", associationId='" + associationId + '\'' +
                '}';
    }
}
